package tp.kafka.spam;

import java.util.function.Function;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpamApplication.class, args);
	}

      @Bean
  public Function<KStream<Object, String>, KStream<?, String>> process() {
    return record -> record;
  }
}
