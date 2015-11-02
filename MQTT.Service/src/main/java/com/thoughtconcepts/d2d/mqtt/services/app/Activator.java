package com.thoughtconcepts.d2d.mqtt.services.app;

import static com.thoughtconcepts.d2d.configuration.constants.ConnectionConstants.*;
import com.thoughtconcepts.d2d.mqtt.services.listener.MQTTListener;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public class Activator implements BundleActivator {

    private Thread thread;

    public void start(BundleContext bc) throws Exception {

        ServiceReference ref = bc.getServiceReference(EventAdmin.class.getName());
        if (ref != null) {
            EventAdmin eventAdmin = (EventAdmin) bc.getService(ref);

            MQTTListener mqList = new MQTTListener("tcp://" + BROKER_IP + ":" + BROKER_PORT, "d2d/device/#", "D:/D2D/logs", true, eventAdmin);
            thread = new Thread(mqList);
            thread.start();
        }
    }

    public void stop(BundleContext bc) throws Exception {
        thread.interrupt();
    }
}
