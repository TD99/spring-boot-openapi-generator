package dev.timduerr.openapigeneratorexample.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CorsConfigurationTest.
 *
 * @author Tim Dürr
 * @version 1.0
 */
@WebMvcTest
@Import(CorsConfiguration.class)
public class CorsConfigurationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void cors_preflight_request_allowsConfiguredOriginAndMethods() throws Exception {
        mockMvc.perform(options("/todos")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")));
    }
}
