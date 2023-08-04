package com.example.homework.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration {


    @Bean
    fun securityFilterChain(http: HttpSecurity) : SecurityFilterChain {


        http {
            cors {  }
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeRequests {
                authorize( HttpMethod.GET,"/cars/analytics", permitAll )
                authorize( HttpMethod.POST, "/cars", hasAnyAuthority("SCOPE_admin", "SCOPE_user") )
                authorize( HttpMethod.GET, "/cars/page", hasAuthority("SCOPE_admin"))
                authorize( HttpMethod.GET, "/cars/{carId}", hasAnyAuthority("SCOPE_admin", "SCOPE_user"))
                authorize( HttpMethod.DELETE, "/cars/{carId}", hasAuthority("SCOPE_admin"))
                authorize( HttpMethod.POST, "/cars/checkup", hasAuthority("SCOPE_admin"))
                authorize( HttpMethod.DELETE, "/cars/checkup/{id}", hasAuthority("SCOPE_admin"))
                authorize( HttpMethod.GET, "/cars/checkup/{carId}/page", hasAnyAuthority("SCOPE_admin", "SCOPE_user"))
            }
            oauth2ResourceServer {
                jwt {}
            }
        }
        return http.build()
    }
}