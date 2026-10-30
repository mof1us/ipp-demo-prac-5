package com.ipp.bookEmit

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.postgresql.PostgreSQLContainer

@SpringBootTest
class SwaggerTests {
    companion object {
        private val postgres = PostgreSQLContainer("postgres:16-alpine").apply {
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configurePostgres(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `openapi describes all contact operations and fields`() {
        mvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.info.title").value("Contacts API"))
            .andExpect(jsonPath("$.paths['/v1/contact'].get").exists())
            .andExpect(jsonPath("$.paths['/v1/contact'].post").exists())
            .andExpect(jsonPath("$.paths['/v1/contact'].delete").exists())
            .andExpect(jsonPath("$.paths['/v1/contact/{uid}'].get").exists())
            .andExpect(jsonPath("$.paths['/v1/contact/{uid}'].put").exists())
            .andExpect(jsonPath("$.paths['/v1/contact/{uid}'].delete").exists())
            .andExpect(jsonPath("$.components.schemas.ContactDto.properties.id.type").value("integer"))
            .andExpect(jsonPath("$.components.schemas.ContactDto.properties.name.type").value("string"))
            .andExpect(jsonPath("$.components.schemas.ContactDto.properties.phone.type").value("string"))
            .andExpect(jsonPath("$.components.schemas.ContactDto.properties.email.type").value("string"))
    }

    @Test
    fun `swagger ui and yaml description are available`() {
        mvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection)
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk)
        mvc.perform(get("/v3/api-docs.yaml")).andExpect(status().isOk)
    }
}
