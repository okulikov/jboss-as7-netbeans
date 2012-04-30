/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

/**
 *
 * @author kulikov
 */
public interface CommandLineListener {
    public void onCommandCompleted(CommandLineInterface cli);
    public void onCommandFail(CommandLineInterface cli, Exception e);
}
