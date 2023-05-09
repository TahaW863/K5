package edu.lsu.main.model;

import edu.lsu.main.dtos.ContainersDtos;
import lombok.Data;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "containerBatches")
public class ContainerBatches {
    private String id;
    List<ContainersDtos> batches;
    private String sessionId;
}
