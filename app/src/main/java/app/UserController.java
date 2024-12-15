package app;

import model.OrderDao;
import model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    private OrderDao orderDao;
    public UserController(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @GetMapping("/")
    public String frontPage() {
        return "Front page!";
    }

    @GetMapping("/home")
    public String info() {
        return "ok";
    }

    @GetMapping("/info")
    public String info(Principal principal) {
        String user = principal != null ? principal.getName() : "";

        return "Current user: " + user;
    }

    @GetMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public User getUserByName(@PathVariable("username") String username) {
        return orderDao.getUserByUserName(username);
    }

    @GetMapping("/users")
    public String getUsers() {
        return "ok";
    }
}
