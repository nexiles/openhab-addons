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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.oh2mqtt.internal.events.EventbusEventListener;
import org.openhab.core.events.Event;
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
public class OH2MQTTHandler extends BaseBridgeHandler implements EventbusEventListener {

    private final Logger logger = LoggerFactory.getLogger(OH2MQTTHandler.class);

    private final MQTTClientService mqttClientService;

    public OH2MQTTHandler(Bridge bridge) {
        super(bridge);
        this.mqttClientService = new MQTTClientService();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // there are no channels on the gateway yet
    }

    @Override
    public void initialize() {

        final OH2MQTTConfiguration configuration = getConfigAs(OH2MQTTConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);

        scheduler.execute(() -> {
            if (mqttClientService.connect(configuration)) {
                updateStatus(ThingStatus.ONLINE);
                mqttClientService.subscribe(configuration.inTopic);
                EventbusService.registerEventbusEventListener(this);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
            }
        });
    }

    @Override
    public void dispose() {
        logger.info("DISPOSE Bridge");
        mqttClientService.disconnect();
    }

    @Override
    public void eventbusEventReceived(@Nullable Event e) {
        String topic = e.getTopic();
        String type = e.getType();
        String payload = e.getPayload();
        String source = e.getSource();

        logger.info(String.format("Received new event: Topic: %s, Type: %s, Payload: %s, Source: %s", topic, type,
                payload, source));

        mqttClientService.publish(e);
    }
}
