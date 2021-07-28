package org.openhab.binding.oh2mqtt.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.openhab.core.events.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1_1;

/**
 * The {@link MQTTClientService} is responsible for handling MQTT broker connection, subscription and messages.
 *
 * @author nexiles - Initial contribution
 */
@NonNullByDefault
public class MQTTClientService {

    private final Logger logger = LoggerFactory.getLogger(MQTTClientService.class);

    private @Nullable MqttClient mqttClient;
    private @Nullable String serverUri;
    private @Nullable OH2MQTTConfiguration configuration;

    public MQTTClientService() {}

    public boolean connect(OH2MQTTConfiguration configuration) {
        this.configuration = configuration;

        serverUri = String.format("tcp://%s:%s", configuration.mqttHost, configuration.mqttPort);

        try {
            logger.debug("Connect to MQTT broker: {}", serverUri);
            mqttClient = new MqttClient(serverUri, configuration.mqttClientName, new MemoryPersistence());
        } catch (MqttException e) {
            logger.error("Cannot create MQTT broker: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setUserName(configuration.mqttUser);
        options.setPassword(configuration.mqttPassword.toCharArray());
        options.setConnectionTimeout(10);
        options.setMqttVersion(MQTT_VERSION_3_1_1);

        try {
            mqttClient.setCallback(mqttCallback());
            mqttClient.connect(options);
            logger.debug("Successfully connected to MQTT broker: {}", serverUri);
            return true;
        } catch (MqttException e) {
            logger.error("Cannot connect to MQTT broker", e);
            return false;
        }
    }

    public void subscribe(String topic) {
        if (mqttClient == null)
            logger.warn("Connect to MQTT broker first, before subscribing");
        try {
            logger.info("Subscribe to topic: {}", topic);
            mqttClient.subscribe(topic);
        } catch (MqttException e) {
            logger.error("Cannot subscribe to topic", e);
        }
    }

    public void publish(Event event) {
        String topicOut = configuration.outTopic + "/" + event.getTopic();
        logger.trace("Publishing to MQTT topic: {}, payload {}", topicOut, event.getPayload());
        try {
            mqttClient.publish(topicOut, new MqttMessage(event.getPayload().getBytes()));
        } catch (MqttException e) {
            logger.error("Cannot publish to MQTT broker", e);
        }
    }

    public void disconnect() {
        if (mqttClient == null)
            logger.warn("Connect to MQTT broker first, before disconnecting");
        try {
            logger.debug("Disconnect from MQTT broker: {}", serverUri);
            mqttClient.disconnect();
        } catch (MqttException e) {
            logger.error("Cannot disconnect from MQTT broker", e);
        }
    }

    private MqttCallback mqttCallback() {
        return new MqttCallback() {
            @Override
            public void connectionLost(@Nullable Throwable cause) {
                logger.error("MQTT connection lost", cause);
            }

            @Override
            public void messageArrived(@Nullable String topic,@Nullable MqttMessage message) {
                final String messageBody = new String(message.getPayload());
                logger.trace("MQTT message received ({}): {}", topic, messageBody);
            }

            @Override
            public void deliveryComplete(@Nullable IMqttDeliveryToken token) {
                // currently not required
            }
        };
    }
}
