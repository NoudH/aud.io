package io.aud.zuulgateway.configuration;

import com.planetexpress.jwtsecurity.filter.JwtAuthorizationFilter;
import com.planetexpress.jwtsecurity.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayConfiguration extends WebSecurityConfigurerAdapter {

    public JwtUtil jwtUtil;

    public GatewayConfiguration(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .anonymous()
                .and()
                .addFilterBefore(new ResponseInterceptor(jwtUtil), BasicAuthenticationFilter.class)
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtUtil))
                .authorizeRequests()
                    .antMatchers("/**/v2/api-docs").hasAuthority("API_DOCS")
                    .antMatchers("/**/swagger-ui.html").hasAuthority("API_DOCS")
                    .antMatchers("/**/h2-console").hasAuthority("H2_CONSOLE")
                    .anyRequest().permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
