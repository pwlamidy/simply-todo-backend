package com.deepbluestudio.todobackend;

import com.deepbluestudio.todobackend.models.Todo;
import com.deepbluestudio.todobackend.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    TodoRepository todoRepository;

    @Test
    public void getTodoShouldBeSuccess() throws Exception {
        Page<Todo> aMockPage = generateMockPage();

        when(todoRepository.findAll(Mockito.any(Pageable.class))).thenReturn(aMockPage);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.total").value(aMockPage.getTotalElements()));
    }

    @Test
    public void createTodoShouldBeSuccess() throws Exception {
        Todo mockTodo = new Todo(1L, "title 1", null, null, null, null, new Date(), new Date());

        when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(mockTodo);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/todos")
                        .content("{\"title\":\"title 1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("title 1"));
    }

    @Test
    public void updateTodoShouldBeSuccess() throws Exception {
        Todo mockTodo = new Todo(1L, "title 1", null, null, null, null, new Date(), new Date());

        when(todoRepository.findById(mockTodo.getId())).thenReturn(Optional.of(mockTodo));

        mockTodo.setCompleted(true);
        when(todoRepository.save(mockTodo)).thenReturn(mockTodo);

        this.mockMvc.perform(MockMvcRequestBuilders.patch("/api/todos/1")
                        .content("{\"completed\":true}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed").value(true));
    }

    @Test
    public void deleteTodoShouldBeSuccess() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/todos/5"))
                .andExpect(status().isOk());
    }

    private Page<Todo> generateMockPage() {
        return new Page<>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super Todo, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Todo> getContent() {
                return null;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return Sort.by("updatedAt").descending();
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Todo> iterator() {
                return null;
            }
        };
    }
}
