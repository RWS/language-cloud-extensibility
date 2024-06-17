package com.rws.lt.lc.blueprint.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

/**
 * JSON Web Key. See <a href="https://tools.ietf.org/html/rfc7517">RFC 7517</a>.
 * <p>
 * Only RSA keys supported currently.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class JWK {

    public static final String RSA_ALGORITHM = "RS256";
    public static final String RSA_KEY_TYPE = "RSA";
    public static final String SIGNATURE_KEY_USE = "sig";

    private String kty;

    private String n;

    private String e;

    private String alg;

    private String kid;

    private String use;

    public JWK() {
        // For JSON deserialization
    }

    public void setEInt(BigInteger e) {
        this.e = (e != null) ? base64UrlEncodedUnsignedBigEndianBytes(e) : null;
    }

    @JsonIgnore
    public BigInteger getEInt() {
        return (this.e != null) ? base64UrlDecodedUnsignedBigEndianBytes(this.e) : null;
    }

    private BigInteger base64UrlDecodedUnsignedBigEndianBytes(String s) {
        return new BigInteger(1, Base64.getUrlDecoder().decode(s));
    }

    private String base64UrlEncodedUnsignedBigEndianBytes(BigInteger i) {
        return Base64.getUrlEncoder().encodeToString(toByteArrayUnsigned(i));
    }

    private byte[] toByteArrayUnsigned(BigInteger bigInteger) {
        if (bigInteger == null) {
            throw new IllegalArgumentException("Argument may not be null.");
        }
        byte[] bytes = bigInteger.abs().toByteArray();
        // Strip first byte if sign byte
        if (bytes[0] == 0) {
            return Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return bytes;
    }

    public void setNInt(BigInteger n) {
        this.n = (n != null) ? base64UrlEncodedUnsignedBigEndianBytes(n) : null;
    }

    @JsonIgnore
    public BigInteger getNInt() {
        return (this.n != null) ? base64UrlDecodedUnsignedBigEndianBytes(this.n) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JWK jwk = (JWK) o;

        return kid != null ? kid.equals(jwk.kid) : jwk.kid == null;

    }

    @Override
    public int hashCode() {
        return kid != null ? kid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "JWK{" +
                "alg='" + alg + '\'' +
                ", kty='" + kty + '\'' +
                ", n='" + n + '\'' +
                ", e='" + e + '\'' +
                ", kid='" + kid + '\'' +
                '}';
    }
}