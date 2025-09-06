package dev.timduerr.openapigeneratorexample.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TodosApiImpl.
 *
 * @author Tim Dürr
 * @version 1.0
 */
@RestController
public class TodosApiImpl implements dev.timduerr.openapigeneratorexample.api.TodosApi {

    private final Map<UUID, dev.timduerr.openapigeneratorexample.model.Todo> store = new ConcurrentHashMap<>();

    @Override
    public ResponseEntity<List<dev.timduerr.openapigeneratorexample.model.Todo>> todosGet() {
        return ResponseEntity.ok(new ArrayList<>(store.values()));
    }

    @Override
    public ResponseEntity<dev.timduerr.openapigeneratorexample.model.Todo> todosIdGet(UUID id) {
        dev.timduerr.openapigeneratorexample.model.Todo t = store.get(id);
        return (t == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(t);
    }

    @Override
    public ResponseEntity<dev.timduerr.openapigeneratorexample.model.Todo> todosPost(@Valid @RequestBody dev.timduerr.openapigeneratorexample.model.TodoCreate body) {
        UUID id = UUID.randomUUID();
        dev.timduerr.openapigeneratorexample.model.Todo t = new dev.timduerr.openapigeneratorexample.model.Todo().id(id).title(body.getTitle()).done(false);
        store.put(id, t);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(t);
    }
}
