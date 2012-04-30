/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.wizard;

/**
 *
 * @author kulikov
 */
public class Retriever {

    public interface Updater {
        public void updateMessageText(String msg);
        public void updateStatusText(String status);
        public void clearCancelState();
    }
}
