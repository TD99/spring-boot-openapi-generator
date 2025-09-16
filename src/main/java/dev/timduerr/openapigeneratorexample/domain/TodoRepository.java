package dev.timduerr.openapigeneratorexample.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * TodoRepository.
 *
 * @author Tim Dürr
 * @version 1.0
 */
public interface TodoRepository extends JpaRepository<TodoEntity, UUID> {

    Page<TodoEntity> findByTitleContainingIgnoreCase(String q, Pageable pageable);
}
