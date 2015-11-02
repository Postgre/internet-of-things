package com.thoughtconcepts.d2d.mqtt.topic.delegation.osgi.tracker;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public class DeviceServiceTracker implements ServiceTrackerCustomizer {

    @Override
    public Object addingService(ServiceReference ref) {
        Bundle b = ref.getBundle();
        Object service = b.getBundleContext().getService(ref);
        return service;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
    }
}
