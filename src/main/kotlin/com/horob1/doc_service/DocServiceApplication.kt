package com.horob1.doc_service

import com.horob1.doc_service.shared.web.config.SharedWebConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@EnableDiscoveryClient
@SpringBootApplication
@Import(SharedWebConfig::class)
class DocServiceApplication

fun main(args: Array<String>) {
    runApplication<DocServiceApplication>(*args)
}
