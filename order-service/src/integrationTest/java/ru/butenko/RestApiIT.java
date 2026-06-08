package ru.butenko;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestApiIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnSeedCarFromCatalogEndpoint() throws Exception {
        mockMvc.perform(get("/api/cars")
                        .with(auth())
                        .param("brand", "BMW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("55555555-5555-5555-5555-555555555555"))
                .andExpect(jsonPath("$[0].brand").value("BMW"))
                .andExpect(jsonPath("$[0].modelName").value("320i"));
    }

    @Test
    void shouldBuildConfigurationThroughRestApi() throws Exception {
        String requestBody = """
                {
                  "modelId": "33333333-3333-3333-3333-333333333333",
                  "componentIds": {
                    "WHEELS": "44444444-4444-4444-4444-444444444441",
                    "TRANSMISSION": "44444444-4444-4444-4444-444444444442",
                    "STEERING_WHEEL": "44444444-4444-4444-4444-444444444443",
                    "INTERIOR": "44444444-4444-4444-4444-444444444444"
                  }
                }
                """;

        mockMvc.perform(post("/api/configurations")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelId").value("33333333-3333-3333-3333-333333333333"))
                .andExpect(jsonPath("$.price").value(3230000.00))
                .andExpect(jsonPath("$.selectedOptions.WHEELS.id")
                        .value("44444444-4444-4444-4444-444444444441"));
    }

    private RequestPostProcessor auth() {
        return jwt().authorities(() -> "ROLE_USER");
    }
}
