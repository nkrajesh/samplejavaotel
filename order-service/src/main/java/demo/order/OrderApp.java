package demo.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrderApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}

@Entity
@Table(name = "orders")
class Order {
    @Id
    @GeneratedValue
    public Long id;
    public String item;
    public int qty;
}

interface OrderRepo extends JpaRepository<Order, Long> {
}

@RestController
class OrderController {
    private final OrderRepo repo;
    private final KafkaTemplate<String, String> kafka;

    OrderController(OrderRepo repo, KafkaTemplate<String, String> kafka) {
        this.repo = repo;
        this.kafka = kafka;
    }

    @PostMapping("/orders")
    Order create(@RequestBody Order order) {
        Order saved = repo.save(order);
        kafka.send("orders", saved.id.toString(), "order " + saved.id + ": " + saved.qty + " x " + saved.item);
        return saved;
    }
}
