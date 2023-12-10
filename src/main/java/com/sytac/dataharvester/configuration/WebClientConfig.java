package com.sytac.dataharvester.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@AllArgsConstructor
public class WebClientConfig {

    private final PlatformProperties platformProperties;

    @Bean
    public WebClientFactory webClientFactory() {
        return new WebClientFactory();
    }

    @Bean
    public WebClient sytflixWebClient() {
        return webClientFactory().createWebClient("SYTFLIX");
    }

    @Bean
    public WebClient sytazonWebClient() {
        return webClientFactory().createWebClient("SYTAZON");
    }

    @Bean
    public WebClient sysneyWebClient() { return webClientFactory().createWebClient("SYSNEY"); }

    class WebClientFactory {
        public WebClient createWebClient(String provider) {
            PlatformDetails details = platformProperties.getPlatforms().get(provider);
            return WebClient.builder()
                    .baseUrl(details.getHost())
                    .defaultHeaders(header -> header.setBasicAuth(details.getUsername(), details.getPassword()))
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .clientConnector(createConnectorWithTimeouts())
                    .build();
        }
        private ClientHttpConnector createConnectorWithTimeouts() {
            int connectTimeOut = platformProperties.getConnectionTimeout();
            int readTimeOut = platformProperties.getReadTimeout();

            var httpClient = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut)
                    .doOnConnected(connection ->
                            connection.addHandlerLast(new ReadTimeoutHandler(readTimeOut, TimeUnit.MILLISECONDS))
                                    .addHandlerLast(new WriteTimeoutHandler(readTimeOut, TimeUnit.MILLISECONDS)))
                    .compress(true);

            return new ReactorClientHttpConnector(httpClient);
        }
    }


}
