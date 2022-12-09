package com.mih.training.invoice;

import com.twitter.finagle.Http;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinagleConfig {
    @Bean
    public Service<Request, Response> httpClient(@Value("${wiremock.server.port:8080}") int port) {
        return Http.client().newService(":" + port);
    }
}
