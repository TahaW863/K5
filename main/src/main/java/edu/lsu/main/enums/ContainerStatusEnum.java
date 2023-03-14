package edu.lsu.main.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContainerStatusEnum {
    RUNNING("RUNNING"),
    STOPPED("STOPPED"),
    ERROR("ERROR");
    String value;
}
