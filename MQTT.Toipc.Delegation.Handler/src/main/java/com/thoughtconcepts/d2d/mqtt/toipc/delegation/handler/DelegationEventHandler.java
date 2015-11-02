package com.thoughtconcepts.d2d.mqtt.toipc.delegation.handler;

import static com.thoughtconcepts.d2d.configuration.constants.ConnectionConstants.*;
import com.thoughtconcepts.d2d.device.services.IDeviceService;
import com.thoughtconcepts.d2d.event.constants.EventConstants;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public class DelegationEventHandler implements EventHandler {

    private IDeviceService deviceService;

    public DelegationEventHandler(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void handleEvent(Event event) {
        System.out.println("---------------- HERE ----------- EVENT IS :::"+event.getTopic());
        if (event.getTopic().equals(EventConstants.NEW_DEVICE_DETECTED)) {
            String macID = parseTopic((String) event.getProperty("MQTT_TOPIC"), 2);
            String deviceIP = parsePayload((String) event.getProperty("payload"), 1);
            String deviceName = parsePayload((String) event.getProperty("payload"), 3);
            if (deviceService != null) {
                if (deviceService.findDevice(macID)) {
                    deviceService.updateDevice(macID, deviceIP, deviceName);
                }
            }
        }
        if (event.getTopic().equals(EventConstants.DEVICE_STIMULATED)) {
            MQTT mqtt = new MQTT();
            String macID = (String) event.getProperty("mac");
            try {
                mqtt.setHost("tcp://" + BROKER_IP + ":" + BROKER_PORT);
                mqtt.setClientId("GLASSFISH");
                mqtt.setCleanSession(true);
                BlockingConnection connection = mqtt.blockingConnection();
                connection.connect();
                connection.publish("d2d/device/fired/" + macID, "Hello WISER".getBytes(), QoS.AT_LEAST_ONCE, true);
            } catch (URISyntaxException ex) {
                Logger.getLogger(DelegationEventHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(DelegationEventHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String parsePayload(String input, int qualifier) {
        String[] fields = input.split(":");
        if (fields != null) {
            return fields[qualifier];
        }

        return "";
    }

    private String parseTopic(String input, int qualifier) {
        String[] fields = input.split("/");
        if (fields != null) {
            return fields[qualifier];
        }

        return "";
    }
}
