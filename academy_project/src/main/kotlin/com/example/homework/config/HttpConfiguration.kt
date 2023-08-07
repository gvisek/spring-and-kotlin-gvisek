package com.example.homework.config

import com.example.homework.service.HttpConfigUrl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class HttpConfiguration {

    @Bean
    fun provideWebClient(
        webClientBuilder: WebClient.Builder,
        httpConfigUrl: HttpConfigUrl
    ): WebClient {
        return webClientBuilder.baseUrl(httpConfigUrl.baseUrl).build()
    }

}