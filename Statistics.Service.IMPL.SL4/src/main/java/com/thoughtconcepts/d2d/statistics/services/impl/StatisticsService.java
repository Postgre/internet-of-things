package com.thoughtconcepts.d2d.statistics.services.impl;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.UnknownHostException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import static com.thoughtconcepts.d2d.configuration.constants.ConnectionConstants.*;
import com.thoughtconcepts.d2d.statistics.services.IStatisticsService;
import com.thoughtconcepts.statistics.model.Statistics;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */

public class StatisticsService implements IStatisticsService {

    final ClassLoader oldCcl;

    public StatisticsService() {
        oldCcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

    public Statistics getStatistics() {
        Statistics statistics = null;
        DB db = null;
        try {
            System.out.println("--- INSIDE IMPL ----");
            db = new MongoClient(MONGODB_SERVER, MONNGODB_PORT).getDB(DB_NAME);
            Jongo jongo = new Jongo(db);
            MongoCollection groups = jongo.getCollection("groups");
            MongoCollection devices = jongo.getCollection("devices");

            statistics = new Statistics();
            statistics.setNoOfDevices(devices.count());
            statistics.setNoOfGroups(groups.count());
            statistics.setNoOfActiveDevices(devices.count("{status: 'Connected'}"));
            statistics.setNoOfRegisteredDevices(devices.count("{status: 'Registered'}"));

        } catch (UnknownHostException ex) {
            Logger.getLogger(StatisticsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(StatisticsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("STATISTICS::::"+statistics);
        return statistics;
    }
}
