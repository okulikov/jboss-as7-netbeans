/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.modules.jbossas7.nodes.Hk2StandaloneNode;
import org.openide.nodes.Node;

/**
 *
 * @author kulikov
 */
public final class AS7Standalone extends AS7Server {

    protected final ManagementClient cli = new ManagementClient();

    private StartServerTask startServer = new StartServerTask();
    private StopServerTask stopServer = new StopServerTask();
    private RestartTask restartServer = new RestartTask();

    private static final Logger logger = Logger.getLogger("AS7Instance");

    public AS7Standalone(String name, String path, String username, String password) {
        super(name, path, username, password);
    }

    public Collection<String> getApplications() {
        try {
            return cli.getApplications();
        } catch (IOException e) {
            return new ArrayList();
        }
    }

    public Collection<String> getExtensions() {
        try {
            return cli.getExtensions();
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
        return new Hk2StandaloneNode(this, true);
    }

    @Override
    public Node getBasicNode() {
        return new Hk2StandaloneNode(this, false);
    }

    @Override
    public JComponent getCustomizer() {
        return null;
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
        RP.post(stopServer);
    }

    @Override
    public void restart() {
        RP.post(restartServer);
    }

    public Collection<String> listApplications() throws IOException {
        return cli.getApplications();
    }

    private class StartServerTask implements Runnable {

        private JBossProcess standalone = new JBossProcess("standalone");
        private JBossProcess domain = new JBossProcess("domain");

        private Semaphore semaphore = new Semaphore(0);
        private volatile boolean success;

        @Override
        public void run() {
            success = false;

            JBossProcess process = standalone;
            process.setJBossHome(getLocation());

            try {
                process.start();

                LogViewer log = new LogViewer(process);
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
