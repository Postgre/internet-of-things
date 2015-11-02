package com.thoughtconcepts.d2d.device.services.impl;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.UnknownHostException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import static com.thoughtconcepts.d2d.configuration.constants.ConnectionConstants.*;
import com.thoughtconcepts.d2d.device.services.IDeviceService;
import com.thoughtconcepts.device.model.Device;
import com.thoughtconcepts.group.model.Group;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public class DeviceService implements IDeviceService {

    final ClassLoader oldCcl;

    public DeviceService() {
        oldCcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

    @Override
    public boolean registerDevice(String mac, String groupId) {
        DB db = null;
        boolean flag = false;
        try {
            db = new MongoClient(MONGODB_SERVER, MONNGODB_PORT).getDB(DB_NAME);
            Jongo jongo = new Jongo(db);

            MongoCollection devices = jongo.getCollection("devices");
            MongoCollection groups = jongo.getCollection("groups");

            Device device = new Device();
            device.setMac(mac);

            device.setGroupId(groupId);
            device.setStatus("Registered");

            if (devices.save(device) != null) {
                flag = true;
            }
            System.out.println("Group Id: "+groupId);
            
            if (groups.update("{id: #}", groupId).with("{$inc: {deviceCount: 1}}") != null) {
                flag = true;
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(DeviceService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DeviceService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return flag;
    }

    @Override
    public boolean updateDevice(String mac, String deviceIP, String deviceName) {
        boolean flag = false;

        try {
            DB db = new MongoClient(MONGODB_SERVER, MONNGODB_PORT).getDB(DB_NAME);
            Jongo jongo = new Jongo(db);

            MongoCollection devices = jongo.getCollection("devices");

            if (devices.update("{mac: #}", mac).with("{$set: {IP: #, name: #, status: 'Connected'}}",
                    deviceIP, deviceName) != null) {
                flag = true;
            }
        } catch (UnknownHostException hostException) {
            flag = false;
        } catch (Exception ex) {
            Logger.getLogger(DeviceService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return flag;
    }

    @Override
    public List<Device> listConnectedDevices() {
        List<Device> listDevices = new ArrayList<Device>();
        try {
            DB db = new MongoClient(MONGODB_SERVER, MONNGODB_PORT).getDB(DB_NAME);
            Jongo jongo = new Jongo(db);
            MongoCollection devices = jongo.getCollection("devices");

            Iterable<Device> allDevices = devices.find().as(Device.class);
            for (Device device : allDevices) {
                listDevices.add(device);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(DeviceService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listDevices;
    }

    @Override
    public boolean findDevice(String macID) {
        boolean flag = false;

        try {
            DB db = new MongoClient(MONGODB_SERVER, MONNGODB_PORT).getDB(DB_NAME);
            Jongo jongo = new Jongo(db);
            MongoCollection devices = jongo.getCollection("devices");

            Device device = devices.findOne("{mac: #}", macID).as(
                    Device.class);
            if (device != null) {
                flag = true;
            }

        } catch (UnknownHostException hostException) {
            flag = false;
        }
        return flag;
    }
}
