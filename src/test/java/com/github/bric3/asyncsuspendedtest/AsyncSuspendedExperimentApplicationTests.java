package com.github.bric3.asyncsuspendedtest;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import okhttp3.logging.HttpLoggingInterceptor.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AsyncSuspendedExperimentApplicationTests {

    @LocalServerPort
    int port;

    private OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(0, SECONDS)
            .readTimeout(0, SECONDS)
            .writeTimeout(0, SECONDS)
            .addInterceptor(new HttpLoggingInterceptor(Logger.DEFAULT).setLevel(Level.BODY))
            .build();


    @Test
    public void single_poke_sync_should_work() {
        HttpCallCallback responseCallback = new HttpCallCallback().enqueue(
                http.newCall(new Request.Builder().url("http://localhost:" + port + "/poke")
                                                  .addHeader("Accept", "text/plain")
                                                  .build()));


        assertThat(responseCallback.response().code()).isEqualTo(200);
    }

    /*
    2017-11-16T15:18:24.851 --> filter : http-nio-8000-exec-1
    2017-11-16T15:18:24.853 --> pokeSync : http-nio-8000-exec-1
    2017-11-16T15:18:24.853 --> waitForIt : http-nio-8000-exec-1
    2017-11-16T15:18:24.853 --> waitForIt : 15
    2017-11-16T15:18:39.857 <-- waitForIt : http-nio-8000-exec-1
    2017-11-16T15:18:39.858 <-- pokeSync : http-nio-8000-exec-1
    2017-11-16T15:18:39.858 <-- filter : http-nio-8000-exec-1
    2017-11-16T15:18:39.860 --> filter : http-nio-8000-exec-1
    2017-11-16T15:18:39.861 --> pokeSync : http-nio-8000-exec-1
    2017-11-16T15:18:39.861 --> waitForIt : http-nio-8000-exec-1
    2017-11-16T15:18:39.861 --> waitForIt : 15
    2017-11-16T15:18:54.866 <-- waitForIt : http-nio-8000-exec-1
    2017-11-16T15:18:54.866 <-- pokeSync : http-nio-8000-exec-1
    2017-11-16T15:18:54.867 <-- filter : http-nio-8000-exec-1
     */
    @Test
    public void two_successive_poke_sync_will_be_executed_successively() {
        int wait = 4;

        HttpCallCallback request1 = new HttpCallCallback().enqueue(
                http.newCall(new Request.Builder().url(new HttpUrl.Builder().scheme("http")
                                                                            .host("localhost")
                                                                            .port(port)
                                                                            .addPathSegments("poke")
                                                                            .addQueryParameter("wait", Integer.toString(wait))
                                                                            .build())
                                                  .addHeader("Accept", "text/plain")
                                                  .build()));
        HttpCallCallback request2 = new HttpCallCallback().enqueue(
                http.newCall(new Request.Builder().url(new HttpUrl.Builder().scheme("http")
                                                                            .host("localhost")
                                                                            .port(port)
                                                                            .addPathSegments("poke")
                                                                            .addQueryParameter("wait", Integer.toString(wait))
                                                                            .build())
                                                  .addHeader("Accept", "text/plain")
                                                  .build()));

        await("request 1").atLeast(wait - 1, SECONDS).timeout(20, SECONDS).until(request1::hasResponse);
        assertThat(request2.hasResponse()).describedAs("second request should wait for first query to finish").isFalse();
        await("request 2").atLeast(wait - 1, SECONDS).timeout(20, SECONDS).until(request2::hasResponse);
    }


    /*
    2017-11-16T15:21:03.856 --> filter : http-nio-8000-exec-1
    2017-11-16T15:21:03.862 --> pokeSuspended : http-nio-8000-exec-1
    2017-11-16T15:21:03.863 <-- pokeSuspended : http-nio-8000-exec-1
    2017-11-16T15:21:03.863 --> pokeSuspended task : pool-1-thread-1
    2017-11-16T15:21:03.863 --> waitForIt : pool-1-thread-1
    2017-11-16T15:21:03.863 --> waitForIt : 15
    2017-11-16T15:21:03.863 <-- filter : http-nio-8000-exec-1
    2017-11-16T15:21:03.864 --> filter : http-nio-8000-exec-1
    2017-11-16T15:21:03.866 --> pokeSuspended : http-nio-8000-exec-1
    2017-11-16T15:21:03.866 <-- pokeSuspended : http-nio-8000-exec-1
    2017-11-16T15:21:03.866 --> pokeSuspended task : pool-1-thread-2
    2017-11-16T15:21:03.866 --> waitForIt : pool-1-thread-2
    2017-11-16T15:21:03.866 <-- filter : http-nio-8000-exec-1
    2017-11-16T15:21:03.866 --> waitForIt : 15
    2017-11-16T15:21:18.866 <-- waitForIt : pool-1-thread-1
    2017-11-16T15:21:18.867 <-- waitForIt : pool-1-thread-2
    2017-11-16T15:21:18.869 <-- pokeSuspended task : pool-1-thread-1
    2017-11-16T15:21:18.869 <-- pokeSuspended task : pool-1-thread-2
     */
    @Test
    public void two_successive_poke_suspended_will_be_executed_successively() {
        int wait = 4;

        HttpCallCallback request1 = new HttpCallCallback().enqueue(
                http.newCall(new Request.Builder().url(new HttpUrl.Builder().scheme("http")
                                                                            .host("localhost")
                                                                            .port(port)
                                                                            .addPathSegments("poke")
                                                                            .addQueryParameter("wait", Integer.toString(wait))
                                                                            .build())
                                                  .addHeader("Accept", "text/vnd.suspended+plain")
                                                  .build()));
        HttpCallCallback request2 = new HttpCallCallback().enqueue(
                http.newCall(new Request.Builder().url(new HttpUrl.Builder().scheme("http")
                                                                            .host("localhost")
                                                                            .port(port)
                                                                            .addPathSegments("poke")
                                                                            .addQueryParameter("wait", Integer.toString(wait))
                                                                            .build())
                                                  .addHeader("Accept", "text/vnd.suspended+plain")
                                                  .build()));

        await("request 1").atLeast(wait - 1, SECONDS).timeout(20, SECONDS).until(request1::hasResponse);
        assertThat(request2.hasResponse()).describedAs("second request should be handled").isFalse();
        await("request 2").atLeast(wait - 1, SECONDS).timeout(20, SECONDS).until(request2::hasResponse);
    }

    @Test
    public void single_poke_suspended_wait_request_processing() {
        HttpCallCallback responseCallback = new HttpCallCallback().enqueue(
                http.newCall(new Request.Builder().url("http://localhost:" + port + "/poke")
                                                  .addHeader("Accept", "text/vnd.suspended+plain")
                                                  .build()));


        assertThat(responseCallback.response().code()).isEqualTo(200);
    }

}
