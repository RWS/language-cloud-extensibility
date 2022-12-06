package com.rws.lt.lc.mtsampleaddon.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rws.lt.lc.mtsampleaddon.exception.NotAuthorizedException;
import com.rws.lt.lc.mtsampleaddon.security.dto.JWK;
import com.rws.lt.lc.mtsampleaddon.service.RemotePublicApiService;
import com.rws.lt.lc.mtsampleaddon.util.LocalContextKeys;
import com.rws.lt.lc.mtsampleaddon.util.RequestLocalContext;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

import static com.rws.lt.lc.mtsampleaddon.security.JWSExtractor.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JWSExtractorTest {

    private static final String JWS_KEY_D = "AR1RYw8MDPhLuUdyGZf2zz8D1czYbiA1eX2ymAlVZCF8SUdAnfAoKvfe3kHeKo6QS2ufbO6a1rM0gMiIkq77OQ==";
    private static final String JWS_KEY_N = "AK8pcJOaxbBnkAHyHOC64DSwPeQtkhe4io8lgCTKPQ_kd_0wx6o-wWUQlm3UKAdNGd4DT039a-19BYDRQkyTKEs=";
    private static final String JWS_KEY_E = "AQAB";
    private static final String JWS_KEY_ID = "keyId";

    private static final String JWS_KEY_2_N = "AKSAJPkZaJxpwN2dHjwYrCdNJQcZHJWPnVgA6FC0FK_kdJXoRypl1m7fKDTtNA8PchSWj9RxZNGimHEzwlXZ7xk=";
    private static final String JWS_KEY_2_E = "AQAB";
    private static final String JWS_KEY_2_ID = "key2Id";

    private static final String JWS_INVALID_KEY_N = "Fw==";
    private static final String JWS_INVALID_KEY_E = "CNC23Lcb1NB_k6Yy3biPmE8FHhxbXhfhhxzF3nFcUAAAAAAAAAAAAAAAAA==";
    private static final String JWS_INVALID_KEY_ID = "invalidKeyId";

    private static final Integer JWS_VERIFY_LEEWAY_SECONDS = 30;
    public static final String TEST_ACCOUNT_ID = "accountId";

    private static final String MOCK_BASE_URL = "https://www.mt-sample-addon.rws.com/";
    private static final String JWS_ISSUER_LC = "https://test-lc-issuer.rws.com/";

    private static Algorithm algorithm = null;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RemotePublicApiService remotePublicApiService;

    @InjectMocks
    private JWSExtractor jwsExtractor;


    @Before
    public void init() throws Exception {
        RequestLocalContext.clean();
        ReflectionTestUtils.setField(jwsExtractor, "baseUrl", MOCK_BASE_URL);
        ReflectionTestUtils.setField(jwsExtractor, "lcIssuer", JWS_ISSUER_LC);
        algorithm = Algorithm.RSA256(getJWSPublicKey(), getJWSPrivateKey());
    }

    @Test
    public void extractMissingJWSShouldThrowAuthError() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        doReturn(null).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractEmptyJWSShouldThrowAuthError() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        doReturn("").when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_emptyAccountIdJWS() throws Exception {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, "");
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.seconds(60)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_nullAccountIdJWS() throws Exception {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.seconds(60)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);
        try {
            jwsExtractor.extract(httpRequest);
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());

    }

    @Test
    public void extractJWS_exerciseJWSKeyCache() throws Exception {

        doReturn(getJwk()).when(remotePublicApiService).getPublicKeyById(JWS_KEY_ID);

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.seconds(60)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.extract(httpRequest);

        String accountId = RequestLocalContext.getActiveAccountId();
        assertThat(accountId, is(TEST_ACCOUNT_ID));

        Map<String, JWK> cachedJWSKeys = jwsExtractor.getCachedJWSKeys();
        assertThat(cachedJWSKeys.size(), equalTo(1));
        List<JWK> keys = new ArrayList<>(cachedJWSKeys.values());
        assertThat(keys.get(0).getKid(), equalTo(JWS_KEY_ID));

        accountId = RequestLocalContext.getActiveAccountId();
        jwsExtractor.extract(httpRequest);

        assertThat(accountId, is(TEST_ACCOUNT_ID));
    }

    @Test
    public void extractJWS_setJWSKeyCacheMaxEntriesClearsCache() throws Exception {

        doReturn(getJwk()).when(remotePublicApiService).getPublicKeyById(JWS_KEY_ID);

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.seconds(60)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.extract(httpRequest);

        String accountId = RequestLocalContext.getActiveAccountId();
        assertThat(accountId, is(TEST_ACCOUNT_ID));

        Map<String, JWK> cachedJWSKeys = jwsExtractor.getCachedJWSKeys();
        assertThat(cachedJWSKeys.size(), equalTo(1));

        jwsExtractor.setJwsKeyCacheMaxEntries(5);
        cachedJWSKeys = jwsExtractor.getCachedJWSKeys();
        assertThat(cachedJWSKeys.size(), equalTo(0));

    }

    @Test
    public void extractJWS_jwsIssuedInFuture_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.plus(Period.minutes(5)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.minutes(6)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.setJwsVerifyLeewayInSeconds(0);
        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_jwsIssuedInFutureTimeInLeeway_accountIdIsAddedToContext() throws Exception {
        doReturn(getJwk()).when(remotePublicApiService).getPublicKeyById(JWS_KEY_ID);

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.plus(Period.seconds(15)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.seconds(60)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.setJwsVerifyLeewayInSeconds(JWS_VERIFY_LEEWAY_SECONDS);

        jwsExtractor.extract(httpRequest);

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, equalTo(TEST_ACCOUNT_ID));
    }

    @Test
    public void extractJWS_jwsExpired_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.minutes(3)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.minutes(2)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.setJwsVerifyLeewayInSeconds(0);
        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }


    @Test
    public void extractJWS_invalidAudience_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, "invalid_audience");
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.minutes(3)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.minutes(2)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.setJwsVerifyLeewayInSeconds(0);
        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }


    @Test
    public void extractJWS_audienceNotSetInJWS_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.minutes(3)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.minutes(2)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.setJwsVerifyLeewayInSeconds(0);
        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }


    @Test
    public void extractJWS_addonNameNotSetInContextForAudienceValidation_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.minutes(3)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.minutes(2)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.setJwsVerifyLeewayInSeconds(0);
        try {
            ReflectionTestUtils.setField(jwsExtractor, "baseUrl", null);
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }


    @Test
    public void extractJWS_invalidIssuer_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, "invalid_issuer");
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.minutes(3)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.plus(Period.minutes(2)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.setJwsVerifyLeewayInSeconds(0);
        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_jwsExpiredTimeInLeeway_accountIdIsAddedToContext() throws Exception {
        doReturn(getJwk()).when(remotePublicApiService).getPublicKeyById(JWS_KEY_ID);

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        jwsExtractor.extract(httpRequest);

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, equalTo(TEST_ACCOUNT_ID));
    }

    @Test
    public void extractJWS_invalidSignature_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        doReturn(getJwk2()).when(remotePublicApiService).getPublicKeyById(JWS_KEY_2_ID);

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_2_ID); // Verify with wrong key
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_noSignature_authExceptionThrownAndNoFieldsAreAddedToContext() throws IOException {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_differentAlgorithm_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        doReturn(getJwk()).when(remotePublicApiService).getPublicKeyById(JWS_KEY_ID);

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = signWithDifferentAlgo(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_invalidKeyComponents_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        doReturn(getJwkInvalid()).when(remotePublicApiService).getPublicKeyById(JWS_INVALID_KEY_ID);

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_INVALID_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_noJwsKey_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_emptyJwsKeyId_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, "");
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_jwsKeyNotFound_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        doThrow(new IllegalJWSException("invalid")).when(remotePublicApiService).getPublicKeyById("noSuchId");

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, "noSuchId");
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign(EMPTY_PAYLOAD_HASH, objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_wrongPayloadHash_authExceptionThrownAndNoFieldsAreAddedToContext() throws Exception {
        doReturn(getJwk()).when(remotePublicApiService).getPublicKeyById(JWS_KEY_ID);

        DateTime now = DateTime.now();
        Map<String, Object> jwsHeaders = new LinkedHashMap<>();
        jwsHeaders.put(JWSExtractor.JWS_KID_HEADER, JWS_KEY_ID);
        jwsHeaders.put(JWSExtractor.JWS_ACCOUNT_ID_HEADER, TEST_ACCOUNT_ID);
        jwsHeaders.put(JWSExtractor.JWS_AUDIENCE_HEADER, MOCK_BASE_URL);
        jwsHeaders.put(JWSExtractor.JWS_ISSUER_HEADER, JWS_ISSUER_LC);
        jwsHeaders.put(JWSExtractor.JWS_ISSUED_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS + 60)).withMillisOfSecond(0).toDate().getTime() / 1000);
        jwsHeaders.put(JWSExtractor.JWS_EXPIRES_AT_HEADER, now.minus(Period.seconds(JWS_VERIFY_LEEWAY_SECONDS - 1)).withMillisOfSecond(0).toDate().getTime() / 1000);

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        String jws = sign("someRandomStringAsPayloadHash", objectMapper.writeValueAsString(jwsHeaders));
        doReturn(jws).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_invalidJWSFormat_authExceptionThrownAndNoFieldsAreAddedToContext() throws IOException {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        InputStream targetStream = new ByteArrayInputStream("".getBytes());
        doReturn(new DelegatingServletInputStream(targetStream)).when(httpRequest).getInputStream();
        doReturn("invalid").when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
            fail("Expected auth exception.");
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_noJWS_noFieldsAreAddedToContext() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        doReturn(null).when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @Test
    public void extractJWS_emptyJWS_noFieldsAreAddedToContext() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        doReturn("").when(httpRequest).getHeader(LC_SIGNATURE_HEADER);

        try {
            jwsExtractor.extract(httpRequest);
        } catch (NotAuthorizedException expected) {
            assertThat(expected.getMessage(), equalTo(JWSExtractor.ILLEGAL_JWS_AUTH_EXCEPTION_MESSAGE));
            assertThat(expected.getDetails().size(), equalTo(0));
        }

        String accountId = (String) RequestLocalContext.getFromLocalContext(LocalContextKeys.ACTIVE_ACCOUNT_ID);
        assertThat(accountId, nullValue());
    }

    @After
    public void cleanUp() {
        RequestLocalContext.clean();
    }

    private RSAPrivateKey getJWSPrivateKey() throws Exception {
        BigInteger nInt = new BigInteger(Base64.getUrlDecoder().decode(JWS_KEY_N));
        BigInteger dInt = new BigInteger(Base64.getUrlDecoder().decode(JWS_KEY_D));

        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(nInt, dInt);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);
    }


    private RSAPublicKey getJWSPublicKey() throws Exception {
        BigInteger nInt = new BigInteger(Base64.getUrlDecoder().decode(JWS_KEY_N));
        BigInteger eInt = new BigInteger(Base64.getUrlDecoder().decode(JWS_KEY_E));

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(nInt, eInt);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
    }

    private String sign(String payloadHash, String headerJson) throws SignatureGenerationException {
        return sign(payloadHash, headerJson, algorithm);
    }

    private String signWithDifferentAlgo(String payloadHash, String headerJson) throws Exception {
        Algorithm differentAlgo = Algorithm.HMAC256(getJWSPublicKey().getEncoded());

        return sign(payloadHash, headerJson, differentAlgo);
    }

    private String sign(String payloadHash, String headerJson, Algorithm algorithm) {
        String header = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(headerJson.getBytes(StandardCharsets.UTF_8));
        String content = String.format("%s.%s", header, payloadHash);

        byte[] signatureBytes = algorithm.sign(content.getBytes(StandardCharsets.UTF_8));
        String signature = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString((signatureBytes));

        return String.format("%s..%s", header, signature);
    }

    private JWK getJwk() {
        JWK firstJWK = new JWK();
        firstJWK.setKid(JWS_KEY_ID);
        firstJWK.setE(JWS_KEY_E);
        firstJWK.setN(JWS_KEY_N);
        firstJWK.setAlg(JWK.RSA_ALGORITHM);
        firstJWK.setKty(JWK.RSA_KEY_TYPE);
        firstJWK.setUse(JWK.SIGNATURE_KEY_USE);
        return firstJWK;
    }

    private JWK getJwk2() {
        JWK secondJWK = new JWK();
        secondJWK.setKid(JWS_KEY_2_ID);
        secondJWK.setE(JWS_KEY_2_E);
        secondJWK.setN(JWS_KEY_2_N);
        secondJWK.setAlg(JWK.RSA_ALGORITHM);
        secondJWK.setKty(JWK.RSA_KEY_TYPE);
        secondJWK.setUse(JWK.SIGNATURE_KEY_USE);
        return secondJWK;
    }

    private JWK getJwkInvalid() {
        JWK invalidJWK = new JWK();
        invalidJWK.setKid(JWS_INVALID_KEY_ID);
        invalidJWK.setE(JWS_INVALID_KEY_E);
        invalidJWK.setN(JWS_INVALID_KEY_N);
        invalidJWK.setAlg(JWK.RSA_ALGORITHM);
        invalidJWK.setKty(JWK.RSA_KEY_TYPE);
        invalidJWK.setUse(JWK.SIGNATURE_KEY_USE);

        return invalidJWK;
    }
}