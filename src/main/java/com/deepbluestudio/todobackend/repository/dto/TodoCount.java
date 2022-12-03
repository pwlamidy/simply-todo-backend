package com.deepbluestudio.todobackend.repository.dto;

import java.util.Date;

public class TodoCount {
    private Date date;
    private Long total;

    public TodoCount(Date date, Long total) {
        this.date = date;
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
