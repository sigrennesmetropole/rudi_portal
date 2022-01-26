package org.rudi.microservice.acl.facade.config.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.util.SecretKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * @author FNI18300
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServerConfig.class);

    @Value("${security.jwt.access.tokenKey:JwTRud1}")
    private String signingKey;

    @Value("${security.jwt.access.verifierKey:}")
    private String verifierKey;

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailService;

    @Bean("clientPasswordEncoder")
    PasswordEncoder clientPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        if (StringUtils.isNotEmpty(signingKey)) {
            boolean rsaKey = false;
            if (signingKey.startsWith(SecretKeyUtils.FILE_PREFIX)) {
                if (StringUtils.isEmpty(verifierKey)) {
                    verifierKey = signingKey + ".pub";
                }
                final String privateKeyContent = readKeyContent(signingKey);
                final String publicKeyContent = readKeyContent(verifierKey);
                if (StringUtils.isNotEmpty(privateKeyContent) && StringUtils.isNotEmpty(publicKeyContent)) {
                    LOGGER.info("Set signing rsa key");
                    setKeyPair(converter, privateKeyContent, publicKeyContent);
                    rsaKey = true;
                } else {
                    LOGGER.info("Invalid rsa key");
                }
            }
            if (!rsaKey) {
                LOGGER.info("Set signing mac key");
                converter.setSigningKey(signingKey);
            }
        }
        return converter;
    }

    private void setKeyPair(JwtAccessTokenConverter converter, String privateKeyContent, String publicKeyContent) {
        try {
            converter.setKeyPair(SecretKeyUtils.readKeyPairFromContents(publicKeyContent, privateKeyContent));
        } catch (Exception e) {
            LOGGER.error("Setting signing rsa key failed", e);
        }
    }

    private String readKeyContent(String signingKey) {
        final String privateKeyName = signingKey.substring(SecretKeyUtils.FILE_PREFIX.length());
        return SecretKeyUtils.readKey(privateKeyName);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer cfg) {
        LOGGER.info("Configure AuthorizationServerSecurityConfigurer");
        // Enable /oauth/token_key URL used by resource server to validate JWT tokens
        cfg.tokenKeyAccess("permitAll");

        // Enable /oauth/check_token URL
        cfg.checkTokenAccess("permitAll");

        // BCryptPasswordEncoder(8) is used for oauth_client_details.user_secret
        cfg.passwordEncoder(clientPasswordEncoder());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        LOGGER.info("Configure ClientDetailsServiceConfigurer");
        clients.withClientDetails(clientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        LOGGER.info("Configure AuthorizationServerEndpointsConfigurer");
        endpoints.accessTokenConverter(jwtAccessTokenConverter());
        endpoints.authenticationManager(authenticationManager);
        endpoints.userDetailsService(userDetailService);
    }

    private ClientDetailsService clientDetailsService() {
        return new ClientDetailServiceImpl();
    }
}
