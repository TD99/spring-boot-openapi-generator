package dev.timduerr.openapigeneratorexample.mapper;

import dev.timduerr.openapigeneratorexample.domain.TodoEntity;
import dev.timduerr.openapigeneratorexample.model.TodoCreateDto;
import dev.timduerr.openapigeneratorexample.model.TodoDto;
import dev.timduerr.openapigeneratorexample.model.TodoPatchDto;
import dev.timduerr.openapigeneratorexample.model.TodoUpdateDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TodoMapperTest.
 *
 * @author Tim Dürr
 * @version 1.0
 */
class TodoMapperTest {

    /**
     * Tests the mapping of all fields from a source entity to a Todo DTO.
     */
    @Test
    void toTodoDto_mapAllFields() {
        UUID uuid = randomUUID();

        TodoEntity entity = new TodoEntity();
        entity.setId(uuid);
        entity.setTitle("Test Title");
        entity.setCompleted(true);

        TodoDto todoDto = TodoMapper.toTodoDto(entity);

        assertEquals(uuid, todoDto.getId(), "Expected the ID to be mapped");
        assertEquals("Test Title", todoDto.getTitle(), "Expected the title to be mapped");
        assertTrue(todoDto.getCompleted(), "Expected the completed status to be mapped");
    }

    /**
     * Tests the mapping from a TodoCreateDto to a TodoEntity, ensuring that only the title is set and other fields are defaulted.
     */
    @Test
    void toTodoEntity_fromCreateDto_setsTitleOnly() {
        TodoCreateDto todoCreateDto = new TodoCreateDto().title("New Todo");
        TodoEntity entity = TodoMapper.toTodoEntity(todoCreateDto);

        assertNull(entity.getId(), "Expected the ID to be null");
        assertEquals("New Todo", entity.getTitle(), "Expected the title to be mapped");
        assertFalse(entity.isCompleted(), "Expected the completed status to be false by default");
    }

    /**
     * Tests the mapping from a TodoUpdateDto to a TodoEntity, ensuring that all fields are set.
     */
    @Test
    void toTodoEntity_fromUpdateDto_setsAllFields() {
        UUID uuid = randomUUID();

        TodoUpdateDto todoUpdateDto = new TodoUpdateDto().title("Updated Title").completed(true);
        TodoEntity entity = TodoMapper.toTodoEntity(uuid, todoUpdateDto);

        assertEquals(uuid, entity.getId(), "Expected the ID to be mapped");
        assertEquals("Updated Title", entity.getTitle(), "Expected the title to be mapped");
        assertTrue(entity.isCompleted(), "Expected the completed status to be mapped");
    }

    /**
     * Tests the mapping from a TodoPatchDto to a TodoEntity, ensuring that only non-null fields are applied and that the entity is cloned.
     */
    @Test
    void toTodoEntity_fromPatchDto_appliesOnlyNonNullFieldsAndClones() {
        TodoEntity originalTodoEntity = new TodoEntity();
        originalTodoEntity.setId(randomUUID());
        originalTodoEntity.setTitle("Original Title");
        originalTodoEntity.setCompleted(false);

        TodoPatchDto todoPatchDto = new TodoPatchDto().completed(true);

        TodoEntity patchedTodoEntity = TodoMapper.toTodoEntity(originalTodoEntity, todoPatchDto);

        assertNotSame(originalTodoEntity, patchedTodoEntity, "Expected a new instance to be created");
        assertEquals(originalTodoEntity.getId(), patchedTodoEntity.getId(), "Expected the ID to remain unchanged");
        assertEquals("Original Title", patchedTodoEntity.getTitle(), "Expected the title to remain unchanged");
        assertTrue(patchedTodoEntity.isCompleted(), "Expected the completed status to be updated");
    }
}
