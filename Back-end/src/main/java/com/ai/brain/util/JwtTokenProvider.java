package com.ai.brain.util;

import com.ai.brain.vo.Userinfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${token.secret.key}")
    private String secretKey;

    @Value("${token.vaild.time}")
    private long tokenValidTime;

    public String createToken(Userinfo userinfo) {
        Claims claims = Jwts.claims().setSubject(userinfo.getUiId());
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidTime);
        return Jwts.builder().setClaims(claims).claim("u_name", userinfo.getUiName())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    // 토큰에서 헤더 값 가져옴
    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        System.out.println("logger - Header token: [" + token + "]");
        return token;
    }

    // 로그인 시 헤더에서 access Token 얻어옴
    public String getAccessToken(HttpServletRequest request) {
        String access_token = request.getHeader("access_token");
        return access_token;
    }

    // 토큰 유효성 체크
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 유저 정보 가져옴
    public Userinfo getInfo(HttpServletRequest request) {
        Userinfo userinfo = new Userinfo();
        String token = resolveToken(request);
        if (validateToken(token)) {
            Claims cl = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            userinfo.setUiId(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
            userinfo.setUiName(cl.get("u_name").toString());
        } else {
            userinfo.setUiName("F");
        }

        return userinfo;
    }

}
