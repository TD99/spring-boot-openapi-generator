package dev.timduerr.openapigeneratorexample.mapper;

import dev.timduerr.openapigeneratorexample.domain.TodoEntity;
import dev.timduerr.openapigeneratorexample.model.TodoCreateDto;
import dev.timduerr.openapigeneratorexample.model.TodoDto;
import dev.timduerr.openapigeneratorexample.model.TodoPatchDto;
import dev.timduerr.openapigeneratorexample.model.TodoUpdateDto;
import org.apache.commons.lang3.SerializationUtils;

import java.util.UUID;

/**
 * TodoMapper.
 *
 * @author Tim Dürr
 * @version 1.0
 */
public class TodoMapper {

    private TodoMapper() {
    }

    // -----------------------------------------------------------------------------------------------------------------
    // TodoDto <-- TodoEntity
    /**
     * Converts a {@link TodoEntity} object to a {@link TodoDto} object.
     *
     * <p>This method maps the fields of a {@link TodoEntity} to a new instance of {@link TodoDto}.
     *
     * @param entity the {@link TodoEntity} to be converted
     * @return a {@link TodoDto} object containing the mapped data
     */
    public static TodoDto toTodoDto(TodoEntity entity) {
        TodoDto dto = new TodoDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCompleted(entity.isCompleted());
        return dto;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // TodoCreateDto --> TodoEntity
    /**
     * Converts a {@link TodoCreateDto} object to a {@link TodoEntity} object.
     *
     * <p>This method maps the title field from the input {@link TodoCreateDto} to a corresponding {@link TodoEntity}.
     *
     * @param dto the {@link TodoCreateDto} object to be converted
     * @return a {@link TodoEntity} object containing the mapped data
     */
    public static TodoEntity toTodoEntity(TodoCreateDto dto) {
        TodoEntity entity = new TodoEntity();
        entity.setTitle(dto.getTitle());
        return entity;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // TodoUpdateDto --> TodoEntity
    /**
     * Converts a {@link TodoUpdateDto} object and an ID into a {@link TodoEntity}.
     *
     * <p>This method maps the fields of a {@link TodoUpdateDto} and assigns the given ID
     * to create a new {@link TodoEntity}.
     *
     * @param id the unique identifier for the {@link TodoEntity}
     * @param dto the {@link TodoUpdateDto} containing the updates to be mapped
     **/
    public static TodoEntity toTodoEntity(UUID id, TodoUpdateDto dto) {
        TodoEntity entity = new TodoEntity();
        entity.setId(id);
        entity.setTitle(dto.getTitle());
        entity.setCompleted(dto.getCompleted());
        return entity;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // TodoEntity + TodoPatchDto --> TodoEntity
    /**
     * Updates a {@link TodoEntity} object with values from a {@link TodoPatchDto}.
     *
     * <p>This method creates a copy of the provided entity and modifies its fields based on the
     * non-null values present in the given DTO.
     *
     * @param entity the existing {@link TodoEntity} to be updated
     * @param dto the {@link TodoPatchDto} containing the fields to be updated
     * @return a new {@link TodoEntity} instance with the applied updates
     */
    public static TodoEntity toTodoEntity(TodoEntity entity, TodoPatchDto dto) {
        TodoEntity patchedEntity = SerializationUtils.clone(entity);

        if (dto.getTitle() != null) {
            patchedEntity.setTitle(dto.getTitle());
        }

        if (dto.getCompleted() != null) {
            patchedEntity.setCompleted(dto.getCompleted());
        }

        return patchedEntity;
    }
}
