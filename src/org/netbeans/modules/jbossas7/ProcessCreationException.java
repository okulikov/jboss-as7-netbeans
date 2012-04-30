/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

/**
 *
 * @author kulikov
 */
public class ProcessCreationException extends Exception {

    /**
     * Creates a new instance of
     * <code>ProcessCreateException</code> without detail message.
     */
    public ProcessCreationException(Exception cause, String messageName, String... args) {
    }

    /**
     * Constructs an instance of
     * <code>ProcessCreateException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ProcessCreationException(String msg) {
        super(msg);
    }
}
