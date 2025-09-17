package dev.timduerr.openapigeneratorexample.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TodoRepositoryTest.
 *
 * @author Tim Dürr
 * @version 1.0
 */
@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;

    /**
     * Tests that the repository is seeded with data via the data.sql script and that the repository can be paginated.
     */
    @Test
    void findAll_isSeeded_viaDataSql_andPaginates() {
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("title"));
        Page<TodoEntity> pageZero = todoRepository.findAll(pageRequest);

        assertTrue(6 < pageZero.getTotalElements(), "Expected more than 5 items");
        assertEquals(5, pageZero.getContent().size(), "Expected 5 items on the first page");
    }

    /**
     * Tests that the repository can be paginated and filtered by title.
     */
    @Test
    void search_titleContainingIgnoreCase_returnsExpectedResults() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<TodoEntity> pageZero = todoRepository.findByTitleContainingIgnoreCase("grocer", pageRequest);

        assertTrue(pageZero.getContent().stream()
                .map(item -> item.getTitle().toLowerCase())
                .anyMatch(title -> title.contains("grocer")),
                "Expected 'grocer' to be in the results");
    }
}
