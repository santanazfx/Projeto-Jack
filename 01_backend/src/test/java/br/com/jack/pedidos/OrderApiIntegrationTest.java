package br.com.jack.pedidos;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:jacktest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
class OrderApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndTracksPersistedDeliveryOrder() throws Exception {
        String payload = """
                {
                  "customerName":"Cliente de Teste",
                  "phone":"(11) 99999-0000",
                  "serviceType":"DELIVERY",
                  "address":{"zipCode":"01001-000","street":"Praça da Sé","number":"10","neighborhood":"Sé","reference":"Portaria"},
                  "paymentMethod":"PIX",
                  "items":[{"productId":1,"quantity":2,"extras":["Bacon"],"removals":["Alface"],"notes":"Sem guardanapo"}]
                }
                """;

        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Pedido recebido"))
                .andExpect(jsonPath("$.serviceType").value("Delivery"))
                .andExpect(jsonPath("$.total").value(72.00))
                .andReturn().getResponse().getContentAsString();

        String trackingCode = JsonPath.read(response, "$.trackingCode");
        assertThat(trackingCode).startsWith("JACK-");
        mockMvc.perform(get("/api/orders/{trackingCode}", trackingCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingCode").value(trackingCode))
                .andExpect(jsonPath("$.address").value("Praça da Sé, 10 - Sé — Portaria"));
    }
}
