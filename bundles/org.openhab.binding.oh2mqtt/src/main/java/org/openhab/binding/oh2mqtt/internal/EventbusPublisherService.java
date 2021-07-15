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

import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.events.ItemCommandEvent;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.types.OnOffType;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { EventbusPublisherService.class }, immediate = true)
public class EventbusPublisherService {
    private EventPublisher eventPublisher;

    private final Logger logger = LoggerFactory.getLogger(EventbusPublisherService.class);

    @Activate
    protected void activate(BundleContext context) {
        logger.info("EventBusToMQTTPublisher is activated!");
    }

    @Deactivate
    protected void deactivate(BundleContext context) {
        logger.info("EventBusToMQTTPublisher is deactivated!");
    }

    public void postSomething() {
        ItemCommandEvent itemCommandEvent = ItemEventFactory.createCommandEvent("ItemX", OnOffType.ON);

        String topic = itemCommandEvent.getTopic();
        String type = itemCommandEvent.getType();
        String payload = itemCommandEvent.getPayload();
        logger.info(String.format("Create new event: Topic: %s, Type: %s, Payload: %s", topic, type, payload));

        eventPublisher.post(itemCommandEvent);
    }

    public void publishCommand(ItemCommandEvent payload) {
        eventPublisher.post(payload);
    }

    @Reference
    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void unsetEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = null;
    }
}
