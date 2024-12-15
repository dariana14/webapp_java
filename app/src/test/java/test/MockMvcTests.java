package test;

import app.OrderController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import config.MvcConfig;
import model.Order;
import model.OrderRow;
import model.ValidationError;
import model.ValidationErrors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = { MvcConfig.class })
public class MockMvcTests {

    private WebApplicationContext ctx;

    @Autowired
    public void setCtx(WebApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Test
    public void controllerTest() {
        var controller = ctx.getBean(OrderController.class);

        System.out.println(controller.getOrders());
    }

    @Test
    public void showErrorsOnInvalidInput() throws Exception {

        String json = "{\"id\":null,\"orderNumber\":null,\"orderRows\": null}";

        MockHttpServletResponse response = simulatePost("/orders", json);
        var errors = getErrorCodes(response.getContentAsString());

        assertThat(response.getStatus(), is(400));

        assertThat(errors, containsInAnyOrder(
                "NotNull.order.orderNumber"));
    }

    @Test
    public void noErrorsOnValidData() throws UnsupportedEncodingException {

        String json = "{\"id\":null,\"orderNumber\":\"fgdkko1\"," +
                "\"orderRows\":[" +
                "{\"itemName\":\"fgdkki1\",\"quantity\":1,\"price\":100}," +
                "{\"itemName\":\"fgdkki2\",\"quantity\":1,\"price\":100}]}";

        MockHttpServletResponse response = simulatePost("/orders", json);

        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void deleteOrderShouldRemoveOrder() throws Exception {

        String json = "{\"id\":null,\"orderNumber\":\"toDelete\",\"orderRows\":[" +
                "{\"itemName\":\"item\",\"quantity\":1,\"price\":100}]}";

        MockHttpServletResponse postResponse = simulatePost("/orders", json);
        assertThat(postResponse.getStatus(), is(200));

        String id = JsonPath.read(postResponse.getContentAsString(), "$.id").toString();

        MockHttpServletResponse deleteResponse = simulateDelete("/orders/" + id);
        assertThat(deleteResponse.getStatus(), is(200));

        MockHttpServletResponse getResponse = simulateGet("/orders");
        assertThat(getResponse.getContentAsString().contains("\"id\":\"" + id + "\""), is(false));
    }

    @Test
    public void getOrderShouldReturnCorrectOrder() throws Exception {
        String json = "{\"id\":null,\"orderNumber\":\"testOrder\",\"orderRows\":[" +
                "{\"itemName\":\"item\",\"quantity\":1,\"price\":100}]}";

        MockHttpServletResponse postResponse = simulatePost("/orders", json);

        String id = JsonPath.read(postResponse.getContentAsString(), "$.id").toString();

        MockHttpServletResponse getResponse = simulateGet("/orders/" + id);
        assertThat(getResponse.getStatus(), is(200));

        String responseContent = getResponse.getContentAsString();
        Order order = new ObjectMapper().readValue(responseContent, Order.class);

        assertThat(order.getId().toString(), is(id));
        assertThat(order.getOrderNumber(), is("testOrder"));
        assertThat(order.getOrderRows().size(), is(1)); // Kontrolli, et on 1 orderRow

        OrderRow orderRow = order.getOrderRows().getFirst();
        assertThat(orderRow.getItemName(), is("item"));
        assertThat(orderRow.getQuantity(), is(1));
        assertThat(orderRow.getPrice(), is(100));
    }

    private MockHttpServletResponse simulateGet(String url) {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(ctx).build();

        try {
            return mvc.perform(get(url))
                    .andReturn()
                    .getResponse();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MockHttpServletResponse simulateDelete(String url) {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(ctx).build();

        try {
            return mvc.perform(delete(url))
                    .andReturn()
                    .getResponse();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MockHttpServletResponse simulatePost(String url, String input) {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(ctx).build();

        try {
            return mvc.perform(post(url)
                            .content(input)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Content-type", "application/json"))
                    .andReturn()
                    .getResponse();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getErrorCodes(String json) {
        try {
            ValidationErrors errors = new ObjectMapper().readValue(json, ValidationErrors.class);

            return errors.getErrors().stream().map(ValidationError::getCode).toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
