package edu.lsu.publisher.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.Date;

@Data
@Builder
public class HashModel {
    private String containerId;
    private String hashType;
    private String hashValue;
    private Date timestamp;
}
