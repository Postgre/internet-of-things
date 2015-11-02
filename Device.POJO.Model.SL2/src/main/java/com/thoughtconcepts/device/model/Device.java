package com.thoughtconcepts.device.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.jongo.marshall.jackson.oid.Id;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
@XmlRootElement(name = "Device")
public class Device {

    @Id
    private String mac;
    private String IP;
    private String name;
    private String status;
    private String groupId;

    public Device() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}