package org.openhab.binding.oh2mqtt.internal;

import org.openhab.binding.oh2mqtt.internal.events.EventbusEventListener;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFilter;
import org.openhab.core.events.EventSubscriber;
import org.openhab.core.events.TopicEventFilter;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component(service = { EventbusService.class, EventSubscriber.class })
public class EventbusService implements EventSubscriber {
    private final Set<String> subscribedEventTypes = Set.of(EventSubscriber.ALL_EVENT_TYPES);
    private final EventFilter eventFilter = new TopicEventFilter("openhab/.*");

    private final Logger logger = LoggerFactory.getLogger(EventbusService.class);

    private final static Set<EventbusEventListener> eventbusEventListeners = new CopyOnWriteArraySet<>();

    @Activate
    protected void activate(BundleContext context) {
        logger.info("EventbusService is activated!");
    }

    @Deactivate
    protected void deactivate(BundleContext context) {
        logger.info("EventbusService is deactivated!");
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
        notifyEventbusEventListener(event);
    }

    private void notifyEventbusEventListener(Event event) {
        eventbusEventListeners.forEach(listener -> {
                listener.eventbusEventReceived(event);
        });
    }

    public static void registerEventbusEventListener(EventbusEventListener eventbusEventListener) {
        eventbusEventListeners.add(eventbusEventListener);
    }
}
