/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.IOException;
import java.io.InputStream;
import javax.swing.Action;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author kulikov
 */
public class LogViewer implements Runnable {
    private InputOutput io;
    private InputStream in;
    private InputStream err;

    public LogViewer(JBossProcess process) {
        io = IOProvider.getDefault().getIO("Jboss-AS7", new Action[]{});
        in = process.getInputStream();
    }

    public void print() throws IOException {
        new Thread(this).start();
    }

    @Override
    public void run() {
        io.select();
        io.getOut().write("Starting JBOSS AS7....");
        byte[] buff = new byte[1000];

        try {
            int len = 1;
            while (len > 0) {
                len = in.read(buff);
                String s = new String(buff, 0, len);
                io.getOut().write(s);
            }
        } catch (Exception e) {
        }
        io.getOut().write("Done....");

        try {
            in.close();
            io.getOut().close();
        } catch (IOException e) {
        }
    }
}
