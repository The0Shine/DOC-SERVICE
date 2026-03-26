package com.horob1.doc_service.config

import com.horob1.doc_service.shared.web.interceptor.UserPermissionContextFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userContextFilter: UserPermissionContextFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }

            authorizeHttpRequests {
                // Infrastructure
                authorize("/swagger-ui/**",  permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/health",         permitAll)
                authorize("/actuator/**",    permitAll)

                // Public GET endpoints
                authorize(HttpMethod.GET, "/api/v1/schools/**",       permitAll)
                authorize(HttpMethod.GET, "/api/v1/departments/**",   permitAll)

                // Protected: specific document paths (MUST come before {id} wildcard)
                authorize(HttpMethod.GET, "/api/v1/documents/my",           authenticated)
                authorize(HttpMethod.GET, "/api/v1/documents/*/download",   authenticated)
                authorize(HttpMethod.POST,   "/api/v1/documents",           authenticated)
                authorize(HttpMethod.PUT,    "/api/v1/documents/*",         authenticated)
                authorize(HttpMethod.DELETE, "/api/v1/documents/*",         authenticated)

                // Public: document list & detail (after specific paths)
                authorize(HttpMethod.GET, "/api/v1/documents",        permitAll)
                authorize(HttpMethod.GET, "/api/v1/documents/{id}",   permitAll)

                // Protected: bookmarks, purchases
                authorize("/api/v1/bookmarks/**",         authenticated)
                authorize("/api/v1/purchases/**",         authenticated)

                // Public: SePay payment webhook (authenticated via API key, not JWT)
                authorize(HttpMethod.POST, "/api/transactions/payment-confirmation", permitAll)

                authorize(anyRequest, denyAll)
            }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            anonymous { disable() }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(userContextFilter)
        }
        return http.build()
    }
}
