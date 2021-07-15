/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.oh2mqtt.internal;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFilter;
import org.openhab.core.events.EventSubscriber;
import org.openhab.core.events.TopicEventFilter;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { EventbusSubscriberService.class, EventSubscriber.class }, immediate = true)
public class EventbusSubscriberService implements EventSubscriber {
    private final Set<String> subscribedEventTypes = Set.of(EventSubscriber.ALL_EVENT_TYPES);
    private final EventFilter eventFilter = new TopicEventFilter("openhab/.*");

    private final Logger logger = LoggerFactory.getLogger(EventbusSubscriberService.class);

    @Reference
    protected @NonNullByDefault({}) EventbusPublisherService publisher;

    @Reference
    protected @NonNullByDefault({}) MQTTPublisherService mqttPublisherService;

    @Activate
    protected void activate(BundleContext context) {
        logger.info("EventBusToMQTTSubscriber is activated!");
    }

    @Deactivate
    protected void deactivate(BundleContext context) {
        logger.info("EventBusToMQTTSubscriber is deactivated!");
    }

    @Override
    public Set<String> getSubscribedEventTypes() {
        return subscribedEventTypes;
    }

    @Override
    public EventFilter getEventFilter() {
        return eventFilter;
    }

    @Override
    public void receive(Event event) {
        String topic = event.getTopic();
        String type = event.getType();
        String payload = event.getPayload();
        String source = event.getSource();

        logger.info(String.format("Received new event: Topic: %s, Type: %s, Payload: %s, Source: %s", topic, type,
                payload, source));

        try {
            mqttPublisherService.publish(event);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
