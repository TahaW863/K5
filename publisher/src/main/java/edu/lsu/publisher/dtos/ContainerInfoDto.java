package edu.lsu.publisher.dtos;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ContainerInfoDto {
    private String containerId;
    private String command;
    private String ImageNameWithTag;

}
