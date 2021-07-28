package org.openhab.binding.oh2mqtt.internal.events;


import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * The {@link MQTTEventListener} is to notify implemented classed about node messages.
 *
 * @author Jakob Huber - Initial contribution
 */
public interface MQTTEventListener {

    void mqttEventReceived(String topic, MqttMessage message);
}
