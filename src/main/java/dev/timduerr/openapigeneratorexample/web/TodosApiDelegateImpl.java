package dev.timduerr.openapigeneratorexample.web;

import dev.timduerr.openapigeneratorexample.domain.TodoEntity;
import dev.timduerr.openapigeneratorexample.domain.TodoRepository;
import dev.timduerr.openapigeneratorexample.mapper.TodoMapper;
import dev.timduerr.openapigeneratorexample.model.TodoCreateDto;
import dev.timduerr.openapigeneratorexample.model.TodoDto;
import dev.timduerr.openapigeneratorexample.model.TodoPatchDto;
import dev.timduerr.openapigeneratorexample.model.TodoUpdateDto;
import dev.timduerr.openapigeneratorexample.web.SortResolver.DefaultSort;
import dev.timduerr.openapigeneratorexample.web.SortResolver.SortResolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.timduerr.openapigeneratorexample.mapper.TodoMapper.*;
import static dev.timduerr.openapigeneratorexample.web.PaginationHeaders.*;
import static dev.timduerr.openapigeneratorexample.web.SortHeaders.X_SORT;
import static dev.timduerr.openapigeneratorexample.web.SortHeaders.X_SORT_DIR;

/**
 * TodosApiDelegateImpl.
 *
 * @author Tim Dürr
 * @version 1.0
 */
@Service
public class TodosApiDelegateImpl implements TodosApiDelegate {

    private final TodoRepository todoRepository;

    public TodosApiDelegateImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public ResponseEntity<List<TodoDto>> listTodos(Integer page, Integer size, String sortString, String qString) {
        int pageIndex = (page == null) ? 0 : Math.max(0, page);
        int pageSize = (size == null) ? 20 : Math.max(1, Math.min(50, size));

        DefaultSort defaultSort = new DefaultSort("title", Sort.Direction.ASC);
        SortResolution sortResolution = SortResolver.resolve(TodoEntity.class, TodoDto.class, sortString, defaultSort);
        Sort sort = sortResolution.sort().and(Sort.by(Sort.Order.asc("id"))); // ensure consistent order

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, sort);

        Page<TodoEntity> result;
        if (qString == null || qString.isBlank()) {
            result = todoRepository.findAll(pageRequest);
        } else {
            result = todoRepository.findByTitleContainingIgnoreCase(qString, pageRequest);
        }

        List<TodoDto> body = result.getContent().stream()
                .map(TodoMapper::toTodoDto)
                .toList();

        return ResponseEntity.ok()
                .header(X_PAGE.getValue(), String.valueOf(pageIndex))
                .header(X_SIZE.getValue(), String.valueOf(pageSize))
                .header(X_TOTAL_ELEMENTS.getValue(), String.valueOf(result.getTotalElements()))
                .header(X_TOTAL_PAGES.getValue(), String.valueOf(result.getTotalPages()))
                .header(X_SORT.getValue(), sortResolution.appliedKey())
                .header(X_SORT_DIR.getValue(), sortResolution.appliedDirection().name())
                .body(body);
    }

    @Override
    public ResponseEntity<TodoDto> createTodo(TodoCreateDto todoCreateDto) {
        TodoEntity newEntity = toTodoEntity(todoCreateDto);
        TodoEntity savedEntity = todoRepository.save(newEntity);
        return ResponseEntity.ok(toTodoDto(savedEntity));
    }

    @Override
    public ResponseEntity<TodoDto> getTodo(UUID id) {
        Optional<TodoEntity> todoOptional = todoRepository.findById(id);
        return todoOptional.map(e -> ResponseEntity.ok(toTodoDto(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<TodoDto> updateTodo(UUID id, TodoUpdateDto todoUpdateDto) {
        if (!todoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        TodoEntity todoEntity = toTodoEntity(id, todoUpdateDto);
        TodoEntity updatedEntity = todoRepository.save(todoEntity);
        return ResponseEntity.ok(toTodoDto(updatedEntity));
    }

    @Override
    public ResponseEntity<TodoDto> patchTodo(UUID id, TodoPatchDto todoPatchDto) {
        Optional<TodoEntity> todoOptional = todoRepository.findById(id);

        if (todoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TodoEntity todoEntity = todoOptional.get();
        TodoEntity patchedEntity = toTodoEntity(todoEntity, todoPatchDto);
        TodoEntity updatedEntity = todoRepository.save(patchedEntity);
        return ResponseEntity.ok(toTodoDto(updatedEntity));
    }

    @Override
    public ResponseEntity<Void> deleteTodo(UUID id) {
        if (!todoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        todoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
