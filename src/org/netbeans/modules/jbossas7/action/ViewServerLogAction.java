/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.action;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author kulikov
 */
public class ViewServerLogAction extends NodeAction {

    @Override
    protected void performAction(Node[] nodes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean enable(Node[] nodes) {
        return false;
    }

    @Override
    public String getName() {
        return "View server log";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

}
