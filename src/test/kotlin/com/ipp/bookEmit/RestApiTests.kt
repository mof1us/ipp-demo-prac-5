package com.ipp.bookEmit

import com.ipp.bookEmit.server.AuthorsController
import com.ipp.bookEmit.server.BooksController
import com.ipp.bookEmit.server.LibrariesController
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestApiTests {
    @Autowired
    private lateinit var authorsController: AuthorsController

    @Autowired
    private lateinit var librariesController: LibrariesController

    @Autowired
    private lateinit var booksController: BooksController

    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        mvc = MockMvcBuilders.standaloneSetup(authorsController, librariesController, booksController).build()
    }

    @ParameterizedTest
    @CsvSource("authors,name", "libraries,title")
    fun `create read replace patch and delete resource`(resource: String, field: String) {
        val id = create("/$resource", """{"$field":"Created"}""")
        mvc.perform(get("/$resource/$id"))
            .andExpect(status().isOk).andExpect(jsonPath("$.$field").value("Created"))
        mvc.perform(get("/$resource"))
            .andExpect(status().isOk).andExpect(jsonPath("$[?(@.id == $id)].$field").value("Created"))
        mvc.perform(put("/$resource/$id").contentType(MediaType.APPLICATION_JSON)
            .content("""{"$field":"Replaced"}"""))
            .andExpect(status().isOk).andExpect(jsonPath("$.$field").value("Replaced"))
        mvc.perform(patch("/$resource/$id").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isOk).andExpect(jsonPath("$.$field").value("Replaced"))
        mvc.perform(patch("/$resource/$id").contentType(MediaType.APPLICATION_JSON)
            .content("""{"$field":"Patched"}"""))
            .andExpect(status().isOk).andExpect(jsonPath("$.$field").value("Patched"))
        mvc.perform(delete("/$resource/$id")).andExpect(status().isOk)
        mvc.perform(get("/$resource/$id")).andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @CsvSource("authors,name", "libraries,title")
    fun `missing resources return 404 and required fields return 400`(resource: String, field: String) {
        mvc.perform(get("/$resource/999")).andExpect(status().isNotFound)
        mvc.perform(delete("/$resource/999")).andExpect(status().isNotFound)
        mvc.perform(put("/$resource/999").contentType(MediaType.APPLICATION_JSON)
            .content("""{"$field":"Updated"}"""))
            .andExpect(status().isNotFound)
        mvc.perform(patch("/$resource/999").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isNotFound)
        mvc.perform(post("/$resource").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest)
        mvc.perform(put("/$resource/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deleting author removes their books and links but keeps other books and libraries`() {
        val authorId = create("/authors", """{"name":"Other author"}""")
        val linkedBook = create("/books", """{"title":"Linked","author":$authorId,"libraries":[1,2]}""")
        val unlinkedBook = create("/books", """{"title":"Unlinked","author":$authorId,"libraries":[]}""")
        mvc.perform(delete("/authors/$authorId")).andExpect(status().isOk)
        mvc.perform(get("/books/$linkedBook")).andExpect(status().isNotFound)
        mvc.perform(get("/books/$unlinkedBook")).andExpect(status().isNotFound)
        mvc.perform(get("/books")).andExpect(status().isOk).andExpect(jsonPath("$.length()").value(1))
        mvc.perform(get("/libraries/1")).andExpect(status().isOk)
        mvc.perform(get("/libraries/2")).andExpect(status().isOk)
        // Reused book IDs must not inherit links left by the deleted author.
        val replacement = create("/books", """{"title":"Replacement","author":1,"libraries":[]}""")
        mvc.perform(get("/books/$replacement"))
            .andExpect(status().isOk).andExpect(jsonPath("$.libraries").isEmpty)
    }

    @Test
    fun `deleting library removes links but keeps books usable`() {
        val bookId = create("/books", """{"title":"Shared","author":1,"libraries":[1,2]}""")
        mvc.perform(delete("/libraries/2")).andExpect(status().isOk)
        mvc.perform(get("/books/1")).andExpect(status().isOk).andExpect(jsonPath("$.libraries").isEmpty)
        mvc.perform(get("/books/$bookId"))
            .andExpect(status().isOk).andExpect(jsonPath("$.libraries[0].id").value(1))
        // Recreating an ID must not restore links to the deleted library.
        create("/libraries", """{"title":"Replacement"}""")
        mvc.perform(get("/books/$bookId"))
            .andExpect(status().isOk).andExpect(jsonPath("$.libraries.length()").value(1))
        mvc.perform(patch("/books/1").contentType(MediaType.APPLICATION_JSON).content("""{"title":"Updated"}"""))
            .andExpect(status().isOk).andExpect(jsonPath("$.title").value("Updated"))
        mvc.perform(delete("/books/1")).andExpect(status().isOk)
    }

    @Test
    fun `book updates return current links and reject missing libraries without changes`() {
        mvc.perform(put("/books/1").contentType(MediaType.APPLICATION_JSON)
            .content("""{"title":"Updated","author":1,"libraries":[1]}"""))
            .andExpect(status().isOk).andExpect(jsonPath("$.libraries[0].id").value(1))
        mvc.perform(patch("/books/1").contentType(MediaType.APPLICATION_JSON)
            .content("""{"title":"Invalid","libraries":[999]}"""))
            .andExpect(status().isNotFound)
        mvc.perform(get("/books/1")).andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated"))
            .andExpect(jsonPath("$.libraries[0].id").value(1))
        mvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON)
            .content("""{"title":"Invalid","author":1,"libraries":[999]}"""))
            .andExpect(status().isNotFound)
        mvc.perform(get("/books")).andExpect(status().isOk).andExpect(jsonPath("$.length()").value(1))
    }

    private fun create(path: String, body: String): Long {
        val response = mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk).andReturn().response.contentAsString
        return JsonPath.read<Number>(response, "$.id").toLong()
    }
}
