package demo.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class NotifyApp {
    public static void main(String[] args) {
        SpringApplication.run(NotifyApp.class, args);
    }
}

@Component
class OrderListener {
    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);

    @KafkaListener(topics = "orders", groupId = "notification-service")
    void onOrder(String msg) {
        log.info("Sending notification for -> {}", msg);
    }
}
