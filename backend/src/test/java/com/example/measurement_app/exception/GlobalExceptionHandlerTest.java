package com.example.measurement_app.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

class GlobalExceptionHandlerTest {

    @RestController
    @RequestMapping("/test")
    @Validated
    static class TestController {

        @GetMapping("/type-mismatch")
        public void triggerTypeMismatch(@RequestParam Integer param) {
            // This will throw MethodArgumentTypeMismatchException
        }

        @PostMapping("/validation")
        public void triggerValidation(@Valid @RequestBody TestRequest request) {
            // This will throw MethodArgumentNotValidException
        }

        @GetMapping("/constraint-violation")
        public void triggerConstraintViolation(@RequestParam @NotNull String param) {
            // This will throw ConstraintViolationException
        }

        @GetMapping("/generic")
        public void triggerGenericException() {
            throw new RuntimeException("Something went wrong");
        }

        static class TestRequest {
            @NotNull
            private String field;

            public String getField() {
                return field;
            }

            public void setField(String field) {
                this.field = field;
            }
        }
    }

    @Autowired
    private MockMvc mockMvc;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void testHandleMethodArgumentTypeMismatchException() throws Exception {
        mockMvc.perform(get("/test/type-mismatch")
                .param("param", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid argument type"));
    }

    @Test
    void testHandleMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("must not be null"));
    }

    @Test
    void testHandleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }
}
