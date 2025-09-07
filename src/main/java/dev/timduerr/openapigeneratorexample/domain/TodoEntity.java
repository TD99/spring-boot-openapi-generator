package dev.timduerr.openapigeneratorexample.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * TodoEntity.
 *
 * @author Tim Dürr
 * @version 1.0
 */
@Entity
@Table(name = "tbl_todo")
public class TodoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
