package org.tma.intern.infrastructure;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "oauth2")
public interface OAuth2Config {
    String clientId();
    String clientSecret();
    String tokenEndpoint();
    String scope();
}
