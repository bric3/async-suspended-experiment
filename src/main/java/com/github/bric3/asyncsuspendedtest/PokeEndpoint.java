package com.github.bric3.asyncsuspendedtest;

import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Service
@Path("poke")
public class PokeEndpoint {

    HeavyWeightService heavyWeightService;

    public PokeEndpoint(HeavyWeightService heavyWeightService) {
        this.heavyWeightService = heavyWeightService;
    }

    @GET
    public Response poke() {
        return Response.ok(heavyWeightService.waitForIt()).build();
    }
}
