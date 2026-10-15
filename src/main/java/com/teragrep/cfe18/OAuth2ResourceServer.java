/*
 * Integration main data management for Teragrep
 * Copyright (C) 2021  Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://github.com/teragrep/teragrep/blob/main/LICENSE>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.cfe18;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "classpath:application.properties")
@EnableWebSecurity
@EnableMethodSecurity
public class OAuth2ResourceServer {

    @Value("${public.key.write}")
    RSAPublicKey writeKey;

    @Value("${public.key.read}")
    RSAPublicKey readKey;

    /** Decodes public keys and assigns SimpleGrantedAuthority value depending on key used. Authority is used in SecurityFilterChain to match HttpMethod with given token
     *
     * @return ProviderManager
     */
    @Bean
    AuthenticationManager authenticationManager() {

        JwtAuthenticationProvider readWriteProvider = new JwtAuthenticationProvider(
                NimbusJwtDecoder.withPublicKey(writeKey).build()
        );
        JwtAuthenticationConverter readWriteConverter = new JwtAuthenticationConverter();
        readWriteConverter.setJwtGrantedAuthoritiesConverter(
                jwt -> Arrays.asList(new SimpleGrantedAuthority("ROLE_ALL_ACCESS"))
        );
        readWriteProvider.setJwtAuthenticationConverter(readWriteConverter);

        JwtAuthenticationProvider readOnlyProvider = new JwtAuthenticationProvider(
                NimbusJwtDecoder.withPublicKey(readKey).build()
        );
        JwtAuthenticationConverter readOnlyConverter = new JwtAuthenticationConverter();
        readOnlyConverter.setJwtGrantedAuthoritiesConverter(
                jwt -> Arrays.asList(new SimpleGrantedAuthority("ROLE_READ_ONLY"))
        );
        readOnlyProvider.setJwtAuthenticationConverter(readOnlyConverter);

        return new ProviderManager(Arrays.asList(readOnlyProvider, readWriteProvider));
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeRequests().antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll().and()
                .authorizeRequests((authorize) -> authorize
                        .antMatchers(HttpMethod.GET,"/**").hasAnyAuthority("ROLE_READ_ONLY","ROLE_ALL_ACCESS")
                        .antMatchers(HttpMethod.PUT,"/**").hasAuthority("ROLE_ALL_ACCESS")
                        .antMatchers(HttpMethod.DELETE,"/**").hasAuthority("ROLE_ALL_ACCESS")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt((jwt) -> jwt.authenticationManager(authenticationManager()))
                );
        // @formatter:on
        return http.build();
    }


}
