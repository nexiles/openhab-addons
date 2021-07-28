/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.oh2mqtt.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.openhab.binding.oh2mqtt.internal.dtos.MQTTEventDTO;
import org.openhab.binding.oh2mqtt.internal.events.EventbusEventListener;
import org.openhab.binding.oh2mqtt.internal.events.MQTTEventListener;
import org.openhab.core.events.Event;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The {@link OH2MQTTHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jakob Huber - Initial contribution
 */
@NonNullByDefault
public class OH2MQTTHandler extends BaseBridgeHandler implements EventbusEventListener, MQTTEventListener {

    private final Logger logger = LoggerFactory.getLogger(OH2MQTTHandler.class);

    private final MQTTClientService mqttClientService;
    private final EventbusService eventbusService;

    private OH2MQTTConfiguration configuration;

    private final ObjectMapper objectMapper;

    public OH2MQTTHandler(Bridge bridge, EventbusService eventbusService) {
        super(bridge);
        this.configuration = getConfigAs(OH2MQTTConfiguration.class);
        this.eventbusService = eventbusService;
        this.mqttClientService = new MQTTClientService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // there are no channels on the gateway yet
    }

    @Override
    public void initialize() {
        configuration = getConfigAs(OH2MQTTConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);

        scheduler.execute(() -> {
            if (mqttClientService.connect(configuration)) {
                updateStatus(ThingStatus.ONLINE);
                mqttClientService.subscribe(configuration.inTopic);
                mqttClientService.registerMQTTEventListener(this);
                eventbusService.registerEventbusEventListener(this);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
            }
        });
    }

    @Override
    public void dispose() {
        logger.debug("Dispose Bridge");
        mqttClientService.disconnect();
    }

    @Override
    public void eventbusEventReceived(@Nullable Event e) {
        String topic = e.getTopic();
        String type = e.getType();
        String payload = e.getPayload();
        String source = e.getSource();

        logger.debug(String.format("Received new event: Topic: %s, Type: %s, Payload: %s, Source: %s", topic, type,
                payload, source));

        mqttClientService.publish(e);
    }

    @Override
    public void mqttEventReceived(@Nullable String topic, @Nullable MqttMessage message) {
        String messageBody = new String(message.getPayload());
        logger.debug("MQTT message received ({}): {}", topic, messageBody);

        String itemName = topic.split("/")[4];

        try{
            MQTTEventDTO basicEventDTO = objectMapper.readValue(messageBody, MQTTEventDTO.class);

            if (!basicEventDTO.getType().equals("Decimal")) throw new Exception(String.format("Invalid event type: %s", basicEventDTO.getType()));

            ItemCommandEvent itemCommandEvent = ItemEventFactory.createCommandEvent(itemName, DecimalType.valueOf(basicEventDTO.getValue()));

            eventbusService.publish(itemCommandEvent);
        } catch (JsonProcessingException e) {
            logger.error("Cannot process JSON", e);
        } catch (Exception e) {
            logger.error("Only events of typo 'Decimal' are supported", e);
        }
    }
}
