package com.ecommerce.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Jwtgenerator {
    public String generateToken(Authentication authentication){
        // this is the auth object gen by uath provider
        String username = authentication.getName();
        // then we need a expiry date
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);


        // after gather the username and expiry, we can gen the token
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date()) //current date
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)// jwt token is signed with certain algo, you can actually tell
        // in the token, so the token split 3 part, you can see it, so there is a algo does signing
                .compact();

        return token;
    }


    // we need to able get user name form jwt
    public String getUsernameFromJWT(String token){
        Claims claims = Jwts.parser() // every jwt token has a claim in it,
                .setSigningKey(SecurityConstants.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); //SUBJECT is the username
    }

    // we need to be able to validate the token
    public Boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
            // if this is true, we return true
            return true;
        }catch (Exception ex){
            throw new AuthenticationCredentialsNotFoundException("Jwt was expired or incorrect");
        }
    }
}
