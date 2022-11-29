package com.deepbluestudio.todobackend.repository;

import com.deepbluestudio.todobackend.models.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findAllByDateBetween(Date startDate, Date endDate, Pageable pageable);
}
