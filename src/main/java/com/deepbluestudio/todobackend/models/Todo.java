package com.deepbluestudio.todobackend.models;

import com.deepbluestudio.todobackend.repository.dto.TodoCount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@NamedNativeQuery(
        name = "find_todocount_dto",
        query = "SELECT DATE(t.date) AS date, COUNT(t.date) AS total " +
                "FROM Todo AS t " +
                "WHERE t.user_id = :userId AND DATE(t.date) between :startDate and :endDate " +
                "GROUP BY DATE(t.date) " +
                "ORDER BY DATE(t.date)",
        resultSetMapping = "todocount_dto"
)
@SqlResultSetMapping(
        name = "todocount_dto",
        classes = @ConstructorResult(
                targetClass = TodoCount.class,
                columns = {
                        @ColumnResult(name = "date", type = Date.class),
                        @ColumnResult(name = "total", type = Long.class),
                }
        )
)
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private String title;

    private String details;

    private Date date;

    private Date time;

    private Boolean completed;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    @JsonIgnore
    private User user;

    public Todo() {

    }

    public Todo(UUID id, String title, String details, Date date, Date time, Boolean completed, Date createdAt, Date updatedAt, User user) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.date = date;
        this.time = time;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
