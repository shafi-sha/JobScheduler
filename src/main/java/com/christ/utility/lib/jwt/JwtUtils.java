package com.christ.utility.lib.jwt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String KEY_ID = "ID";
    public static final String KEY_NAME = "Name";
    public static final String KEY_ROLE = "Role";
    public static final String KEY_DATA = "Data";

    private final static String Secret;
    private final static long ExpirationTime;

    static {
        //Secret = VaultUtils.instance.get(VaultUtils.CONFIG_SECTION_APP_CONFIG, "security.jwt.secret").getValue();
        //ExpirationTime = VaultUtils.instance.get(VaultUtils.CONFIG_SECTION_APP_CONFIG, "security.jwt.expiry.in.seconds").getInt();
        Secret = "erp@2019";
        ExpirationTime = 3600;
    }

    public static Claims getClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(JwtUtils.getKey())
                .parseClaimsJws(token)
                .getBody();
    }
    public static String getIDFromToken(String token) {
        return getClaims(token).get(KEY_ID, String.class);
    }
    public static String getNameFromToken(String token) {
        return getClaims(token).get(KEY_NAME, String.class);
    }
    public static String getRoleFromToken(String token) {
        return getClaims(token).get(KEY_ROLE, String.class);
    }
    public static<T> T getDataFromToken(String token) {
        return new Gson()
                .fromJson(getClaims(token).get(KEY_DATA, String.class),
                        new TypeToken<T>() {}.getType());
    }
    public static JwtUser getUserFromToken(String token) {
        return new Gson()
                .fromJson(getClaims(token).get(KEY_DATA, String.class), JwtUser.class);
    }
    public static Date getExpirationDateFromToken(String token) {
        return getClaims(token).getExpiration();
    }
    public static String generateToken(JwtUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(KEY_ID, user.id);
        claims.put(KEY_NAME, user.name);
        claims.put(KEY_ROLE, user.role);
        claims.put(KEY_DATA, new Gson().toJson(user));
        return generateToken(claims, user.id);
    }
    public static Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    private static Key getKey() {
        Key key = null;
        try {
            byte[] apiKeySecretBytes = Base64.getEncoder().encodeToString(JwtUtils.Secret.getBytes()).getBytes();
            key = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());
        }
        catch(Exception ignored) { }
        return key;
    }
    private static String generateToken(Map<String, Object> claims, String username) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + (JwtUtils.ExpirationTime * 1000));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS512, JwtUtils.getKey())
                .compact();
    }
}
