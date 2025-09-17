package dev.timduerr.openapigeneratorexample.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.timduerr.openapigeneratorexample.model.TodoCreateDto;
import dev.timduerr.openapigeneratorexample.model.TodoUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * TodosApiIT.
 *
 * @author Tim Dürr
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
class TodosApiIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * Tests that the API returns a list of todos with default values.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void listTodos_defaults_headersAndBody() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(header().string(PaginationHeaders.X_PAGE.getValue(), "0"))
                .andExpect(header().string(PaginationHeaders.X_SIZE.getValue(), "20"))
                .andExpect(header().string(PaginationHeaders.X_TOTAL_ELEMENTS.getValue(), notNullValue()))
                .andExpect(header().string(PaginationHeaders.X_TOTAL_PAGES.getValue(), notNullValue()))
                .andExpect(header().string(SortHeaders.X_SORT.getValue(), "title"))
                .andExpect(header().string(SortHeaders.X_SORT_DIR.getValue(), Sort.Direction.ASC.name()))
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    /**
     * Tests that the API returns a list of todos with the specified sorting and direction.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void listTodos_sorting_andDirection() throws Exception {
        mockMvc.perform(get("/todos").param("sort", "-completed"))
                .andExpect(status().isOk())
                .andExpect(header().string(SortHeaders.X_SORT.getValue(), "completed"))
                .andExpect(header().string(SortHeaders.X_SORT_DIR.getValue(), Sort.Direction.DESC.name()));
    }

    /**
     * Tests that the API returns a list of todos with case-insensitive sorting for non-default values.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void listTodos_sorting_isCaseInsensitive_nonDefaultPropertyAndDirection() throws Exception {
        mockMvc.perform(get("/todos").param("sort", "-COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Sort", "completed"))
                .andExpect(header().string("X-Sort-Dir", Sort.Direction.DESC.name()));
    }

    /**
     * Tests that the API returns a list of todos with the default sorting when the specified sort property is invalid.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void listTodos_fallbackWhenInvalidSortProperty() throws Exception {
        mockMvc.perform(get("/todos").param("sort", "-doesNotExist"))
                .andExpect(status().isOk())
                .andExpect(header().string(SortHeaders.X_SORT.getValue(), "title"))
                .andExpect(header().string(SortHeaders.X_SORT_DIR.getValue(), Sort.Direction.DESC.name())); // direction kept, key fell back
    }

    /**
     * Tests that the API returns a list of todos with the specified query filter.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void listTodos_queryFilterReturnsExpectedResults() throws Exception {
        mockMvc.perform(get("/todos").param("q", "grocer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", everyItem(containsStringIgnoringCase("grocer"))));
    }

    /**
     * Tests that the API returns a specific todo when it exists.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getTodo_found() throws Exception {
        mockMvc.perform(get("/todos/{id}", "073c98bc-4fa0-4ede-b121-6be06c25977f"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("073c98bc-4fa0-4ede-b121-6be06c25977f")));
    }

    /**
     * Tests that the API returns a 404 Not Found status when the specific todo does not exist.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getTodo_notFound() throws Exception {
        mockMvc.perform(get("/todos/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests that creating a new todo returns the expected created response.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void createTodo_returnsCreated() throws Exception {
        TodoCreateDto todoCreateDto = new TodoCreateDto().title("Write some tests");

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(todoCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Write some tests")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    /**
     * Tests that creating a new todo with an empty or null title returns a bad request response.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void createTodo_emptyOrNullTitle_returnsBadRequest() throws Exception {
        // Empty title
        TodoCreateDto emptyTitle = new TodoCreateDto().title("");
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emptyTitle)))
                .andExpect(status().isBadRequest());

        // Null title (no field at all)
        String nullTitleJson = "{}";
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullTitleJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that updating an existing todo returns the expected OK response.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void updateTodo_returnsOk() throws Exception {
        String id = createTestTodo("Write some more tests");

        TodoUpdateDto todoUpdateDto = new TodoUpdateDto().title("Updated Title").completed(true);

        mockMvc.perform(put("/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(todoUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    /**
     * Tests that updating a non-existing todo returns a Not Found response.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void updateTodo_returnsNotFound() throws Exception {
        String missingId = "00000000-0000-0000-0000-000000000000";

        TodoUpdateDto todoUpdateDto = new TodoUpdateDto().title("Does not matter").completed(true);

        mockMvc.perform(put("/todos/{id}", missingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(todoUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTodo_emptyOrNullTitle_returnsBadRequest() throws Exception {
        String id = createTestTodo("Title will be updated");

        // Empty title
        TodoUpdateDto emptyTitle = new TodoUpdateDto().title("").completed(false);
        mockMvc.perform(put("/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emptyTitle)))
                .andExpect(status().isBadRequest());

        // Null title
        TodoUpdateDto nullTitle = new TodoUpdateDto().title(null).completed(false);
        mockMvc.perform(put("/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(nullTitle)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that patching an existing todo returns the expected OK response.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void patchTodo_returnsOk() throws Exception {
        String id = createTestTodo("Patch me");

        TodoUpdateDto todoUpdateDto = new TodoUpdateDto().title("Patched Title");

        mockMvc.perform(patch("/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(todoUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Patched Title")));
    }

    /**
     * Tests that patching a non-existing todo returns a Not Found response.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void patchTodo_returnsNotFound() throws Exception {
        String missingId = "00000000-0000-0000-0000-000000000000";

        TodoUpdateDto todoUpdateDto = new TodoUpdateDto().title("Patched Title");

        mockMvc.perform(patch("/todos/{id}", missingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(todoUpdateDto)))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests that deleting an existing todo returns the expected No Content response and that later retrieval returns Not Found.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void deleteTodo_returnsNoContent_andThenNotFoundOnGet() throws Exception {
        String id = createTestTodo("Delete me");

        mockMvc.perform(delete("/todos/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/todos/{id}", id))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests that deleting a non-existing todo returns a Not Found response.
     * @throws Exception if an error occurs during the test
     */
    @Test
    void deleteTodo_returnsNotFound() throws Exception {
        String missingId = "00000000-0000-0000-0000-000000000000";

        mockMvc.perform(delete("/todos/{id}", missingId))
                .andExpect(status().isNotFound());
    }

    /**
     * Creates a new test Todo item by sending a POST request to the "/todos" API.
     * @param title the title of the Todo to be created
     * @return the ID of the created Todo as a String
     * @throws Exception if an error occurs during the request or response processing
     */
    private String createTestTodo(String title) throws Exception {
        byte[] originalTodoResponse = mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TodoCreateDto().title(title))))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        return objectMapper.readTree(originalTodoResponse).get("id").asText();
    }
}
