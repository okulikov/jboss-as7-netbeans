/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.nodes.domain;

import org.netbeans.modules.jbossas7.AS7Domain;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author kulikov
 */
public class Hk2ItemNode extends AbstractNode {


    public Hk2ItemNode(AS7Domain instance, String name) {
        super(Children.LEAF, instance.getLookup());
        this.setDisplayName(name);
    }

    public Hk2ItemNode(AS7Domain instance, Children children, String name) {
        super(children, instance.getLookup());
        this.setDisplayName(name);
    }

}
