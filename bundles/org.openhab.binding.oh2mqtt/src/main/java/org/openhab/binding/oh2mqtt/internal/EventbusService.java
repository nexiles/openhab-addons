package org.openhab.binding.oh2mqtt.internal;

import org.openhab.binding.oh2mqtt.internal.events.EventbusEventListener;
import org.openhab.core.events.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component(service = { EventbusService.class, EventSubscriber.class })
public class EventbusService implements EventSubscriber {
    private final Set<String> subscribedEventTypes = Set.of(EventSubscriber.ALL_EVENT_TYPES);
    private final EventFilter eventFilter = new TopicEventFilter("openhab/.*");

    private final Logger logger = LoggerFactory.getLogger(EventbusService.class);

    @Reference
    private EventPublisher eventPublisher;

    private final Set<EventbusEventListener> eventbusEventListeners = new CopyOnWriteArraySet<>();

    @Activate
    protected void activate(BundleContext context) {
        logger.debug("EventbusService is activated!");
    }

    @Deactivate
    protected void deactivate(BundleContext context) {
        logger.debug("EventbusService is deactivated!");
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

    public void publish(Event event) {
        eventPublisher.post(event);
    }

    private void notifyEventbusEventListener(Event event) {
        eventbusEventListeners.forEach(listener -> {
                listener.eventbusEventReceived(event);
        });
    }

    public void registerEventbusEventListener(EventbusEventListener eventbusEventListener) {
        eventbusEventListeners.add(eventbusEventListener);
    }
}
