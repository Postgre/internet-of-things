package com.thoughtconcepts.d2d.fire.device.services.impl;

import com.thoughtconcepts.d2d.event.constants.EventConstants;
import com.thoughtconcepts.d2d.fire.device.services.IFireDeviceService;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public class FireDeviceService implements IFireDeviceService {

    private volatile EventAdmin eventAdmin;

    public void fireToDeviceViaEventAdmin(String mac) {
        System.out.println("---- INSIDE FIRING -----");
        System.out.println("--- EVENTADMIN IN FIRING ----"+eventAdmin);
        boolean flag = false;
        Dictionary properties = new Hashtable();
        properties.put("mac", mac);
        Event event = new Event(EventConstants.DEVICE_STIMULATED, properties);
        eventAdmin.sendEvent(event);
    }
}
