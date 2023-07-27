package com.example.homework.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class HttpConfiguration {

    @Bean
    fun provideWebClient(
        webClientBuilder: WebClient.Builder,
        @Value("\${car-service.base-url}") baseUrl: String
    ): WebClient {
        return webClientBuilder.baseUrl(baseUrl).build()
    }

}