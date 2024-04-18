package com.ssepoc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SLabelButton {
    private String alarmStatus;
    private String mode;
    private int buttonNumber;
}
