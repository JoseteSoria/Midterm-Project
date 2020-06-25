package com.ironhack.MidtermProject.security;


import com.ironhack.MidtermProject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable();
        httpSecurity.httpBasic();
        httpSecurity.authorizeRequests()
                //CheckingAcc
                .mvcMatchers(HttpMethod.GET, "/checking-accounts").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.POST, "/checking-accounts").hasAuthority("ADMIN")
                .mvcMatchers("/checking-accounts/{id}").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN")
                .mvcMatchers("/checking-accounts/{id}/debit").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN", "THIRD_PARTY")
                .mvcMatchers( "/checking-accounts/{id}/credit").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN", "THIRD_PARTY")
                .mvcMatchers(HttpMethod.PUT, "/checking-accounts/{id}/set-status/{status}").hasAuthority("ADMIN")

                //CreditCardAcc
                .mvcMatchers(HttpMethod.GET, "/credit-card-accounts").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.POST,"/credit-card-accounts").hasAuthority("ADMIN")
                .mvcMatchers("/credit-card-accounts/{id}").hasAnyAuthority("ACCOUNT_HOLDER","ADMIN")
                .mvcMatchers("/credit-card-accounts/{id}/debit").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN", "THIRD_PARTY")
                .mvcMatchers("/credit-card-accounts/{id}/credit").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN", "THIRD_PARTY")

                //SavingsAcc
                .mvcMatchers(HttpMethod.GET,"/savings-accounts").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.POST,"/savings-accounts").hasAuthority("ADMIN")
                .mvcMatchers("/savings-accounts/{id}").hasAnyAuthority("ACCOUNT_HOLDER","ADMIN")
                .mvcMatchers("/savings-accounts/{id}/debit").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN", "THIRD_PARTY")
                .mvcMatchers("/savings-accounts/{id}/credit").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN", "THIRD_PARTY")

                //StudentCheckingAcc
                .mvcMatchers(HttpMethod.GET,"/student-checking-accounts").hasAuthority("ADMIN")
                .mvcMatchers("/student-checking-accounts/{id}").hasAnyAuthority("ACCOUNT_HOLDER","ADMIN")
                .mvcMatchers("/student-checking-accounts/{id}/debit").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN", "THIRD_PARTY")
                .mvcMatchers("/student-checking-accounts/{id}/credit").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN", "THIRD_PARTY")

                //Transaction
                .mvcMatchers("/transactions").hasAuthority("ADMIN")

                //AccountHolder
                .mvcMatchers(HttpMethod.GET,"/account-holders").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.POST,"/account-holders").hasAuthority("ADMIN")
                .mvcMatchers("/account-holders/{id}").hasAnyAuthority("ACCOUNT_HOLDER","ADMIN")
                .mvcMatchers("/account-holders/{id}/accounts").hasAnyAuthority("ACCOUNT_HOLDER", "ADMIN")
                .mvcMatchers("/account-holders/transference/{id}").hasAnyAuthority("ACCOUNT_HOLDER")

                //Admin
                .mvcMatchers("/admins").hasAuthority("ADMIN")

                //ThirdParty
                .mvcMatchers(HttpMethod.GET,"/third-parties").hasAuthority("ADMIN")
                .mvcMatchers(HttpMethod.POST, "/third-parties").hasAuthority("ADMIN")

                //User
                .mvcMatchers("/users").hasAuthority("ADMIN")

                .anyRequest().denyAll()
                .and().requestCache().requestCache(new NullRequestCache())
                .and().logout().deleteCookies("JSESSIONID");
    }

}

