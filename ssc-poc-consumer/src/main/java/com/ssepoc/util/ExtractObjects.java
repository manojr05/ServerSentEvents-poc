package com.ssepoc.util;

import com.ssepoc.model.EndDevice;
import com.ssepoc.model.SLabelButton;
import com.ssepoc.model.SLabelPickingStatus;
import com.ssepoc.model.enums.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExtractObjects {

    public static void extractObjects(String input, String event){
        if(event.equals(Event.ALARM_STATUS.toString())){
            EndDevice endDevice = extractEndDevice(input);
            SLabelButton sLabelButton = extractSLabelButton(input);
            log.info("EndDevice: {}", endDevice);
            log.info("SLabelButton: {}", sLabelButton);
        }else {
            SLabelPickingStatus sLabelPickingStatus = extractSLabelPickingStatus(input);
            log.info("SLabelPickingStatus: {}", sLabelPickingStatus);
        }


    }

    private static EndDevice extractEndDevice(String input) {
        Pattern pattern = Pattern.compile("EndDevice\\(id=(\\d+), code=(\\w+), macAddress=(\\w+-\\w+-\\w+-\\w+-\\w+-\\w+-\\w+-\\w+)\\)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            String code = matcher.group(2);
            String macAddress = matcher.group(3);
            return new EndDevice(id, code, macAddress);
        } else {
            throw new IllegalArgumentException("Invalid input format for EndDevice");
        }
    }

    private static SLabelButton extractSLabelButton(String input) {
        Pattern pattern = Pattern.compile("SLabelButton\\[(\\d+)\\]\\(alarmStatus=(\\w+), mode=(\\w+)\\)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String alarmStatus = matcher.group(2);
            String mode = matcher.group(3);
            int buttonNumber = Integer.parseInt(matcher.group(1));
            return new SLabelButton(alarmStatus, mode, buttonNumber);
        } else {
            throw new IllegalArgumentException("Invalid input format for SLabelButton");
        }
    }

    private static SLabelPickingStatus extractSLabelPickingStatus(String input) {
        Pattern pattern = Pattern.compile("SLabelPickingStatus\\(code=(\\w+), pickingLinkStatus=(\\w+), ledColor=(\\w+), requestPickingDate=(.*), pickingDate=(.*)\\)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String code = matcher.group(1);
            String pickingLinkStatus = matcher.group(2);
            String ledColor = matcher.group(3);
            String requestPickingDate = matcher.group(4);
            String pickingDate = matcher.group(5);
            return new SLabelPickingStatus(code, pickingLinkStatus, ledColor, requestPickingDate, pickingDate);
        } else {
            throw new IllegalArgumentException("Invalid input format for SLabelPickingStatus");
        }
    }

}
