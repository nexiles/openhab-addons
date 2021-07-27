package org.openhab.binding.oh2mqtt.internal;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.openhab.core.events.Event;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1_1;

@Component(service = { MQTTPublisherService.class }, immediate = true)
public class MQTTPublisherService extends MqttClient {
    private final Logger logger = LoggerFactory.getLogger(MQTTPublisherService.class);

    private static final String URL = "tcp://192.168.200.93:1883";
    private static final String MQTT_USER = "openhab";
    private static final String MQTT_PASSWORD = "openhab";
    private static final String CLIENT_ID = "OHEB2MQTT";
    private static final String TOPIC_OUT = "oheb2mqtt/out";
    private static final String TOPIC_IN = "oheb2mqtt/in";

    @Activate
    public MQTTPublisherService(BundleContext context) throws MqttException {
        super(URL, CLIENT_ID, new MemoryPersistence());

        logger.info("MQTTPublisher is activated!");

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(MQTT_USER);
        options.setPassword(MQTT_PASSWORD.toCharArray());

        try {
            this.connect(options);
            logger.debug("Successfully connected to MQTT broker: {}", URL);
            this.subscribe();
        } catch (MqttException e) {
            logger.error("Cannot connect to MQTT broker", e);
        }
    }

    @Deactivate
    protected void deactivate(BundleContext context) {
        try {
            logger.debug("Disconnect from MQTT broker: {}", URL);
            this.disconnect();
        } catch (MqttException e) {
            logger.error("Cannot disconnect from MQTT broker", e);
        }
    }

    private void subscribe() throws MqttException {
        String topicIn = TOPIC_IN + "/#";
        logger.info("Connect to topic: {}", topicIn);

        super.subscribe(topicIn, (topic, message) -> {
            logger.info("Received new message on topic: {}, message: {}", topic, message.toString());
        });
    }

    public void publish(Event event) throws MqttException {
        String topicOut = TOPIC_OUT + "/" + event.getTopic();
        logger.info("Publishing to MQTT topic: {}, payload {}", topicOut, event.getPayload());
        super.publish(topicOut, new MqttMessage(event.getPayload().getBytes()));
    }
}
