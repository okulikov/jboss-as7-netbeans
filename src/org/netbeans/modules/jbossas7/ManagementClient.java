/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author kulikov
 */
public class ManagementClient {

    private URL address;

    public ManagementClient() {
        try {
            address = new URL("http://localhost:9990/management");
        } catch (Exception e) {
        }
    }

    public Collection<String> getApplications(String serverName) {
        return null;
    }

    public Collection<String> getApplications() throws IOException {
        String response = read(exec("{\"operation\":\"read-children-names\", \"child-type\":\"deployment\",\"json.pretty\":1}"));
        return parse(response);
    }

    public Collection<String> getExtensions() throws IOException {
        String response = read(exec("{\"operation\":\"read-children-names\", \"child-type\":\"extension\",\"json.pretty\":1}"));
        return parse(response);
    }

    public Collection<String> getDataSources() throws IOException {
        String res = read(exec("{\"operation\":\"read-children-types\", \"address\":[{\"subsystem\":\"datasources\"}],\"json.pretty\":1}"));
        return parse(res);
    }

    public Collection<String> getJMS() throws IOException {
        String res = read(exec("{\"operation\":\"read-children-types\", \"address\":[{\"subsystem\":\"jms\"}],\"json.pretty\":1}"));
        return parse(res);
    }

    public void shutdown() throws IOException {
        exec("{\"operation\":\"shutdown\",\"json.pretty\":1}");
    }


    private InputStream exec(String cmd) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        connection.addRequestProperty("Content-Type", "application/json");
        connection.connect();

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(cmd + "\n");
        writer.flush();

        InputStream in = null;
        try {
            in = connection.getInputStream();
        } catch (Exception e) {
        }

        return in;
    }

    private Collection<String> parse(String response) {
        ArrayList<String> list = new ArrayList();
        String outcome = response.substring(1, response.indexOf(','));

        String msg = response.substring(response.indexOf('[') + 1, response.indexOf(']'));
        String[] tokens = msg.split(",");

        for (String s : tokens) {
            list.add(s.trim());
        }
        return list;
    }

    private String read(InputStream in) throws IOException {
        int b;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        while ((b = in.read()) != -1) {
            bout.write(b);
        }
        return new String(bout.toByteArray());
    }

}
