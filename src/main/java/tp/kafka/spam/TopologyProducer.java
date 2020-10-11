package tp.kafka.spam;

import java.nio.channels.SelectionKey;
import java.time.Duration;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Window;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.apache.kafka.streams.state.internals.InMemoryWindowBytesStoreSupplier;

import io.quarkus.kafka.client.serialization.JsonbSerde;
import io.smallrye.mutiny.tuples.Tuple2;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import org.apache.kafka.common.serialization.Serdes.LongSerde;
import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.common.serialization.Serdes.VoidSerde;

@ApplicationScoped
public class TopologyProducer {

    private static final Logger log = Logger.getLogger(TopologyProducer.class.getName()); 
    



    @Inject
    Configuration conf;

    private final JsonbSerde<ChatMessage> msgSerde = new JsonbSerde<>(ChatMessage.class);
    private final JsonbSerde<ChatTuple> tupleSerde = new JsonbSerde<>(ChatTuple.class);
    
    private final VoidSerde voidSerde = new VoidSerde();
    private StringSerde stringSerde = new StringSerde();
    private LongSerde longSerde = new LongSerde();


    @Produces
    public Topology filteredInputTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        var input = builder.<Void, ChatMessage>stream(conf.inputTopic(), Consumed.with(voidSerde, msgSerde))
            .filterNot(this::containsBadWords)
            .<String>selectKey((k, v) -> v.getUserId())
            .through("input-repartition",  Produced.with(stringSerde, msgSerde))
            ;

        var blocked = input
            .<String>groupBy((k, v) -> v.getUserId(), Grouped.with(stringSerde, msgSerde))
            .windowedBy(TimeWindows.of(Duration.ofSeconds(2)).grace(Duration.ofMillis(50)))
            .count()
            .toStream()
            .filter((w,c) -> (c > conf.spamThreshold()))
            .map((w,c) -> new KeyValue<>(w.key(), c))
            .through("banned",  Produced.with(stringSerde, longSerde))
            .peek((k,v) -> TopologyProducer.log.info(k + " blocked for sending " + v + " messages within 2 seconds"))
            ;
                
        input
            .<Long, ChatTuple>leftJoin(blocked, 
                ChatTuple::new, 
                JoinWindows.of(conf.banTime()),
                StreamJoined.with(stringSerde, msgSerde, longSerde))
            .filterNot((k,v) -> v.isSpam())  
            .mapValues(v -> v.getMsg())  
            .peek((k,v) -> TopologyProducer.log.info("forwarding: " + v))    
            .to(conf.outputTopic(), Produced.with(stringSerde, msgSerde));

        
        return builder.build();
    }

    Boolean containsBadWords(Void key, ChatMessage msg){
        boolean result = msg.getMessage().toLowerCase().contains("fight club");
        TopologyProducer.log.info(msg + " does " + (result?"":"not ") +  "contain bad words.");
        return result;
    }

}