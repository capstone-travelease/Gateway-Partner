package com.GatewayAPI.Utils;

import com.GatewayAPI.Repository.JwtRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Autowired
    private JwtRepository jwtRepository;
    private static final String KEY_SECRET = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";


    private Claims parseToken(String token){
        return  Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){return  extractClaim(token,Claims::getExpiration);}
    public boolean isTokenValid(String token){
        final String userNameToken = extractClaim(token,Claims::getSubject);
        String userNameDB = jwtRepository.findEmail(userNameToken);
        if(!userNameDB.isBlank() && !isTokenExpired(token)){
            return true;
        }
        return false;
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }
    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(KEY_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
