/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import org.netbeans.api.server.ServerInstance;
import org.netbeans.spi.server.ServerInstanceImplementation;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;

/**
 *
 * @author kulikov
 */
public interface AS7Instance extends ServerInstanceImplementation, Lookup.Provider, LookupListener{
    public enum ServerState {
        STARTING, STARTED, STOPPED;
    }

    public static final String NAME = "name";
    public static final String LOCATION = "location";
    public static final String USER_NAME = "user";
    public static final String PASSWORD = "password";
    public static final String DOMAIN_MODE = "domain_mode";

    public String getUserName();
    public String getPassword();
    
    public String getLocation();
    public ServerInstance getCommonInstance();
    public void updateModuleSupport();

    public ServerState getState();

    public void start();
    public void stop();
    public void restart();

}
