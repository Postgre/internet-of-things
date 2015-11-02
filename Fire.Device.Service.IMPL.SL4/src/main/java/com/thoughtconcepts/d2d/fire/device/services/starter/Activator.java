package com.thoughtconcepts.d2d.fire.device.services.starter;

import com.thoughtconcepts.d2d.fire.device.services.IFireDeviceService;
import com.thoughtconcepts.d2d.fire.device.services.impl.FireDeviceService;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext bc, DependencyManager dm) throws Exception {
        dm.add(createComponent()
                .setInterface(IFireDeviceService.class.getName(), null)
                .setImplementation(FireDeviceService.class)
                .add(createServiceDependency()
                    .setService(EventAdmin.class)
                    .setRequired(true)
                )
        );
    }

    @Override
    public void destroy(BundleContext bc, DependencyManager dm) throws Exception {
    }

   
}
