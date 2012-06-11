/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.nodes.domain;

import org.netbeans.modules.jbossas7.nodes.*;
import java.util.Collection;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.jbossas7.AS7Domain;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Mutex;
import org.openide.util.WeakListeners;

/**
 *
 * @author kulikov
 */
public class Hk2InstanceChildren extends Children.Keys<Node> implements Refreshable, ChangeListener {

    private AS7Domain serverInstance;
    private String host;
    private String server;

    public Hk2InstanceChildren(AS7Domain si, String host, String server) {
        this.serverInstance = si;
        this.host = host;
        this.server = server;
        serverInstance.addChangeListener(WeakListeners.change(this, serverInstance));
    }

    @Override
    protected Node[] createNodes(Node key) {
        System.out.println(";;;;; Application children: Create node: " + key);
        return new Node[]{key};
    }

    @Override
    public void updateKeys() {
        System.out.println(";;;;; Application children: Update keys: ");
        Vector<Node> keys = new Vector<Node>();

        Hk2DatasourcesChildren datasourcesNode = new Hk2DatasourcesChildren(serverInstance, host, server);

        keys.add(new Hk2ItemNode(serverInstance, datasourcesNode, "Datasources"));
        
        setKeys(keys);
    }

    @Override
    protected void addNotify() {
        updateKeys();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Mutex.EVENT.readAccess(new Runnable() {
            @Override
            public void run() {
                updateKeys();
            }
        });

    }
}
