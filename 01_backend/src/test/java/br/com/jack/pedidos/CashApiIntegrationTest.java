package br.com.jack.pedidos;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:cashtest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
class CashApiIntegrationTest {
    @Autowired MockMvc mvc;

    private MockHttpSession cashierSession() throws Exception {
        return (MockHttpSession) mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"jack evandro\",\"password\":\"2026Jack\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("CAIXA"))
                .andReturn().getRequest().getSession(false);
    }

    @Test
    void managesTabItemsAvailabilityAndPayment() throws Exception {
        mvc.perform(get("/api/cash/tabs")).andExpect(status().isUnauthorized());
        MockHttpSession session = cashierSession();

        String created=mvc.perform(post("/api/cash/tabs").session(session).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"T-TESTE\",\"customerName\":\"Cliente Balcão\",\"serviceType\":\"TABLE\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();
        Integer tabId=JsonPath.read(created,"$.id");

        String withItem=mvc.perform(post("/api/cash/tabs/{id}/items",tabId).session(session).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":1,\"extras\":[\"Bacon\"],\"removals\":[\"Alface\"],\"notes\":\"Bem passado\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(33.00))
                .andReturn().getResponse().getContentAsString();
        Integer itemId=JsonPath.read(withItem,"$.items[0].id");

        mvc.perform(patch("/api/cash/tabs/{id}/items/{itemId}",tabId,itemId).session(session).contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(66.00));
        mvc.perform(post("/api/cash/tabs/{id}/items/{itemId}/cancel",tabId,itemId).session(session).contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"Cliente desistiu\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(0));
        mvc.perform(post("/api/cash/tabs/{id}/items",tabId).session(session).contentType(MediaType.APPLICATION_JSON).content("{\"productId\":2,\"quantity\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(28.00));

        mvc.perform(patch("/api/cash/products/2/availability").session(session).contentType(MediaType.APPLICATION_JSON).content("{\"available\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.available").value(false));
        mvc.perform(post("/api/cash/tabs/{id}/items",tabId).session(session).contentType(MediaType.APPLICATION_JSON).content("{\"productId\":2,\"quantity\":1}"))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.message").value("Produto indisponível."));
        mvc.perform(patch("/api/cash/products/2/availability").session(session).contentType(MediaType.APPLICATION_JSON).content("{\"available\":true}"))
                .andExpect(status().isOk());

        mvc.perform(post("/api/cash/tabs/{id}/finalize",tabId).session(session).contentType(MediaType.APPLICATION_JSON).content("{\"paymentMethod\":\"PIX\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CLOSED"));
    }
}
