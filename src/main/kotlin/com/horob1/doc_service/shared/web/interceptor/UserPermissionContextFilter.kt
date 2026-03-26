package com.horob1.doc_service.shared.web.interceptor

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.horob1.doc_service.shared.constant.SecurityConstants.USER_ID_HEADER
import com.horob1.doc_service.shared.constant.SecurityConstants.USER_PERMISSIONS_HEADER
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class UserPermissionContextFilter : OncePerRequestFilter() {

    private val mapper = ObjectMapper()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val userId = request.getHeader(USER_ID_HEADER)

        if (!userId.isNullOrBlank()) {
            val permissionsHeader = request.getHeader(USER_PERMISSIONS_HEADER)
            val authorities = parsePermissionsHeader(permissionsHeader)

            try {
                val authentication = UsernamePasswordAuthenticationToken(
                    UUID.fromString(userId),
                    null,
                    authorities
                )

                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: Exception) {
                // Ignore if not a valid UUID
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun parsePermissionsHeader(encodedHeader: String?): List<GrantedAuthority> {
        if (encodedHeader.isNullOrBlank()) return emptyList()

        return try {
            val decodedJson = String(Base64.getDecoder().decode(encodedHeader))

            val permissions: List<String> =
                mapper.readValue(decodedJson, object : TypeReference<List<String>>() {})

            permissions.map { SimpleGrantedAuthority(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
