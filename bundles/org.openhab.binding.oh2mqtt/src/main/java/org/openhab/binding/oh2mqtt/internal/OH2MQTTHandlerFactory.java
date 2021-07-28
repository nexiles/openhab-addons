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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static org.openhab.binding.oh2mqtt.internal.OH2MQTTBindingConstants.BROKER;
import static org.openhab.binding.oh2mqtt.internal.OH2MQTTBindingConstants.SUPPORTED_THING_TYPES_UIDS;

/**
 * The {@link OH2MQTTHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Jakob Huber - Initial contribution
 */
@NonNullByDefault
@Component(service = { OH2MQTTHandlerFactory.class,
        ThingHandlerFactory.class }, configurationPid = "binding.zwavetomqtt")
public class OH2MQTTHandlerFactory extends BaseThingHandlerFactory {

    @Reference
    private @NonNullByDefault({}) EventbusService eventbusService;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (BROKER.equals(thingTypeUID)) {
            return new OH2MQTTHandler((Bridge) thing, eventbusService);
        }

        return null;
    }
}
