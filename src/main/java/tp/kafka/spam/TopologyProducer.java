package tp.kafka.spam;

import java.time.Duration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.java.Log;
import org.apache.kafka.common.serialization.Serdes.VoidSerde;

@ApplicationScoped
@Log
public class TopologyProducer {

    @Inject
    Configuration conf;

    private final JsonbSerde<ChatMessage> msgSerde = new JsonbSerde<>(ChatMessage.class);
    private final VoidSerde voidSerde = new VoidSerde();

    @Produces
    public Topology filteredInputTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        builder.<Void, ChatMessage>stream(conf.inputTopic(), Consumed.with(voidSerde, msgSerde))
            .filterNot(this::containsBadWords)
            .to(conf.outputTopic());
        return builder.build();
    }

    Boolean containsBadWords(Void key, ChatMessage msg){
        boolean result = msg.getMessage().toLowerCase().contains("fight club");
        TopologyProducer.log.info(msg + " does " + (result?"":"not ") +  "contain bad words.");
        return result;
    }

}