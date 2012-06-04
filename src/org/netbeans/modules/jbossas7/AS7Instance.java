/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import org.netbeans.api.server.ServerInstance;

/**
 *
 * @author kulikov
 */
public interface AS7Instance {
    public enum ServerState {
        STARTING, STARTED, STOPPED;
    }

    public static final String NAME = "name";
    public static final String LOCATION = "location";
    public static final String DOMAIN_MODE = "domain_mode";

    public String getDisplayName();
    public String getLocation();
    public ServerInstance getCommonInstance();
    public void updateModuleSupport();

    public ServerState getState();

    public void start();
    public void stop();
    public void restart();
    public void remove();

}
