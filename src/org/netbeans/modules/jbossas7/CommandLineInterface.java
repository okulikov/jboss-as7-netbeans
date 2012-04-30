/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.openide.util.RequestProcessor;

/**
 *
 * @author kulikov
 */
public class CommandLineInterface {
    public enum State {
        CONNECTED, DISCONNECTED
    }

    private InputStream in;
    private OutputStream out;

    private State state = State.DISCONNECTED;
    private boolean stopped = false;

    private JBossProcess admin = new JBossProcess("jboss-admin");

    private CommandLineListener listener;
    private RequestProcessor RP = new RequestProcessor("jboss-as7",2);

    //commands
    private ConnectTask connect = new ConnectTask(this);
    private ShutdownTask shutdown = new ShutdownTask(this);
    private ListApplicationsTask listApps = new ListApplicationsTask(this);

    private Recognizer recognizer = new Recognizer();

    //operation results
    private List<String> list;

    public CommandLineInterface() {
    }

    public void setJBossHome(String jbossHome) {
        admin.setJBossHome(jbossHome);
    }

    public void setListener(CommandLineListener listener) {
        this.listener = listener;
    }

    public List<String> getApplications() {
        return list;
    }

    public void connect() throws IOException, ProcessCreationException {
        stopped = false;
        RP.post(connect);
    }

    public void shutdown() {
        stopped = true;
        RP.post(shutdown);
    }

    public void listApplications() {
        RP.post(listApps);
    }

    public void list() {

    }

    public void quit() {

    }

    private void onMessage(String message) {
        System.out.println("Message: " + message);
        list.add(message);
    }

    private class ConnectTask implements Runnable {

        private CommandLineInterface cli;

        private ConnectTask(CommandLineInterface cli) {
            this.cli = cli;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                admin.start();
            } catch (Exception e) {
                listener.onCommandFail(null, e);
                return;
            }

            System.out.println("Streams");
            in = admin.getInputStream();
            out = admin.getOutputStream();

            try {
                out.write("connect\n".getBytes());
            } catch (Exception e) {
                listener.onCommandFail(null, e);
                return;
            }

            listener.onCommandCompleted(cli);
            state = State.CONNECTED;
            System.out.println("----Connected to console");
        }

    }

    private class ShutdownTask implements Runnable {
        private CommandLineInterface cli;

        public ShutdownTask(CommandLineInterface cli) {
            this.cli = cli;
        }

        @Override
        public void run() {
            if (out == null) {
                listener.onCommandFail(cli, new NullPointerException("console is disconnected"));
            }

            try {
                out.write(":shutdown\n".getBytes());
                out.write("q\n".getBytes());

                listener.onCommandCompleted(cli);
                out.close();
            } catch (IOException e) {
                listener.onCommandFail(cli, e);
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

    }

    private class ListApplicationsTask implements Runnable {

        private CommandLineInterface cli;

        public ListApplicationsTask(CommandLineInterface cli) {
            this.cli = cli;
        }

        @Override
        public void run() {
            System.out.println("Writting command ls: " + out);
            try {
                out.write("ls deployments=\n".getBytes());
            System.out.println("Reading list of apps ls");
                list.clear();
//                list = recognizer.process(in);
System.out.println("List of apps: " + list);
                listener.onCommandCompleted(cli);
            } catch (IOException e) {
                e.printStackTrace();
                listener.onCommandFail(cli, e);
            }
        }

    }

    private class Reader implements Runnable {
        @Override
        public void run() {
            StringBuilder builder = new StringBuilder();
            while (!stopped) {
                int b = -1;

                try {
                    b = in.read();
                } catch (IOException e) {
                    b = -1;
                }

                switch (b) {
                    case '\n' :
                    case '\t' :
                    case '\r' :
                       onMessage(builder.toString());
                       builder = new StringBuilder();
                }
            }
        }
    }
}
