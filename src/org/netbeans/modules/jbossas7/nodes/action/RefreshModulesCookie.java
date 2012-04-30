/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.nodes.action;

/**
 *
 * @author kulikov
 */
public interface RefreshModulesCookie {
    public void refresh();
    public void refresh(String expectedChild, String unexpectedChild);
}
