package com.deepbluestudio.todobackend.repository;

import com.deepbluestudio.todobackend.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
