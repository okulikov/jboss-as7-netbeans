/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.action;

import org.netbeans.modules.jbossas7.AS7Instance;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author kulikov
 */
public class StopServerAction extends NodeAction {

    @Override
    protected void performAction(Node[] nodes) {
        for (Node node : nodes) {
            AS7Instance as7 = node.getLookup().lookup(AS7Instance.class);
            if (as7 != null) {
                as7.stopServer();
            }
        }
    }

    @Override
    protected boolean enable(Node[] nodes) {
        for (Node node : nodes) {
            AS7Instance as7 = node.getLookup().lookup(AS7Instance.class);
            if (as7 != null) {
                return as7.getState() == AS7Instance.ServerState.STARTED;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "Stop server";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

}
