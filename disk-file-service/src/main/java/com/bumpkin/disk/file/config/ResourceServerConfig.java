package com.bumpkin.disk.file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/12 19:18
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String FILE_RESOURCE_ID = "file";

    private static final String URL = "http://localhost:50002/oauth/check_token";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(FILE_RESOURCE_ID).stateless(true);
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(URL);
        tokenService.setClientId("client_2");
        tokenService.setClientSecret("123456");
        resources.tokenServices(tokenService);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                // Since we want the protected resources to be accessible in the UI as well we need
                // session creation to be allowed (it's disabled by default in 2.0.6)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .requestMatchers().anyRequest()
                .and()
                .anonymous()
                .and()
                .authorizeRequests()
//                    .antMatchers("/product/**").access("#oauth2.hasScope('select') and hasRole('ROLE_USER')")
                .antMatchers("/download/**").authenticated()
                .antMatchers("/upload/**").authenticated()
                .antMatchers("/file/**").authenticated()
                .antMatchers("/share/**").authenticated();
        // @formatter:on
    }
}
