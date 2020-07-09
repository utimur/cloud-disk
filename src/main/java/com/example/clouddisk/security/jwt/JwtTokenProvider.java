package com.example.clouddisk.security.jwt;

import com.example.clouddisk.models.Role;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    @Value("${jwt.token.activation.expired}")
    private long validityActivationInMillSeconds;


    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }



    // isActivation - true - токен создаётся для отправления активации на почту, false - токен создаётся для авторизации. В зависимости от isActivation, будут созданы токены с различным временем жизни.
    public String createToken(String username, List<Role> roles, Long id, boolean isActivation) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", getRoleNames(roles));
        claims.put("id", id);
        Date now = new Date();
        Date validity;
        if (!isActivation) validity = new Date(now.getTime() + validityInMilliseconds);
        else validity = new Date(now.getTime() + validityActivationInMillSeconds);

        return Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secret)//
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null) {
            if (bearerToken.startsWith("Bearer "))
                return bearerToken.substring(7, bearerToken.length());
            /*
            if (bearerToken.startsWith("Activation "))
                return bearerToken.substring(11, bearerToken.length());

             */
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    private List<String> getRoleNames(List<Role> userRoles) {
        List<String> result = new ArrayList<>();

        userRoles.forEach(role -> {
            result.add(role.getRole());
        });

        return result;
    }
}
