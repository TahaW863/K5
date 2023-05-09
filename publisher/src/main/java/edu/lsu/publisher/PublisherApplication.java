package edu.lsu.publisher;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import edu.lsu.publisher.dtos.DockerStatsSummaryDto;
import edu.lsu.publisher.model.DockerStatsModel;
import edu.lsu.publisher.service.DockerStatsService;
import edu.lsu.publisher.service.DockerStatsServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PublisherApplication {
    static DockerStatsService dockerStatsService;
    public PublisherApplication(DockerStatsService dockerStatsService) {
        this.dockerStatsService = dockerStatsService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PublisherApplication.class, args);

        //System.out.println(DigestUtils.sha256Hex("hello ^^ world" ));


        // save the data coming from flux stats summary function to a json file
        // get the data from docker stats summary function
        /*List<DockerStatsSummaryDto> dockerStatsModelList = new ArrayList<>();
        // chain the writing to json file after finsihing the subscription (adding the data to the list)
        dockerStatsService.getAllStatsSummaryForAllContainers()
                .doOnNext(dockerStatsModelList::add)
                .doOnComplete(() -> {
                    // write the data to a json file
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objectMapper.addHandler(new DeserializationProblemHandler() {
                        @Override
                        public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                            return true;
                        }
                    });
                    try {
                        objectMapper.writeValue(new File("src/main/resources/data.json"), dockerStatsModelList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .subscribe();*/

    }
}
