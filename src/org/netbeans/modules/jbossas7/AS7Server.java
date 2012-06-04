/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.spi.server.ServerInstanceFactory;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author kulikov
 */
public abstract class AS7Server implements AS7Instance {
    private String name;
    private final String location;
    private final String userName;
    private final String password;

    private transient InstanceContent ic;
    private transient Lookup lookup;
    private ServerInstance commonInstance;
    private AS7InstanceProvider provider;

    private ServerState state;
    private ChangeSupport changeSupport = new ChangeSupport(this);

    protected static final RequestProcessor RP = new RequestProcessor("JBoss-AS7",5); // NOI18N

    public AS7Server(String name, String location, final String userName, final String password) {
        this.name = name;
        this.location = location;
        this.userName = userName;
        this.password = password;

        this.provider = AS7InstanceProvider.getProvider();
        init();

        Authenticator.setDefault(new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password.toCharArray());
            }
        });

    }

    private void init() {
        commonInstance = ServerInstanceFactory.createServerInstance(this);
        ic = new InstanceContent();
        ic.add(this);
        lookup = new AbstractLookup(ic);
    }

    @Override
    public ServerState getState() {
        return state;
    }

    protected void setState(ServerState state) {
        this.state = state;
        changeSupport.fireChange();
    }

    @Override
    public ServerInstance getCommonInstance() {
        return commonInstance;
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void resultChanged(LookupEvent le) {
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }


    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getServerDisplayName() {
        return this.getDisplayName();
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void remove() {
        ic.remove(this);
        provider.remove(this);
    }


}
