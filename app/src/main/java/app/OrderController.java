package app;

import jakarta.validation.Valid;
import model.Order;
import model.OrderDao;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {

    private OrderDao orderDao;


    public OrderController(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @GetMapping("orders")
    public List<Order> getOrders() {
        return orderDao.getAllOrders();

    }

    @GetMapping("orders/{id}")
    public Order getOrder(@PathVariable("id") Long id) {

        Optional<Order> order = orderDao.findOrderById(id);

        return order.orElse(null);
    }

    @PostMapping("orders")
    //@ResponseStatus(HttpStatus.CREATED)
    public Order addOrder(@RequestBody @Valid Order order) {
        return orderDao.save(order);
    }

    @DeleteMapping("orders/{id}")
    public void deleteOrder(@PathVariable("id") Long id) {
        orderDao.deleteOrderById(id);
    }

}
