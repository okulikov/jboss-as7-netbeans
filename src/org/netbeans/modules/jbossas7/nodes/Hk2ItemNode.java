/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.nodes;

import org.netbeans.modules.jbossas7.AS7Instance;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author kulikov
 */
public class Hk2ItemNode extends AbstractNode {


    public Hk2ItemNode(AS7Instance instance, String name) {
        super(Children.LEAF, instance.getLookup());
        this.setDisplayName(name);
    }

    public Hk2ItemNode(AS7Instance instance, Children children, String name) {
        super(children, instance.getLookup());
        this.setDisplayName(name);
    }

}
