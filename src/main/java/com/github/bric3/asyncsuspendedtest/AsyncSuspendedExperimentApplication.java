package com.github.bric3.asyncsuspendedtest;

import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class AsyncSuspendedExperimentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncSuspendedExperimentApplication.class, args);
    }

//    @Bean
//    public FilterRegistrationBean myFilterBean() {
//        final FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
//        filterRegBean.setFilter(new AsyncFilter());
//        filterRegBean.addUrlPatterns("/*");
//        filterRegBean.setEnabled(Boolean.TRUE);
//        filterRegBean.setName("Meu Filter");
//        filterRegBean.setAsyncSupported(Boolean.TRUE);
//        return filterRegBean;
//    }


    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addConnectorCustomizers(connector -> {
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
            if (protocolHandler instanceof AbstractProtocol) {
//                ExecutorService executor = Executors.newFixedThreadPool(1);
//                ((AbstractProtocol) protocolHandler).setExecutor(executor);
            }
        });
        return tomcat;
    }

    @Bean
    public ServerProperties serverProperties() {
        ServerProperties serverProperties = new ServerProperties();
        serverProperties.setPort(8000);
        serverProperties.getCompression().setEnabled(true);
        serverProperties.getCompression().setMimeTypes(new String[] { "application/json", "text/plain" });

        serverProperties.getTomcat().setMaxConnections(2);
        serverProperties.getTomcat().setMinSpareThreads(1);
        serverProperties.getTomcat().setMaxThreads(1);
        serverProperties.getTomcat().setAcceptCount(10);
        return serverProperties;
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }
}
