package i3oot.qdemo;

import javax.validation.constraints.Size;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "wiki")
public interface Configuration {

    // @Size(min = 2)
    // public String getLang();
   
    // @Size(min = 8, max = 8)
    // String getVersion();
    
     @ConfigProperty(name="topic.in")
     public String inputTopic();
   
     @ConfigProperty(name="topic.out")
     public String outputTopic();

    // @ConfigProperty(name="topic.articles-by-id")
    // public String articlesByIdTopic();

    // @ConfigProperty(name="topic.key-by-title")
    // public String keyByTitleTopic();
}