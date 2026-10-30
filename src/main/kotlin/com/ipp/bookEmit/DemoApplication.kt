package com.ipp.bookEmit

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "Contacts API", version = "1.0.0"))
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}
