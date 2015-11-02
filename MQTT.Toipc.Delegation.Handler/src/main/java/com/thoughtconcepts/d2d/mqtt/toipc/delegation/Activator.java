package com.thoughtconcepts.d2d.mqtt.toipc.delegation;

import com.thoughtconcepts.d2d.device.services.IDeviceService;
import com.thoughtconcepts.d2d.mqtt.toipc.delegation.handler.DelegationEventHandler;
import com.thoughtconcepts.d2d.mqtt.topic.delegation.osgi.tracker.DeviceServiceTracker;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private ServiceTracker deviceServiceTracker;
    private IDeviceService deviceService;

    public void start(BundleContext context) throws Exception {
        String[] topics = new String[]{
            "d2d/device/*"
        };

        Dictionary props = new Hashtable();
        props.put(EventConstants.EVENT_TOPIC, topics);

        obtainDeviceService(context);
        context.registerService(EventHandler.class
                .getName(), new DelegationEventHandler(deviceService), props);
    }

    public void stop(BundleContext context) throws Exception {
    }

    private void obtainDeviceService(BundleContext context) {
        deviceServiceTracker = new ServiceTracker(context,
                IDeviceService.class.getName(),
                new DeviceServiceTracker());
        deviceServiceTracker.open();
        deviceService = (IDeviceService) deviceServiceTracker.getService();
    }
}
