package com.thoughtconcepts.d2d.group.services;

import com.thoughtconcepts.group.model.Group;
import java.util.Collection;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public interface IGroupService {

    public boolean createGroup(String name, String parentId);

    public Group findGroup(String groupId);

    public Collection<Group> listGroups();
}
