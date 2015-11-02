package com.thoughtconcepts.statistics.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
@XmlRootElement(name = "Statistics")
public class Statistics {

    @XmlElement(name = "Device Count")
    private long noOfDevices;
    @XmlElement(name = "Group Count")
    private long noOfGroups;
    @XmlElement(name = "Active Count")
    private long noOfActiveDevices;
    @XmlElement(name = "Reg Count")
    private long noOfRegisteredDevices;

    public long getNoOfDevices() {
        return noOfDevices;
    }

    public void setNoOfDevices(long noOfDevices) {
        this.noOfDevices = noOfDevices;
    }

    public long getNoOfGroups() {
        return noOfGroups;
    }

    public void setNoOfGroups(long noOfGroups) {
        this.noOfGroups = noOfGroups;
    }

    public long getNoOfActiveDevices() {
        return noOfActiveDevices;
    }

    public void setNoOfActiveDevices(long noOfActiveDevices) {
        this.noOfActiveDevices = noOfActiveDevices;
    }

    public long getNoOfRegisteredDevices() {
        return noOfRegisteredDevices;
    }

    public void setNoOfRegisteredDevices(long noOfRegisteredDevices) {
        this.noOfRegisteredDevices = noOfRegisteredDevices;
    }
}