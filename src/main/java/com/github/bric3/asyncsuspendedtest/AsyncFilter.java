package com.github.bric3.asyncsuspendedtest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static java.time.LocalDateTime.now;

@Component
@Order(1)
public class AsyncFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            System.out.println(now() + " --> filter : " + Thread.currentThread().getName());
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
        } finally {
            if (request.isAsyncStarted()) {
                request.getAsyncContext().addListener(new AsyncListener() {
                    @Override
                    public void onComplete(AsyncEvent event) {
                        System.out.println(now() + " <-- filter [complete] : " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onTimeout(AsyncEvent event) {
                        System.out.println(now() + " <-- filter [timeout] : " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(AsyncEvent event) {
                        System.out.println(now() + " <-- filter [error] : " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onStartAsync(AsyncEvent event) {

                    }
                });
            }
            System.out.println(now() + " <-- filter : " + Thread.currentThread().getName());
        }
    }

    @Override
    public void destroy() {
    }
}