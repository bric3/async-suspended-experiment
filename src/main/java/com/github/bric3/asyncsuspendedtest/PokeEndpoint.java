package com.github.bric3.asyncsuspendedtest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.concurrent.Executor;

import static java.time.LocalDateTime.now;

@Endpoint
@Path("poke")
public class PokeEndpoint {
    HeavyWeightService heavyWeightService;
    Executor executor;

    public PokeEndpoint(HeavyWeightService heavyWeightService, Executor executor) {
        this.heavyWeightService = heavyWeightService;
        this.executor = executor;
    }

    @GET
    @Produces({"text/plain", "text/vnd.sync+plain"})
    public Response pokeSync(@QueryParam("wait") Short wait) {
        try {
            System.out.println(now() + " --> pokeSync : " + Thread.currentThread().getName());
            return Response.ok(heavyWeightService.waitForIt(wait)).build();
        } finally {
            System.out.println(now() + " <-- pokeSync : " + Thread.currentThread().getName());
        }
    }

    @GET
    @Produces("text/vnd.suspended+plain")
    public void pokeSuspended(@Suspended AsyncResponse asyncResponse, @QueryParam("wait") Short wait) {
        try {
            System.out.println(now() + " --> pokeSuspended : " + Thread.currentThread().getName());
            executor.execute(() -> {
                try {
                    System.out.println(now() + " --> pokeSuspended task : " + Thread.currentThread().getName());
                    asyncResponse.resume(heavyWeightService.waitForIt(wait));
                } finally {
                    System.out.println(now() + " <-- pokeSuspended task : " + Thread.currentThread().getName());
                }

            });
        } finally {
            System.out.println(now() + " <-- pokeSuspended : " + Thread.currentThread().getName());
        }
    }
}
