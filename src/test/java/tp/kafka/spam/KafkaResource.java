package tp.kafka.spam;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.KafkaContainer;

public class KafkaResource implements QuarkusTestResourceLifecycleManager  {

    private static final KafkaContainer KAFKA = new KafkaContainer();

    @Override
    public Map<String, String> start() {
        KAFKA.start();
        return Collections.singletonMap("kafka.bootstrap.servers", KAFKA.getBootstrapServers());
    }

    @Override
    public void stop() {
        KAFKA.stop();
    }
}