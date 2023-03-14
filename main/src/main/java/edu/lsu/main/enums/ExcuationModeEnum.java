package edu.lsu.main.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum ExcuationModeEnum {
    BATCH("BATCH"),
    ONE_PER_CONTAINER("ONE_PER_CONTAINER");
    String value;
}
