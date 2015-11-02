package com.thoughtconcepts.d2d.device.services;

import com.thoughtconcepts.device.model.Device;
import java.util.List;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public interface IDeviceService {

    public boolean registerDevice(String mac, String groupId);

    public boolean updateDevice(String mac, String deviceIP, String deviceName);
    
    public boolean findDevice(String mac);

    public List<Device> listConnectedDevices();
}
