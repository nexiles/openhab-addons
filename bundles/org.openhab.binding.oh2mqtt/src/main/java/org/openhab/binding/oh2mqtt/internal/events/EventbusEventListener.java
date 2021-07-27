package org.openhab.binding.oh2mqtt.internal.events;


import org.openhab.core.events.Event;

/**
 * The {@link EventbusEventListener} is to notify implemented classed about node messages.
 *
 * @author Jakob Huber - Initial contribution
 */
public interface EventbusEventListener {

    void eventbusEventReceived(Event event);
}
