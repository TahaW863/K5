package edu.lsu.main.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
public class CommandModel {
    private String command;
    private List<String> arguments;
}
