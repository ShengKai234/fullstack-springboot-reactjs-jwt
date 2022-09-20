package com.s.kai.login.LoginJwt.security;

import com.s.kai.login.LoginJwt.security.jwt.AuthEntryPointJwt;
import com.s.kai.login.LoginJwt.security.jwt.AuthTokenFilter;
import com.s.kai.login.LoginJwt.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity                  // allows Spring to find and automatically apply the class to the global Web Security.
@EnableGlobalMethodSecurity(        // provides AOP security on methods
//        securedEnabled = true,
//        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // a filter that executes once per request.
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Spring Security will load User details to perform authentication & authorization. So it has UserDetailsService interface that we need to implement.
    // The implementation of UserDetailsService will be used for configuring DaoAuthenticationProvider by AuthenticationManagerBuilder.userDetailsService() method.
    // We also need a PasswordEncoder for the DaoAuthenticationProvider. If we don’t specify, it will use plain text.
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // UserDetailsServiceImpl (!!!convert Set<Role> into List<GrantedAuthority>!!!)
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    // 驗證類別註冊容器，產生的 token 傳遞給 AuthenticationManager 進行登錄認證
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 加密器註冊容器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Override the configure(HttpSecurity http) method from WebSecurityConfigurerAdapter interface.
    // It tells Spring Security how we configure CORS and CSRF, when we want to require all users to be authenticated or not
    // , which filter (AuthTokenFilter) and when we want it to work (filter before UsernamePasswordAuthenticationFilter)
    // , which Exception Handler is chosen (AuthEntryPointJwt).
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .anyRequest().authenticated();
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
