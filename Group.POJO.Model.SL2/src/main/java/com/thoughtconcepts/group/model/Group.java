package com.thoughtconcepts.group.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.jongo.marshall.jackson.oid.Id;

/**
 * 
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
@XmlRootElement(name = "Group")
public class Group {

    @Id
    private String id;
    private String name;
    private String parentId;
    private boolean parentFlag;
    private int deviceCount;

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public Group() {
    }

    public boolean isParentFlag() {
        return parentFlag;
    }

    public void setParentFlag(boolean parentFlag) {
        this.parentFlag = parentFlag;
    }

    public String getParentId() {
        return parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}