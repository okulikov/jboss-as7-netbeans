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
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.modules.jbossas7.nodes.domain.Hk2DomainNode;
import org.openide.nodes.Node;
import org.openide.util.*;

/**
 *
 * @author kulikov
 */
public final class AS7Domain extends AS7Server {

    protected final ManagementClient cli = new ManagementClient();

    private StartServerTask startServer = new StartServerTask();
    private StopServerTask stopServer = new StopServerTask();
    private RestartTask restartServer = new RestartTask();

    private URL controllerAddress;

    private static final Logger logger = Logger.getLogger("AS7Instance");

    public AS7Domain(String name, String path, String username, String password) {
        super(name, path, username, password);
        try {
            controllerAddress = new URL("http://localhost:9990/management");
        } catch (Exception e) {
        }
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

    public Collection<String> getDatasources(String host, String server) {
        String cmd = String.format("{\"operation\":\"read-children-types\", \"address\":[{\"host\":\"%s\"},{\"server\":\"%s\"},{\"subsystem\":\"datasources\"}],\"json.pretty\":1}", host, server);
        try {
            return ManagementUtil.list(controllerAddress, cmd);
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
    public boolean isRemovable() {
        return true;
    }

    @Override
    public void resultChanged(LookupEvent le) {
    }

    @Override
    public void updateModuleSupport() {
    }

    @Override
    public void start() {
        RP.post(startServer);
    }

    @Override
    public void stop() {
        try {
            ManagementUtil.exec(controllerAddress, "{\"operation\":\"shutdown\",\"address\":[{\"host\":\"*\"}],\"json.pretty\":1}");
            this.setState(ServerState.STOPPED);
        } catch (IOException e) {
        }
        //RP.post(stopServer);
    }

    @Override
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
