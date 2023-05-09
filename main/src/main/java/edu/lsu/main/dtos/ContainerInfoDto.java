package edu.lsu.main.dtos;

import lombok.*;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ContainerInfoDto {
    private String containerId;
    private String containerName;
    private String command;
    private String ImageNameWithTag;

}
