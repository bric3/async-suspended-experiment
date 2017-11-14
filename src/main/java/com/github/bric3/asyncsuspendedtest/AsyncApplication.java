package com.github.bric3.asyncsuspendedtest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Service;

import javax.ws.rs.ApplicationPath;

@Service
@ApplicationPath("/")
public class AsyncApplication extends ResourceConfig {
    public AsyncApplication() {
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
        register(PokeEndpoint.class);
    }
}
