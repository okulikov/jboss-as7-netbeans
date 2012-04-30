/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.jbossas7.nodes.Hk2InstanceNode;
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
public final class AS7Instance implements ServerInstanceImplementation, Lookup.Provider, LookupListener {
    public enum ServerState {
        STARTING, STARTED, STOPPED;
    }

    public static final String NAME = "name";
    public static final String LOCATION = "location";
    public static final String DOMAIN_MODE = "domain_mode";

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


    protected final CommandLineInterface cli = new CommandLineInterface();
    private static final RequestProcessor RP = new RequestProcessor("JBoss-AS7",5); // NOI18N

    private StartServerTask startServer = new StartServerTask();
    private StopServerTask stopServer = new StopServerTask();
    private RestartTask restartServer = new RestartTask();

    private List<String> applications;
    private static final Logger logger = Logger.getLogger("AS7Instance");

    public AS7Instance(String name, String path, boolean isDomain) {
        this.name = name;
        this.location = path;
        this.isDomain = isDomain;

        this.provider = AS7InstanceProvider.getProvider();

        init();
    }

    private void init() {
        commonInstance = ServerInstanceFactory.createServerInstance(this);

        ic = new InstanceContent();
        ic.add(this);
        lookup = new AbstractLookup(ic);
    }

    public ServerState getState() {
        return state;
    }

    protected void setState(ServerState state) {
        this.state = state;
        changeSupport.fireChange();
    }

    public String getLocation() {
        return this.location;
    }

    public boolean isDomain() {
        return this.isDomain;
    }

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

    public List<String> getApplications() {
        return this.applications;
    }

    @Override
    public Node getFullNode() {
        System.out.println("------- GET FULL NODE");
        return new Hk2InstanceNode(this, true);
    }

    @Override
    public Node getBasicNode() {
        System.out.println("------- GET BASIC NODE");
        return new Hk2InstanceNode(this, false);
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

    public void updateModuleSupport() {
    }

    public void startServer() {
        RP.post(startServer);
    }

    public void stopServer() {
        RP.post(stopServer);
    }

    public void restartServer() {
        RP.post(restartServer);
    }

    public void listApplications() {
        cli.setListener(new ApplicationList());
        cli.listApplications();
    }

    private class StartServerTask implements Runnable, CommandLineListener {

        private JBossProcess standalone = new JBossProcess("standalone");
        private JBossProcess domain = new JBossProcess("domain");

        private Semaphore semaphore = new Semaphore(0);
        private volatile boolean success;

        @Override
        public void run() {
            success = false;

            JBossProcess process = isDomain() ? domain : standalone;
            process.setJBossHome(getLocation());

            try {
                process.start();

                LogViewer log = new LogViewer(process);
                log.print();

                cli.setJBossHome(getLocation());
                cli.setListener(this);
                cli.connect();
                semaphore.acquire();

                if (success) {
                    setState(ServerState.STARTED);
                } else {
                    setState(ServerState.STOPPED);
                }
            } catch (Exception e) {
                setState(ServerState.STOPPED);
            }
        }

        @Override
        public void onCommandCompleted(CommandLineInterface cli) {
            success = true;
            semaphore.release();

            listApplications();
        }

        @Override
        public void onCommandFail(CommandLineInterface cli, Exception e) {
            success = false;
            semaphore.release();
        }

    }

    private class StopServerTask implements Runnable, CommandLineListener {

        @Override
        public void run() {
            System.out.println("------------- STOP");
            cli.setListener(this);
            cli.shutdown();
        }

        @Override
        public void onCommandCompleted(CommandLineInterface cli) {
            setState(ServerState.STOPPED);
        }

        @Override
        public void onCommandFail(CommandLineInterface cli, Exception e) {
        }

    }

    private class RestartTask implements Runnable, CommandLineListener {

        @Override
        public void run() {
            System.out.println("------------- STOP");
            cli.setListener(this);
            cli.shutdown();
        }

        @Override
        public void onCommandCompleted(CommandLineInterface cli) {
            setState(ServerState.STOPPED);
            startServer();
        }

        @Override
        public void onCommandFail(CommandLineInterface cli, Exception e) {
        }

    }

    private class ApplicationList implements CommandLineListener {

        @Override
        public void onCommandCompleted(CommandLineInterface cli) {
            applications = cli.getApplications();
            changeSupport.fireChange();
        }

        @Override
        public void onCommandFail(CommandLineInterface cli, Exception e) {
        }

    }
}
