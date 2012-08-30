/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.openide.execution.NbProcessDescriptor;
import org.openide.util.Utilities;

/**
 *
 * @author kulikov
 */
public class JBossProcess {

    private String jbossHome;
    private String name;
    private Process process;

    public JBossProcess(String name) {
        this.name = name;
    }

    public void setJBossHome(String jbossHome) {
        this.jbossHome = jbossHome;
    }

    public void start() throws ProcessCreationException, IOException {
        File workingDir = new File(jbossHome);
        NbProcessDescriptor pd = createProcessDescriptor();

        if (pd == null) {
            throw new ProcessCreationException(null, "MSG_INVALID_JAVA", name, "Process descriptor"); // NOI18N
        }

        System.out.println("Descriptor=" + pd);
        process = pd.exec(null, this.createEnvironment(), true, workingDir);
    }

    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    public InputStream getInputStream() {
        return process.getInputStream();
    }

    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    public String[] createEnvironment() {
        ArrayList<String> envp = new ArrayList<String>();
        envp.add("JBOSS_HOME=" + jbossHome);
        return envp.toArray(new String[envp.size()]);
    }

    private NbProcessDescriptor createProcessDescriptor() throws ProcessCreationException {
        String startScript = jbossHome + File.separator + "bin" + File.separator + name;
        if (Utilities.isWindows()) {
            startScript += ".bat"; // NOI18N
        } else {
            startScript += ".sh";
        }

        File ss = new File(startScript);
        if (!ss.exists()) {
            throw new ProcessCreationException(null, "MSG_INVALID_JAVA", name, startScript); // NOI18N
        }

        return new NbProcessDescriptor(startScript, "");
    }

}
