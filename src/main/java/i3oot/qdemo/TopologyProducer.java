package i3oot.qdemo;

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
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class TopologyProducer {

    private final JsonbSerde<ChatMessage> chatMessageSerde = new JsonbSerde<>(ChatMessage.class);
    private final JsonbSerde<Void> voidSerde = new JsonbSerde<>(Void.class);

    
    @Inject
    Configuration conf;


    @Produces
    public Topology topology() {

       // KeyValueBytesStoreSupplier inMemoryStoreSupplier = Stores.inMemoryKeyValueStore("article-by-id");
        
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(conf.inputTopic(), Consumed.with(voidSerde, chatMessageSerde))
            .peek((k,v) -> TopologyProducer.log.info(v.toString()))
            .to(conf.outputTopic(), Produced.with(voidSerde, chatMessageSerde));
      
        return builder.build();
    }

}