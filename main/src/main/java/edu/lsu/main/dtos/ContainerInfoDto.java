package edu.lsu.main.dtos;

import lombok.*;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContainerInfoDto {
    private String command;
    private String ImageNameWithTag;

}
