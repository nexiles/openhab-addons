package org.openhab.binding.oh2mqtt.internal.dtos;

public class MQTTEventDTO {
    private String type;
    private String value;

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
