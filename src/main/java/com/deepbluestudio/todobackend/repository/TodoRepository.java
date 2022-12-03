package com.deepbluestudio.todobackend.repository;

import com.deepbluestudio.todobackend.models.Todo;
import com.deepbluestudio.todobackend.repository.dto.TodoCount;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Hidden
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findAllByDateBetween(Date startDate, Date endDate, Pageable pageable);

    @Query(name = "find_todocount_dto", nativeQuery = true)
    List<TodoCount> countTotalTodosByDateClass(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
