/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.nodes;

import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.jbossas7.AS7Standalone;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Mutex;
import org.openide.util.WeakListeners;

/**
 *
 * @author kulikov
 */
public class Hk2InstanceChildren extends Children.Keys<Node> implements Refreshable, ChangeListener {

    private AS7Standalone serverInstance;

    public Hk2InstanceChildren(AS7Standalone si) {
        this.serverInstance = si;
        serverInstance.addChangeListener(WeakListeners.change(this, serverInstance));
    }

    @Override
    protected Node[] createNodes(Node key) {
        System.out.println(";;;;; Create node: " + key);
        return new Node[]{key};
    }

    @Override
    public void updateKeys() {
        System.out.println("Instance-children: Update keys: ");
        Vector<Node> keys = new Vector<Node>();

        if (serverInstance.getState() == AS7Standalone.ServerState.STARTED) {
            keys.add(new Hk2ItemNode(serverInstance, new Hk2ExtensionChildren(serverInstance), "Extensions"));
            keys.add(new Hk2ItemNode(serverInstance, new Hk2ApplicationChildren(serverInstance), "Applications"));
            keys.add(new Hk2ItemNode(serverInstance, new Hk2ResourcesChildren(serverInstance), "Resources"));
            keys.add(new Hk2ItemNode(serverInstance, new Hk2WSChildren(serverInstance), "Web services"));
        }

        setKeys(keys);
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
