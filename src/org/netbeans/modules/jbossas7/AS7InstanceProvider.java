/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.spi.server.ServerInstanceProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author kulikov
 */
public class AS7InstanceProvider implements ServerInstanceProvider, LookupListener {

    private final static String INSTANCE_PATH = "/JBossAS7/Instances";

    private final Map<String, AS7Instance> instances =  Collections.synchronizedMap(new HashMap<String, AS7Instance>());
    private static final Set<String> activeDisplayNames = Collections.synchronizedSet(new HashSet<String>());

    private final ChangeSupport support = new ChangeSupport(this);
    private static AS7InstanceProvider provider;

    public static AS7InstanceProvider getProvider() {
        Logger.getLogger("jboss-as7").log(Level.INFO, "Instance provider: init()");
        if (provider == null) {
            provider = new AS7InstanceProvider();
            provider.init();
        }
        return provider;
    }

    public AS7Instance createInstance(String name, String location, boolean isDomain) {
        //create new instance
        AS7Instance server = isDomain? new AS7Domain(name, location) : new AS7Standalone(name, location);

        //put into map
        instances.put(name, server);
        activeDisplayNames.add(name);

        //persist server's config
        try {
            this.writeInstanceToFile(server, true);
        } catch (Exception e) {
            Logger.getLogger("jboss-as7").log(Level.SEVERE, null, e);
        }

        support.fireChange();
        return server;
    }

    public void remove(AS7Instance instance) {
        instances.remove(instance.getDisplayName());
        activeDisplayNames.remove(instance.getDisplayName());

        try {
            removeInstanceFromFile(instance);
        } catch (Exception e) {
            Logger.getLogger("jboss-as7").log(Level.SEVERE, null, e);
        }

        support.fireChange();
    }


    @Override
    public List<ServerInstance> getInstances() {
        List<ServerInstance> result = new ArrayList<ServerInstance>();

        //init();
        synchronized (instances) {
            for (AS7Instance instance : instances.values()) {
                result.add(instance.getCommonInstance());
            }
        }
        Logger.getLogger("jboss-as7").log(Level.INFO, "Instance count: {0}", result.size());
        return result;
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        Logger.getLogger("AS7provider").log(Level.INFO, "addChangeListener()");
        support.addChangeListener(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        support.removeChangeListener(cl);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Logger.getLogger("AS7provider").log(Level.INFO, "ResultChanged()");
    }

    public void loadServerInstances() {
        Logger.getLogger("jboss-as7").log(Level.INFO, "InstanceProvider: Load server instances");
        FileObject dir = this.getRepositoryDir(INSTANCE_PATH, false);

        if (dir != null) {
            FileObject[] instanceFOs = dir.getChildren();
            if (instanceFOs != null && instanceFOs.length > 0) {
                for (int i = 0; i < instanceFOs.length; i++) {
                    AS7Instance instance = this.readInstanceFromFile(instanceFOs[i]);
                    if (instance != null) {
                        instances.put(instance.getDisplayName(), instance);
                        activeDisplayNames.add(instance.getDisplayName());
                    }
                }
            }
        }

        for (AS7Instance i : instances.values()) {
            i.updateModuleSupport();
        }
    }

    private void init() {
        synchronized (instances) {
            try {
                loadServerInstances();
            } catch (RuntimeException ex) {
                Logger.getLogger("jboss-as7").log(Level.INFO, null, ex);
            }
        }
    }

    private AS7Instance readInstanceFromFile(FileObject instanceFO) {
        String name = (String) instanceFO.getAttribute(AS7Standalone.NAME);
        String location = (String) instanceFO.getAttribute(AS7Standalone.LOCATION);
        boolean isDomain = Boolean.valueOf((String) instanceFO.getAttribute(AS7Standalone.DOMAIN_MODE));

        return isDomain ? new AS7Domain(name, location) : new AS7Standalone(name, location);
    }

    private void writeInstanceToFile(AS7Instance instance, boolean search) throws IOException {
        FileObject dir = this.getRepositoryDir(INSTANCE_PATH, true);
        String name = FileUtil.findFreeFileName(dir, "instance", null);
        FileObject instanceFO = dir.createData(name);

        boolean isDomain = instance instanceof AS7Domain;

        instanceFO.setAttribute(AS7Instance.NAME, instance.getDisplayName());
        instanceFO.setAttribute(AS7Instance.LOCATION, instance.getLocation());
        instanceFO.setAttribute(AS7Instance.DOMAIN_MODE, Boolean.toString(isDomain));
    }

    private void removeInstanceFromFile(AS7Instance instance) throws IOException {
        FileObject dir = this.getRepositoryDir(INSTANCE_PATH, false);

        if (dir == null) {
            return;
        }

        FileObject[] instanceFOs = dir.getChildren();
        for (FileObject fo: instanceFOs) {
            String name = (String) fo.getAttribute(AS7Instance.NAME);
            if (name != null && name.equals(instance.getDisplayName())) {
                fo.delete();
            }
        }
    }

    private FileObject getRepositoryDir(String path, boolean create) {
        FileObject dir = FileUtil.getConfigFile(path);
        if (dir == null && create) {
            try {
                dir = FileUtil.createFolder(FileUtil.getConfigRoot(), path);
            } catch (IOException ex) {
                Logger.getLogger("jboss-as7").log(Level.INFO, null, ex);
            }
        }
        return dir;
    }
}
