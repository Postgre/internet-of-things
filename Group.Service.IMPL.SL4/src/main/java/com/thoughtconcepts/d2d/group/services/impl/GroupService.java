package com.thoughtconcepts.d2d.group.services.impl;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.UnknownHostException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import static com.thoughtconcepts.d2d.configuration.constants.ConnectionConstants.*;
import com.thoughtconcepts.d2d.group.services.IGroupService;
import com.thoughtconcepts.group.model.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public class GroupService implements IGroupService {

    final ClassLoader oldCcl;

    public GroupService() {
        oldCcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

    @Override
    public boolean createGroup(String name, String parentId) {
        DB db = null;
        boolean flag = false;
        try {
            db = new MongoClient(MONGODB_SERVER, MONNGODB_PORT).getDB(DB_NAME);
            Jongo jongo = new Jongo(db);

            MongoCollection groups = jongo.getCollection("groups");

            Group group = new Group();
            group.setId(UUID.randomUUID().toString());
            Group parent = null;
            group.setName(name);
            group.setDeviceCount(0);

            if (parentId != null && !("".equals(parentId))) {

                parent = groups.findOne("{id: #}", parentId).as(
                        Group.class);
                if (parent == null) {
                    return false;
                }
                System.out.println("parent id : " + parentId);
                group.setParentId(parentId);
            }

            if (groups.save(group) != null) {
                flag = true;
            }
            if (parent != null && !parent.isParentFlag()) {
                groups.update("{id: #}", parentId).with("{$set: {parentFlag: true}}");
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(GroupService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(GroupService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return flag;
    }

    @Override
    public Collection<Group> listGroups() {
        List<Group> listGroups = new ArrayList<Group>();
        try {
            DB db = new MongoClient(MONGODB_SERVER, MONNGODB_PORT).getDB(DB_NAME);
            Jongo jongo = new Jongo(db);

            MongoCollection groups = jongo.getCollection("groups");
            Iterable<Group> allGroups = groups.find().as(Group.class);
            for (Group group : allGroups) {
                listGroups.add(group);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(GroupService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listGroups;
    }

    @Override
    public Group findGroup(String groupId) {
        Group group = null;
        try {
            DB db = new MongoClient(MONGODB_SERVER, MONNGODB_PORT).getDB(DB_NAME);
            Jongo jongo = new Jongo(db);
            MongoCollection groups = jongo.getCollection("groups");

            group = groups.findOne("{id: #}", groupId).as(Group.class);
            if (group != null) {
                return group;
            }

        } catch (UnknownHostException ex) {
            Logger.getLogger(GroupService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return group;
    }
}
