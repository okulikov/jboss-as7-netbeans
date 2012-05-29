/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.action;

import org.netbeans.modules.jbossas7.AS7Domain;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author kulikov
 */
public class StopDomainAction extends NodeAction {

    @Override
    protected void performAction(Node[] nodes) {
        for (Node node : nodes) {
            AS7Domain as7 = node.getLookup().lookup(AS7Domain.class);
            if (as7 != null) {
                as7.stop();
            }
        }
    }

    @Override
    protected boolean enable(Node[] nodes) {
        for (Node node : nodes) {
            AS7Domain as7 = node.getLookup().lookup(AS7Domain.class);
            if (as7 != null) {
                return as7.getState() == AS7Domain.ServerState.STARTED;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "Stop domain controller";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

}
