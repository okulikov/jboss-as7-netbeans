/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.nodes;

import java.awt.Component;
import java.awt.Image;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.jbossas7.AS7Standalone;
import org.netbeans.modules.jbossas7.action.*;
import org.netbeans.modules.jbossas7.nodes.Hk2InstanceChildren;
import org.netbeans.modules.jbossas7.nodes.Refreshable;
import org.netbeans.modules.jbossas7.nodes.action.RefreshModulesCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Mutex;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author kulikov
 */
public class Hk2StandaloneNode extends AbstractNode implements ChangeListener {

    private static final String ICON_BASE = "org/netbeans/modules/jbossas7/nodes/jbossas7.png";
    private AS7Standalone serverInstance;
    private InstanceContent instanceContent;

    public Hk2StandaloneNode(final AS7Standalone instance, boolean isFullNode) {

        this(instance, new InstanceContent(), isFullNode);

//        if (isFullNode) {
//            instance.getCommonSupport().refresh();
//        }

    }

    private Hk2StandaloneNode(final AS7Standalone instance, final InstanceContent ic, boolean isFullNode) {
        super(isFullNode ? new Hk2InstanceChildren(instance) : Children.LEAF,
                new ProxyLookup(new AbstractLookup(ic), instance.getLookup()));

        serverInstance = instance;
        instanceContent = ic;

        setIconBaseWithExtension(ICON_BASE);

        if (isFullNode) {
            serverInstance.addChangeListener(WeakListeners.change(this, serverInstance));
            instanceContent.add(new RefreshModulesCookie() {

                @Override
                public void refresh() {
                    refresh(null, null);
                }

                @Override
                public void refresh(String expected, String unexpected) {
                    System.out.println("----- REFRESH");
                    Children children = getChildren();

                    if (children instanceof Refreshable) {
                        ((Refreshable) children).updateKeys();
                    }
                }
            });

        }

    }

    @Override
    public String getDisplayName() {
        return this.serverInstance.getDisplayName();
    }

    @Override
    public String getShortDescription() {
        return "JBoss AS7";
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            SystemAction.get(StartServerAction.class),
            SystemAction.get(DebugServerAction.class),
            SystemAction.get(RestartServerAction.class),
            SystemAction.get(StopServerAction.class),
            null,
            SystemAction.get(RemoveAction.class),
            null,
            SystemAction.get(ViewAdminConsoleAction.class),
            SystemAction.get(ViewServerLogAction.class)
        };
    }

    @Override
    public boolean hasCustomizer() {
        return true;
    }

    @Override
    public Component getCustomizer() {
        return new javax.swing.JPanel();
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(ICON_BASE);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return ImageUtilities.loadImage(ICON_BASE);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
                        System.out.println("----- state changed----");

        Mutex.EVENT.readAccess(new Runnable() {

            @Override
            public void run() {
                System.out.println("----- state changed----");
                fireIconChange();
            }
        });
    }
}
