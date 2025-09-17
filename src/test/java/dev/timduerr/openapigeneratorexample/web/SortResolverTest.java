package dev.timduerr.openapigeneratorexample.web;

import dev.timduerr.openapigeneratorexample.domain.TodoEntity;
import dev.timduerr.openapigeneratorexample.model.TodoDto;
import dev.timduerr.openapigeneratorexample.web.SortResolver.DefaultSort;
import dev.timduerr.openapigeneratorexample.web.SortResolver.SortResolution;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SortResolverTest.
 *
 * @author Tim Dürr
 * @version 1.0
 */
class SortResolverTest {

    private static final DefaultSort DEFAULT_SORT = new DefaultSort("title", Sort.Direction.ASC);

    /**
     * Tests the default sort resolution when the sort string is null or blank.
     */
    @Test
    void resolve_default_whenNullOrBlank() {
        SortResolution sortResolution = SortResolver.resolve(TodoDto.class, TodoEntity.class, null, DEFAULT_SORT);

        assertEquals("title", sortResolution.appliedKey(), "Expected the default sort property to be used");
        assertEquals(Sort.Direction.ASC, sortResolution.appliedDirection(), "Expected the default sort direction to be used");
        assertEquals(Sort.by(Sort.Order.by("title")), sortResolution.sort(), "Expected the default sort to be applied");
    }

    /**
     * Tests the sort resolution when the sort string contains a valid property and direction.
     */
    @Test
    void resolve_acceptsKnownPropertyAndDirection() {
        SortResolution sortResolution = SortResolver.resolve(TodoDto.class, TodoEntity.class, "-completed", DEFAULT_SORT);

        assertEquals("completed", sortResolution.appliedKey(), "Expected the property to be used");
        assertEquals(Sort.Direction.DESC, sortResolution.appliedDirection(), "Expected the direction to be used");
    }

    /**
     * Tests the sort resolution when the sort string contains a valid property and direction but is not case-sensitive.
     */
    @Test
    void resolve_isCaseInsensitive_forNonDefaultPropertyAndDirection() {
        SortResolution sortResolution = SortResolver.resolve(TodoDto.class, TodoEntity.class, "-COMPLETED", DEFAULT_SORT);

        assertEquals("completed", sortResolution.appliedKey(), "Expected the property to be used");
        assertEquals(Sort.Direction.DESC, sortResolution.appliedDirection(), "Expected the direction to be used");
    }

    /**
     * Tests the sort resolution when the sort string contains an unknown property.
     */
    @Test
    void resolve_fallsBackIfUnknownProperty() {
        SortResolution sortResolution = SortResolver.resolve(TodoDto.class, TodoEntity.class, "-doesNotExist", DEFAULT_SORT);

        assertEquals("title", sortResolution.appliedKey(), "Expected the default sort property to be used");
        assertEquals(Sort.Direction.DESC, sortResolution.appliedDirection(), "Expected the default sort direction to be used");
    }
}
