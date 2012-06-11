/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author kulikov
 */
public class ManagementUtil {
    public static Collection<String> list(URL controller, String cmd) throws IOException {
        String response = read(exec(controller, cmd));

        ArrayList<String> list = new ArrayList();
        String outcome = response.substring(1, response.indexOf(','));

        String msg = response.substring(response.indexOf('[') + 1, response.indexOf(']'));
        String[] tokens = msg.split(",");

        for (String s : tokens) {
            s = s.trim();

            if (s.startsWith("\"")) s = s.substring(1);
            if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);

            if (s.length() > 0) {
                list.add(s.trim());
            }
        }
        return list;
    }

    public static InputStream exec(URL controller, String cmd) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) controller.openConnection();
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

    private static String read(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        
        int b;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        while ((b = in.read()) != -1) {
            bout.write(b);
        }
        return new String(bout.toByteArray());
    }

}
