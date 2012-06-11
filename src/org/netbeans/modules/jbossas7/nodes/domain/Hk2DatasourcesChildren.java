/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.nodes.domain;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.jbossas7.AS7Domain;
import org.netbeans.modules.jbossas7.AS7Standalone;
import org.netbeans.modules.jbossas7.nodes.Refreshable;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Mutex;
import org.openide.util.WeakListeners;

/**
 *
 * @author kulikov
 */
public class Hk2DatasourcesChildren extends Children.Keys<Node> implements Refreshable, ChangeListener {

    private AS7Domain domain;
    private String host;
    private String server;

    public Hk2DatasourcesChildren(AS7Domain si, String host, String server) {
        this.domain = si;
        this.host = host;
        this.server = server;
        domain.addChangeListener(WeakListeners.change(this, domain));
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

        Collection<String> apps = domain.getDatasources(host, server);

        if (apps != null) {
            for (String name : apps) {
                keys.add(new Hk2ItemNode(domain, name));
            }
        }
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
