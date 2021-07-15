package org.openhab.binding.oh2mqtt.internal;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.openhab.core.events.Event;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { MQTTPublisherService.class }, immediate = true)
public class MQTTPublisherService extends MqttClient {
    private final Logger logger = LoggerFactory.getLogger(MQTTPublisherService.class);

    private static final String URL = "tcp://192.168.200.93:1883";
    private static final String CLIENT_ID = "OHEB2MQTT";
    private static final String TOPIC_OUT = "oheb2mqtt/out";
    private static final String TOPIC_IN = "oheb2mqtt/in";

    @Activate
    public MQTTPublisherService(BundleContext context) throws MqttException {
        super(URL, CLIENT_ID, null);

        logger.info("MQTTPublisher is activated!");

        this.connect();
    }

    @Deactivate
    protected void deactivate(BundleContext context) {
        logger.info("MQTTPublisher is deactivated!");
    }

    public void publish(Event event) throws MqttException {
        String topic = TOPIC_OUT + "/" + event.getTopic();
        logger.info("Publishing to MQTT topic: {}, payload {}", topic, event.getPayload());
        super.publish(topic, new MqttMessage(event.getPayload().getBytes()));
    }
}
