package com.rws.lt.lc.blueprint.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rws.lt.lc.blueprint.exception.NotAuthorizedException;
import com.rws.lt.lc.blueprint.security.dto.JWK;
import com.rws.lt.lc.blueprint.security.dto.Principal;
import com.rws.lt.lc.blueprint.security.util.LRUMap;
import com.rws.lt.lc.blueprint.service.RemotePublicApiService;
import com.rws.lt.lc.blueprint.util.LocalContextKeys;
import com.rws.lt.lc.blueprint.util.RequestLocalContext;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.*;

import static java.util.Objects.isNull;

@Component
public class JWSExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWSExtractor.class);

    public static final Integer DEFAULT_JWS_VERIFY_LEEWAY_SECONDS = 60;
    public static final Integer DEFAULT_JWS_KEY_CACHE_MAX_ENTRIES = 100;

    public static final String JWS_KID_HEADER = "kid";
    public static final String JWS_ISSUER_HEADER = "iss";
    public static final String JWS_ISSUED_AT_HEADER = "iat";
    public static final String JWS_EXPIRES_AT_HEADER = "exp";
    public static final String JWS_ACCOUNT_ID_HEADER = "aid";
    public static final String JWS_AUDIENCE_HEADER = "aud";
    public static final String LC_SIGNATURE_HEADER = "x-lc-signature";

    static final String ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE = "Authentication failed.";
    public static final String PRINCIPAL_TYPE_LC = "LC";
    protected static final String EMPTY_PAYLOAD_HASH = "47DEQpj8HBSa-_TImW-5JCeuQeRkm5NMpJWZG3hSuFU";

    @Value("${authentication.jws.verify.leewayInSeconds:60}")
    private Integer jwsVerifyLeewayInSeconds = DEFAULT_JWS_VERIFY_LEEWAY_SECONDS;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${authentication.lc-issuer}")
    private String lcIssuer;

    private Integer jwsKeyCacheMaxEntries = DEFAULT_JWS_KEY_CACHE_MAX_ENTRIES;

    private final KeyFactory keyFactory;

    private Map<String, JWK> cachedJWSKeys;

    private final RemotePublicApiService remotePublicApiService;

    @Autowired
    public JWSExtractor(RemotePublicApiService remotePublicApiService) {
        this.remotePublicApiService = remotePublicApiService;
        try {
            this.keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unsupported key algorithm.", e);
        }
        this.cachedJWSKeys = newJWSKeyCache(jwsKeyCacheMaxEntries);
    }

    public void extract(HttpServletRequest httpRequest) throws NotAuthorizedException {
        String jws = httpRequest.getHeader(LC_SIGNATURE_HEADER);

        if (jws != null && !jws.isEmpty()) {
            String requestBodyAsString = getRequestBody(httpRequest);
            extract(jws, requestBodyAsString);
        } else {
            LOGGER.trace("No JWS found, nothing to do.");
            throw new NotAuthorizedException(ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE);
        }
    }

    @SneakyThrows
    protected String getRequestBody(HttpServletRequest request) {
        return IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
    }

    private void extract(String jws, String bodyAsString) throws NotAuthorizedException {
        try {
            Principal lcPrincipal = verifyAndBuildPrincipal(jws, bodyAsString);
            populateLocalContext(lcPrincipal);
        } catch (IllegalJWSException illegalJwsEx) {
            LOGGER.error("Illegal JWS.", illegalJwsEx);
            // Fail fast if JWS invalid
            throw new NotAuthorizedException(ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE);
        }
    }

    private void populateLocalContext(Principal principal) {
        if (principal.getActiveAccountId() != null && !principal.getActiveAccountId().isEmpty()) {
            LOGGER.trace("Putting active account id '{}' in local context.", principal.getActiveAccountId());
            RequestLocalContext.putInLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID, principal.getActiveAccountId());
        }
        LOGGER.debug("Local context after principal extraction: {}.", RequestLocalContext.getLocalContextMap());
    }

    private Principal verifyAndBuildPrincipal(String jws, String bodyAsString) {
        LOGGER.debug("Got JWS '{}'. Decoding", jws);
        DecodedJWT decodedJws;
        try {
            jws = jws.replace("..", ".e30.");
            decodedJws = JWT.decode(jws);
        } catch (JWTDecodeException decodeEx) {
            throw new IllegalJWSException(String.format("Unable to decode JWS '%s'.", jws), decodeEx);
        }

        String issuer = decodedJws.getHeaderClaim(JWS_ISSUER_HEADER).asString();
        String issuedAt = formatISODate(decodedJws.getHeaderClaim(JWS_ISSUED_AT_HEADER).asDate());
        String expiresAt = formatISODate(decodedJws.getHeaderClaim(JWS_EXPIRES_AT_HEADER).asDate());
        LOGGER.debug("JWS issued by '{}' at '{}', expires at '{}'.", issuer, issuedAt, expiresAt);

        String accountId = extractAndValidateAccountId(decodedJws);
        validateIssuer(decodedJws);
        validateAudience(decodedJws);
        validateIssuedAt(decodedJws);
        validateExpiration(decodedJws);
        String keyId = decodedJws.getKeyId();
        RSAPublicKey publicKey = getJWSPublicKey(keyId);
        LOGGER.debug("Verifying JWS with key '{}'.", keyId);
        try {
            verify(publicKey, decodedJws.getHeader(), bodyAsString, decodedJws.getSignature());
        } catch (Exception e) {
            throw new IllegalJWSException(String.format("Verification of JWS '%s' signature with key '%s' failed.",
                    jws, keyId), e);
        }

        return Principal.builder().
                activeAccountId(accountId).
                build();
    }

    private void verify(PublicKey publicKey, String header, String payload, String signature) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        String data = header + "." + generatePayloadHashCode(payload);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        byte[] decodedSignature = Base64.decodeBase64(signature);
        if (!sig.verify(decodedSignature)) {
            throw new IllegalJWSException("Verification of JWS signature failed.");
        }
    }

    @SneakyThrows
    private static String generatePayloadHashCode(String bodyAsString) {
        if (StringUtils.isEmpty(bodyAsString)) {
            return EMPTY_PAYLOAD_HASH;
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] payloadHash = digest.digest(bodyAsString.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64URLSafeString(payloadHash);
    }

    private void validateExpiration(DecodedJWT decodedJws) {
        Instant expiryTime = Instant.ofEpochSecond(decodedJws.getHeaderClaim(JWS_EXPIRES_AT_HEADER).asLong() + jwsVerifyLeewayInSeconds);
        Instant currentTime = Instant.now();
        if (currentTime.isAfter(expiryTime)) {
            throw new IllegalJWSException("JWS has expired");
        }
    }

    private void validateIssuedAt(DecodedJWT decodedJws) {
        if (Optional.ofNullable(decodedJws.getHeaderClaim(JWS_ISSUED_AT_HEADER)).isPresent()) {
            Instant issuedAtTime = Instant.ofEpochSecond(decodedJws.getHeaderClaim(JWS_ISSUED_AT_HEADER).asLong() - jwsVerifyLeewayInSeconds);
            Instant currentTime = Instant.now();
            if (issuedAtTime.isAfter(currentTime)) {
                throw new IllegalJWSException("JWS was issued in the future");
            }
        }
    }

    private void validateAudience(DecodedJWT decodedJws) {
        String audience = decodedJws.getHeaderClaim(JWS_AUDIENCE_HEADER).asString();
        if (isNull(audience) || isNull(baseUrl) || !baseUrl.equalsIgnoreCase(audience)) {
            throw new IllegalJWSException(String.format("JWS audience of '%s' does not match intended '%s' audience.",
                    audience, baseUrl));
        }
    }

    private void validateIssuer(DecodedJWT decodedJws) {
        String issuer = decodedJws.getHeaderClaim(JWS_ISSUER_HEADER).asString();
        if (!lcIssuer.equalsIgnoreCase(issuer)) {
            throw new IllegalJWSException(String.format("JWS issuer of '%s' does not match intended '%s' issuer.",
                    issuer, lcIssuer));
        }
    }

    private String extractAndValidateAccountId(DecodedJWT decodedJws) {
        String accountId = decodedJws.getHeaderClaim(JWS_ACCOUNT_ID_HEADER).asString();
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalJWSException(String.format("JWS must contain '%s' header", JWS_ACCOUNT_ID_HEADER));
        }

        return accountId;
    }

    private String formatISODate(Date date) {
        return date != null ? ISODateTimeFormat.dateTime().withZoneUTC().print(new DateTime(date)) : null;
    }

    private RSAPublicKey getJWSPublicKey(String keyId) {
        JWK jwk = getJWSKey(keyId);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(jwk.getNInt(), jwk.getEInt());
        try {
            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalJWSException(String.format("Expected valid JWS key (%s) but key components invalid.", jwk), e);
        }
    }

    private JWK getJWSKey(String keyId) {
        if (keyId == null || keyId.isEmpty()) {
            LOGGER.debug("No key id provided.");
            throw new IllegalJWSException("JWS key id not provided.");
        }
        LOGGER.debug("Looking up JWS key '{}'.", keyId);
        JWK jwk = cachedJWSKeys.get(keyId);
        if (jwk == null) {
            LOGGER.info("JWS key '{}' not found in cache. Making remote call to get JWS keys.", keyId);
            JWK remoteJWK = remotePublicApiService.getPublicKeyById(keyId);

            LOGGER.debug("Remote call for JWS keys returned: {}.", remoteJWK);
            cachedJWSKeys.put(remoteJWK.getKid(), remoteJWK);
            LOGGER.debug("Cache after JWS keys remote call: {}.", cachedJWSKeys);
            return remoteJWK;
        }
        LOGGER.debug("Got JWS key ({}).", jwk);
        return jwk;
    }

    public void setJwsVerifyLeewayInSeconds(Integer jwsVerifyLeewayInSeconds) {
        this.jwsVerifyLeewayInSeconds = jwsVerifyLeewayInSeconds;
    }

    // This will reset the key cache
    @Value("${authentication.jws.keyCache.maxEntries:100}")
    public void setJwsKeyCacheMaxEntries(Integer jwsKeyCacheMaxEntries) {
        this.jwsKeyCacheMaxEntries = jwsKeyCacheMaxEntries;
        this.cachedJWSKeys = newJWSKeyCache(jwsKeyCacheMaxEntries);
    }

    public Map<String, JWK> getCachedJWSKeys() {
        return new LinkedHashMap<>(cachedJWSKeys);
    }

    private static Map<String, JWK> newJWSKeyCache(int maxEntries) {
        return Collections.synchronizedMap(new LRUMap<>(maxEntries));
    }

    public static class IllegalJWSException extends RuntimeException {

        public IllegalJWSException(String message) {
            super(message);
        }

        public IllegalJWSException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}