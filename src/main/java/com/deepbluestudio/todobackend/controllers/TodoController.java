package com.deepbluestudio.todobackend.controllers;

import com.deepbluestudio.todobackend.models.Todo;
import com.deepbluestudio.todobackend.payload.response.EStatus;
import com.deepbluestudio.todobackend.payload.response.ResponseHandler;
import com.deepbluestudio.todobackend.repository.TodoRepository;
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
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/todos")
public class TodoController {
    final TodoRepository todoRepository;

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @Autowired
    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable("id") Long id) {
        Optional<Todo> todo = todoRepository.findById(id);
        return ResponseHandler.generateResponse(EStatus.SUCCESS.getStatus(), HttpStatus.OK, todo);
    }

    @GetMapping
    public ResponseEntity<?> getTodos(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", defaultValue = "20") Integer size,
                                      @RequestParam(value = "sort", defaultValue = "updatedAt") String sort,
                                      @RequestParam(value = "order", defaultValue = "desc") String order) {
        Page<Todo> todos = todoRepository.findAll(PageRequest.of(
                page - 1,
                size,
                order.equals("desc")
                        ? Sort.by(sort).descending()
                        : Sort.by(sort).ascending()));
        return ResponseHandler.generateResponseWithPaging(EStatus.SUCCESS.getStatus(), HttpStatus.OK,
                todos.getContent(), todos.getPageable(), todos.getTotalElements());
    }

    @GetMapping("/findAllByDateBetween")
    public ResponseEntity<?> findAllByDateBetween(@RequestParam(value = "startDate") String startDate,
                                                  @RequestParam(value = "endDate") String endDate,
                                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "20") Integer size,
                                                  @RequestParam(value = "sort", defaultValue = "updatedAt") String sort,
                                                  @RequestParam(value = "order", defaultValue = "desc") String order) {

        OffsetDateTime parsedStartDateTime = OffsetDateTime.parse(startDate);
        OffsetDateTime parsedEndDate = OffsetDateTime.parse(endDate);

        Date startDateTime = Date.from(parsedStartDateTime.toInstant());
        Date endDateTime = Date.from(parsedEndDate.toInstant());

        logger.error(startDateTime + ", " + endDateTime);

        Page<Todo> todos = todoRepository.findAllByDateBetween(
                startDateTime,
                endDateTime,
                PageRequest.of(
                        page - 1,
                        size,
                        order.equals("desc")
                                ? Sort.by(sort).descending()
                                : Sort.by(sort).ascending()));
        return ResponseHandler.generateResponseWithPaging(EStatus.SUCCESS.getStatus(), HttpStatus.OK, todos.getContent(), todos.getPageable(), todos.getTotalElements());
    }

    @PostMapping
    public ResponseEntity<?> addTodo(@RequestBody Todo todo) {
        Object saveResult = todoRepository.save(todo);
        return ResponseHandler.generateResponse(EStatus.SUCCESS.getStatus(), HttpStatus.OK, saveResult);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putTodo(@PathVariable("id") Long id, @RequestBody Todo todo) {
        Object updateResult = null;
        Optional<Todo> existingTodo = todoRepository.findById(id);
        if (existingTodo.isPresent()) {
            updateResult = todoRepository.save(todo);
        }
        return ResponseHandler.generateResponse(EStatus.SUCCESS.getStatus(), HttpStatus.OK, updateResult);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchTodo(@PathVariable("id") Long id, @RequestBody Todo todo) {
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
    public ResponseEntity<?> deleteTodo(@PathVariable("id") Long id) {
        todoRepository.deleteById(id);
        return ResponseHandler.generateResponseWithoutData(EStatus.SUCCESS.getStatus(), HttpStatus.OK);
    }
}
