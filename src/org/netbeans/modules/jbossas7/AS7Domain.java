/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.jbossas7.nodes.domain.Hk2DomainNode;
import org.netbeans.spi.server.ServerInstanceFactory;
import org.netbeans.spi.server.ServerInstanceImplementation;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author kulikov
 */
public final class AS7Domain implements AS7Instance, ServerInstanceImplementation, Lookup.Provider, LookupListener {
    public enum ServerState {
        STARTING, STARTED, STOPPED;
    }

    //instance properties
    private String name;
    private String location;
    private boolean isDomain;

    private transient InstanceContent ic;
    private transient Lookup lookup;
    private ServerInstance commonInstance;
    private AS7InstanceProvider provider;

    private ServerState state;
    private ChangeSupport changeSupport = new ChangeSupport(this);


    protected final ManagementClient cli = new ManagementClient();
    private static final RequestProcessor RP = new RequestProcessor("JBoss-AS7",5); // NOI18N

    private StartServerTask startServer = new StartServerTask();
    private StopServerTask stopServer = new StopServerTask();
    private RestartTask restartServer = new RestartTask();

    private URL controllerAddress;

    private static final Logger logger = Logger.getLogger("AS7Instance");

    public AS7Domain(String name, String path) {
        this.name = name;
        this.location = path;
        this.isDomain = true;

        this.provider = AS7InstanceProvider.getProvider();

        init();
    }

    private void init() {
        commonInstance = ServerInstanceFactory.createServerInstance(this);

        ic = new InstanceContent();
        ic.add(this);
        lookup = new AbstractLookup(ic);

        try {
            controllerAddress = new URL("http://localhost:9990/management");
        } catch (Exception e) {
        }

    }

    public ServerState getState() {
        return state;
    }

    protected void setState(ServerState state) {
        this.state = state;
        changeSupport.fireChange();
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    public boolean isDomain() {
        return this.isDomain;
    }

    @Override
    public ServerInstance getCommonInstance() {
        return commonInstance;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getServerDisplayName() {
        return name;
    }

    public Collection<String> getApplications() {
        try {
            return ManagementUtil.list(controllerAddress, "{\"operation\":\"read-children-names\", \"child-type\":\"deployment\",\"json.pretty\":1}");
        } catch (IOException e) {
            return new ArrayList();
        }
    }

    public Collection<String> getExtensions() {
        try {
            return ManagementUtil.list(controllerAddress, "{\"operation\":\"read-children-names\", \"child-type\":\"extension\",\"json.pretty\":1}");
        } catch (IOException e) {
            return new ArrayList();
        }
    }

    public Collection<String> getHosts() {
        try {
            return ManagementUtil.list(controllerAddress, "{\"operation\":\"read-children-names\", \"child-type\":\"host\",\"json.pretty\":1}");
        } catch (IOException e) {
            return new ArrayList();
        }
    }

    public Collection<String> getServer(String hostName) {
        String cmd = String.format("{\"operation\":\"read-children-names\", \"child-type\":\"server\", \"address\":[{\"host\":\"%s\"}],\"json.pretty\":1}", hostName);
        try {
            return ManagementUtil.list(controllerAddress, cmd);
        } catch (IOException e) {
            return new ArrayList();
        }
    }

    public Collection<String> getServerGroups() {
        try {
            return ManagementUtil.list(controllerAddress, "{\"operation\":\"read-children-names\", \"child-type\":\"server-group\",\"json.pretty\":1}");
        } catch (IOException e) {
            return new ArrayList();
        }
    }

    public Collection<String> getDatasources() {
        try {
            return cli.getDataSources();
        } catch (IOException e) {
            return new ArrayList();
        }
    }

    @Override
    public Node getFullNode() {
        return new Hk2DomainNode(this, true);
//        System.out.println("------- GET FULL NODE");
//        return new Hk2InstanceNode(this, true);
    }

    @Override
    public Node getBasicNode() {
        System.out.println("------- GET BASIC NODE");
        return new Hk2DomainNode(this, false);
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public void remove() {
        logger.log(Level.INFO, "Remove()");
        ic.remove(this);
        provider.remove(this);
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void resultChanged(LookupEvent le) {
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void updateModuleSupport() {
    }

    public void start() {
        RP.post(startServer);
    }

    public void stop() {
        try {
            ManagementUtil.exec(controllerAddress, "{\"operation\":\"shutdown\",\"address\":[{\"host\":\"*\"}],\"json.pretty\":1}");
            this.setState(ServerState.STOPPED);
        } catch (IOException e) {
        }
        //RP.post(stopServer);
    }

    public void restart() {
        RP.post(restartServer);
    }

    public Collection<String> listApplications() throws IOException {
        return cli.getApplications();
    }

    private class StartServerTask implements Runnable {

        private JBossProcess domain = new JBossProcess("domain");

        private Semaphore semaphore = new Semaphore(0);
        private volatile boolean success;

        @Override
        public void run() {
            success = false;

            domain.setJBossHome(getLocation());

            try {
                domain.start();

                LogViewer log = new LogViewer(domain);
                log.print();

//                semaphore.acquire();

//                if (success) {
                    setState(ServerState.STARTED);
//                } else {
//                    setState(ServerState.STOPPED);
//                }
            } catch (Exception e) {
                setState(ServerState.STOPPED);
            }
        }

    }

    private class StopServerTask implements Runnable {

        @Override
        public void run() {
            try {
                cli.shutdown();
                setState(ServerState.STOPPED);
            } catch (IOException e) {
            }
            System.out.println("------------- STOP");
        }

    }

    private class RestartTask implements Runnable  {

        @Override
        public void run() {
            System.out.println("------------- STOP");
        }

    }

}
