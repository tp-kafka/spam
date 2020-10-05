package tp.kafka.spam;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(KafkaResource.class)
public class TopologyTest {


    @Test
    public void testKafkaStreams() throws Exception {
        assertTrue(true);
    }
}