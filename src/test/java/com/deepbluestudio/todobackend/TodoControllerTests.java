package com.deepbluestudio.todobackend;

import com.deepbluestudio.todobackend.models.Todo;
import com.deepbluestudio.todobackend.models.User;
import com.deepbluestudio.todobackend.repository.TodoRepository;
import com.deepbluestudio.todobackend.repository.dto.TodoCount;
import com.deepbluestudio.todobackend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.*;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TodoControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    TodoRepository todoRepository;

    @MockBean
    private UserService userService;

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
    public void getTodoShouldBeSuccess() throws Exception {
        final List<Todo> todoList = new ArrayList<>();
        todoList.add(new Todo(testUUID, "title 1", null, null, null, null, null, null, testUser));
        todoList.add(new Todo(testUUID2, "title 2", null, null, null, null, null, null, testUser));

        Page<Todo> aMockPage = generateMockPage(todoList);

        when(userService.getUser()).thenReturn(testUser);
        when(todoRepository.findAllByUser(Mockito.any(User.class), Mockito.any(Pageable.class))).thenReturn(aMockPage);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(testUUID.toString()));
    }

    @Test
    public void findAllByDateBetweenShouldBeSuccess() throws Exception {
        Date now = new Date();

        final List<Todo> todoList = new ArrayList<>();
        todoList.add(new Todo(testUUID, "title 1", null, null, null, null, now, now, testUser));
        todoList.add(new Todo(testUUID2, "title 2", null, null, null, null, now, now, testUser));

        Page<Todo> aMockPage = generateMockPage(todoList);

        when(userService.getUser()).thenReturn(testUser);
        when(todoRepository.findAllByUserAndDateBetween(Mockito.any(User.class), any(Date.class), any(Date.class), Mockito.any(Pageable.class)))
                .thenReturn(aMockPage);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/todos")
                        .param("date_gte", "2022-11-28T16:00:00.000Z")
                        .param("date_lte", "2022-11-28T16:00:00.000Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(testUUID.toString()));
    }

    @Test
    public void createTodoShouldBeSuccess() throws Exception {
        Todo mockTodo = new Todo(testUUID, "title 1", null, null, null, null, new Date(), new Date(), testUser);

        when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(mockTodo);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/todos")
                        .content("{\"title\":\"title 1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("title 1"));
    }

    @Test
    public void updateTodoShouldBeSuccess() throws Exception {
        Todo mockTodo = new Todo(testUUID, "title 1", null, null, null, null, new Date(), new Date(), testUser);

        when(todoRepository.findById(mockTodo.getId())).thenReturn(Optional.of(mockTodo));

        mockTodo.setCompleted(true);
        when(todoRepository.save(mockTodo)).thenReturn(mockTodo);

        this.mockMvc.perform(MockMvcRequestBuilders.patch("/api/todos/"+testUUID)
                        .content("{\"completed\":true}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed").value(true));
    }

    @Test
    public void deleteTodoShouldBeSuccess() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/todos/"+testUUID))
                .andExpect(status().isOk());
    }

    @Test
    public void countTodosByDateShouldBeSuccess() throws Exception {
        Date now = new Date();

        final List<TodoCount> todoCountList = new ArrayList<>();
        todoCountList.add(new TodoCount(now, 2L));

        when(userService.getUser()).thenReturn(testUser);
        when(todoRepository.countTotalTodosByDateClass(any(), any(Date.class), any(Date.class))).thenReturn(todoCountList);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/todos/count-by-date")
                        .param("date_gte", "2022-11-28T16:00:00.000Z")
                        .param("date_lte", "2022-11-28T16:00:00.000Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].total").value(2));
    }

    private Page<Todo> generateMockPage(List<Todo> todoList) {
        return new MockPage(todoList);
    }

    private class MockPage implements Page<Todo> {
        private final List<Todo> mockContent;

        public MockPage(List<Todo> mockContent) {
            this.mockContent = mockContent;
        }

        @Override
        public int getTotalPages() {
            return 0;
        }

        @Override
        public long getTotalElements() {
            return this.mockContent.size();
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
            return this.mockContent;
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
        public <U> Page<U> map(Function<? super Todo, ? extends U> converter) {
            return null;
        }

        @Override
        public Iterator<Todo> iterator() {
            return null;
        }
    }
}
