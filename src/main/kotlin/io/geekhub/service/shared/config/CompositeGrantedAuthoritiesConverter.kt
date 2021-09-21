package io.geekhub.service.shared.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

class CompositeGrantedAuthoritiesConverter(private val converters: List<JwtGrantedAuthoritiesConverter>) : Converter<Jwt, MutableCollection<GrantedAuthority>> {

    override fun convert(jwt: Jwt): MutableCollection<GrantedAuthority> {

        return converters.map { it.convert(jwt) }
            .flatMap { it!! }
            .toMutableList()
    }
}