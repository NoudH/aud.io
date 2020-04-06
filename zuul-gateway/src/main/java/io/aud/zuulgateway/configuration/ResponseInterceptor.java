package io.aud.zuulgateway.configuration;


import com.planetexpress.jwtsecurity.utils.JwtUtil;
import io.jsonwebtoken.JwtException;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseInterceptor implements Filter {

    private JwtUtil jwtUtil;

    public ResponseInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public void doFilter (ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String token = request.getHeader("Authorization");
        if (token != null ) {
            token = token.replace("Bearer ", "");
            try{
                if (jwtUtil.validateToken(token)) {
                    response.addHeader("Authorization", "Bearer " + jwtUtil.refreshToken(token));
                }
            } catch (JwtException ex){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
