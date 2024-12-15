package model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager em;


    public List<Order> getAllOrders() {
        return em.createQuery("select o from Order o", Order.class).getResultList();
    }

    @Transactional
    public Order save(Order order) {
        if (order.getId() == null) {
            em.persist(order);
            return order;
        } else {
            return em.merge(order);
        }
    }

    @Transactional
    public Optional<Order> findOrderById(Long id) {
        TypedQuery<Order> query = em.createQuery(
                "select o from Order o where o.id = :id",
                Order.class);
        query.setParameter("id", id);

        return query.getResultStream().findFirst();
    }


    @Transactional
    public void deleteOrderById(Long id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            em.remove(order);
        }
    }

    public User getUserByUserName(String userName) {
        return switch (userName) {
            case "user" -> new User("user", "Anonymous User");
            case "admin" -> new User("admin", "Alice Smith");
            case "jill" -> new User("jill", "Bob Jones");
            default -> null;
        };
    }
}
