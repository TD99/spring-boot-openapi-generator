package dev.timduerr.openapigeneratorexample.api;

import dev.timduerr.openapigeneratorexample.domain.TodoEntity;
import dev.timduerr.openapigeneratorexample.domain.TodoRepository;
import dev.timduerr.openapigeneratorexample.model.TodoCreateDto;
import dev.timduerr.openapigeneratorexample.model.TodoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TodosDelegateImpl.
 *
 * @author Tim Dürr
 * @version 1.0
 */
@Service
public class TodosDelegateImpl implements TodosApiDelegate {

    private final TodoRepository todoRepository;

    public TodosDelegateImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    private static TodoDto toDto(TodoEntity entity) {
        TodoDto dto = new TodoDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDone(entity.isCompleted());
        return dto;
    }

    private static List<TodoDto> toDto(List<TodoEntity> entities) {
        return entities.stream().map(TodosDelegateImpl::toDto).toList();
    }

    private static TodoEntity toEntity(TodoCreateDto dto) {
        TodoEntity entity = new TodoEntity();
        entity.setTitle(dto.getTitle());
        return entity;
    }

    @Override
    public ResponseEntity<List<TodoDto>> listTodos() {
        List<TodoEntity> entityList = todoRepository.findAll();
        List<TodoDto> dtoList = toDto(entityList);
        return ResponseEntity.ok(dtoList);
    }

    @Override
    public ResponseEntity<TodoDto> getTodo(UUID id) {
        Optional<TodoEntity> todoOptional = todoRepository.findById(id);
        return todoOptional.map(e -> ResponseEntity.ok(toDto(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<TodoDto> createTodo(TodoCreateDto todoCreateDto) {
        TodoEntity newEntity = toEntity(todoCreateDto);
        TodoEntity savedEntity = todoRepository.save(newEntity);
        return ResponseEntity.ok(toDto(savedEntity));
    }
}
