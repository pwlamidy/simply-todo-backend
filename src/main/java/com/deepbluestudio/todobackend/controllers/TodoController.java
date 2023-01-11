package com.deepbluestudio.todobackend.controllers;

import com.deepbluestudio.todobackend.models.Todo;
import com.deepbluestudio.todobackend.models.User;
import com.deepbluestudio.todobackend.payload.response.EStatus;
import com.deepbluestudio.todobackend.payload.response.ResponseHandler;
import com.deepbluestudio.todobackend.repository.TodoRepository;
import com.deepbluestudio.todobackend.repository.dto.TodoCount;
import com.deepbluestudio.todobackend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/todos")
@Tag(name = "Todo")
@SecurityRequirement(name = "bearerAuth")
public class TodoController {
    @Autowired
    private UserService userService;

    final TodoRepository todoRepository;

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @Autowired
    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable("id") UUID id) {
        User user = userService.getUser();

        Optional<Todo> todo = todoRepository.findByUserAndId(user, id);

        return ResponseHandler.generateResponse(EStatus.SUCCESS.getStatus(), HttpStatus.OK, todo);
    }

    @GetMapping
    public ResponseEntity<?> getTodos(@RequestParam(value = "date_gte", required = false)
                                      @Parameter(description = "Date greater than or equal to value. Support ISO format (ISO 8601)",
                                              example = "2022-11-27T16:00:00.000Z") String startDate,
                                      @RequestParam(value = "date_lte", required = false)
                                      @Parameter(description = "Date less than or equal to value. Support ISO format (ISO 8601)",
                                              example = "2022-11-27T16:00:00.000Z") String endDate,
                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", defaultValue = "20") Integer size,
                                      @RequestParam(value = "sort", defaultValue = "updatedAt")
                                      @Parameter(description = "Support 'Todo' schema only") String sort,
                                      @RequestParam(value = "order", defaultValue = "desc") String order) {
        User user = userService.getUser();

        Page<Todo> todos;
        PageRequest pageRequest = PageRequest.of(
                page - 1,
                size,
                order.equals("desc")
                        ? Sort.by(sort).descending()
                        : Sort.by(sort).ascending());

        if (startDate == null || endDate == null) {
            todos = todoRepository.findAllByUser(user, pageRequest);
        } else {
            OffsetDateTime parsedStartDateTime = OffsetDateTime.parse(startDate);
            OffsetDateTime parsedEndDate = OffsetDateTime.parse(endDate);

            Date startDateTime = Date.from(parsedStartDateTime.toInstant());
            Date endDateTime = Date.from(parsedEndDate.toInstant());

            logger.error(startDateTime + ", " + endDateTime);

            todos = todoRepository.findAllByUserAndDateBetween(user, startDateTime, endDateTime, pageRequest);
        }
        return ResponseHandler.generateResponseWithPaging(
                EStatus.SUCCESS.getStatus(),
                HttpStatus.OK,
                todos.getContent(),
                todos.getPageable(),
                todos.getTotalElements());
    }

    @PostMapping
    public ResponseEntity<?> addTodo(@RequestBody Todo todo) {
        Object saveResult = todoRepository.save(todo);
        return ResponseHandler.generateResponse(EStatus.SUCCESS.getStatus(), HttpStatus.OK, saveResult);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putTodo(@PathVariable("id") UUID id, @RequestBody Todo todo) {
        Object updateResult = null;
        Optional<Todo> existingTodo = todoRepository.findById(id);
        if (existingTodo.isPresent()) {
            updateResult = todoRepository.save(todo);
        }
        return ResponseHandler.generateResponse(EStatus.SUCCESS.getStatus(), HttpStatus.OK, updateResult);
    }

    @Operation(description = "Currently support 'completed' status update only")
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchTodo(@PathVariable("id") UUID id, @RequestBody Todo todo) {
        Object updateResult = null;
        Optional<Todo> existingTodo = todoRepository.findById(id);
        if (existingTodo.isPresent()) {
            Todo todoToUpdate = existingTodo.get();
            todoToUpdate.setCompleted(todo.getCompleted());
            updateResult = todoRepository.save(todoToUpdate);
        }
        return ResponseHandler.generateResponse(EStatus.SUCCESS.getStatus(), HttpStatus.OK, updateResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable("id") UUID id) {
        todoRepository.deleteById(id);
        return ResponseHandler.generateResponseWithoutData(EStatus.SUCCESS.getStatus(), HttpStatus.OK);
    }

    @GetMapping("/count-by-date")
    public ResponseEntity<?> countTodosByDate(@RequestParam(value = "date_gte")
                                              @Parameter(description = "Date greater than or equal to value. Support ISO format (ISO 8601)",
                                                      example = "2022-11-27T16:00:00.000Z") String startDate,
                                              @RequestParam(value = "date_lte")
                                              @Parameter(description = "Date less than or equal to value. Support ISO format (ISO 8601)",
                                                      example = "2022-11-27T16:00:00.000Z") String endDate) {
        User user = userService.getUser();

        OffsetDateTime parsedStartDateTime = OffsetDateTime.parse(startDate);
        OffsetDateTime parsedEndDate = OffsetDateTime.parse(endDate);

        Date startDateTime = Date.from(parsedStartDateTime.toInstant());
        Date endDateTime = Date.from(parsedEndDate.toInstant());

        List<TodoCount> todoCountList = todoRepository.countTotalTodosByDateClass(user.getId(), startDateTime, endDateTime);

        return ResponseHandler.generateResponse(EStatus.SUCCESS.getStatus(), HttpStatus.OK, todoCountList);
    }
}
