package com.deepbluestudio.todobackend;

import com.deepbluestudio.todobackend.models.Todo;
import com.deepbluestudio.todobackend.models.User;
import com.deepbluestudio.todobackend.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BatchTodoControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    TodoRepository todoRepository;

    private static User testUser;
    private static UUID testUUID;
    private static UUID testUUID2;

    @BeforeEach
    public void setUp() {
        testUser = new User("test", "test@test.com", "test");
        testUUID = UUID.randomUUID();
        testUUID2 = UUID.randomUUID();
    }

    @Test
    public void batchUpdateTodoShouldBeSuccess() throws Exception {
        final List<Todo> todoList = new ArrayList<>();
        todoList.add(new Todo(testUUID, "title 1", "test details", null, null, null, new Date(), new Date(), testUser));
        todoList.add(new Todo(testUUID2, "title 2", "test details", null, null, null, new Date(), new Date(), testUser));

        when(todoRepository.saveAll(ArgumentMatchers.anyList())).thenReturn(todoList);
        this.mockMvc.perform(post("/api/batch/todos")
                        .content(String.format("[{\"id\":\"%s\"},{\"id\":\"%s\"}]", testUUID.toString(), testUUID2.toString()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testUUID.toString()))
                .andExpect(jsonPath("$[1].id").value(testUUID2.toString()));
    }

    @Test
    public void batchDeleteTodoShouldBeSuccess() throws Exception {
        this.mockMvc.perform(delete("/api/batch/todos")
                        .content(String.format("[{\"id\":\"%s\"},{\"id\":\"%s\"}]", testUUID.toString(), testUUID2.toString()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
