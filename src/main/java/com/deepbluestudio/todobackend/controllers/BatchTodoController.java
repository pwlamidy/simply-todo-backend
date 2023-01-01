package com.deepbluestudio.todobackend.controllers;

import com.deepbluestudio.todobackend.models.Todo;
import com.deepbluestudio.todobackend.repository.TodoRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/batch")
@Tag(name = "Batch - Todo")
@SecurityRequirement(name = "bearerAuth")
public class BatchTodoController {
    @Autowired
    TodoRepository todoRepository;

    private static final Logger logger = LoggerFactory.getLogger(BatchTodoController.class);

    @PostMapping(value = "/todos")
    public ResponseEntity<?> batchUpdate(@RequestBody List<Todo> todoList) {
        try {
            List<Todo> todosToUpdate = new ArrayList<>();
            for (Todo t : todoList) {
                Optional<Todo> optionalTodo = todoRepository.findById(t.getId());
                if (optionalTodo.isPresent()) {
                    Todo existingTodo = optionalTodo.get();

                    // support batch update "completed" status
                    existingTodo.setCompleted(t.getCompleted());

                    todosToUpdate.add(existingTodo);
                }
            }

            List<Todo> saveAllResult = todoRepository.saveAll(todosToUpdate);

            return new ResponseEntity<>(saveAllResult, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/todos")
    public ResponseEntity<?> batchDelete(@RequestBody List<Todo> todoList) {
        try {
            List<Todo> todosToDelete = new ArrayList<>();
            for (Todo t : todoList) {
                Optional<Todo> optionalTodo = todoRepository.findById(t.getId());
                if (optionalTodo.isPresent()) {
                    todosToDelete.add(optionalTodo.get());
                }
            }

            todoRepository.deleteAll(todosToDelete);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
