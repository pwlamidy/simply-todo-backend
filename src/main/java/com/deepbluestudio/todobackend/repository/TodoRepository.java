package com.deepbluestudio.todobackend.repository;

import com.deepbluestudio.todobackend.models.Todo;
import com.deepbluestudio.todobackend.models.User;
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
import java.util.Optional;
import java.util.UUID;

@Hidden
@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {
    Page<Todo> findAllByUserAndDateBetween(User user, Date startDate, Date endDate, Pageable pageable);

    Page<Todo> findAllByUser(User user, Pageable pageable);

    Optional<Todo> findByUserAndId(User user, UUID id);

    @Query(name = "find_todocount_dto", nativeQuery = true)
    List<TodoCount> countTotalTodosByDateClass(UUID userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
