package com.github.bric3.asyncsuspendedtest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/")
public class AsyncJaxrs extends ResourceConfig {
    public AsyncJaxrs() {
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
        register(PokeEndpoint.class);
    }
}
