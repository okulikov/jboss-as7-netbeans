/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.wizard;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.jbossas7.AS7Instance;
import org.netbeans.modules.jbossas7.AS7Standalone;
import org.netbeans.modules.jbossas7.AS7InstanceProvider;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;

/**
 *
 * @author kulikov
 */
public class ServerWizardIterator implements WizardDescriptor.InstantiatingIterator, ChangeListener {

    private transient AddServerLocationPanel locationPanel = null;
    private transient List<ChangeListener> listeners = new CopyOnWriteArrayList<ChangeListener>();

    private WizardDescriptor wizard;
    private transient WizardDescriptor.Panel[] panels = null;
    private int index;

    private AS7InstanceProvider as7Provider;

    private String[] steps;

    public ServerWizardIterator() {
        this.as7Provider = AS7InstanceProvider.getProvider();
        steps = new String[]{"Server location"};
    }

    @Override
    public Set instantiate() throws IOException {
        String name = (String) wizard.getProperty("ServInstWizard_displayName");
        String path = locationPanel.getServerLocation();
        String username = locationPanel.getUserName();
        String password = locationPanel.getPassword();
        Boolean isDomain = locationPanel.isDomain();

        Set<ServerInstance> result = new HashSet<ServerInstance>();

        AS7Instance server = as7Provider.createInstance(name, path, username, password, isDomain);
        server.updateModuleSupport();

        result.add(server.getCommonInstance());
        return result;
    }

    @Override
    public void initialize(WizardDescriptor wd) {
        this.wizard = wd;
    }

    @Override
    public void uninitialize(WizardDescriptor wd) {
    }

    @Override
    public Panel current() {
        WizardDescriptor.Panel result = getPanels()[index];
        JComponent component = (JComponent) result.getComponent();
        component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);  // NOI18N
        component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(index));// NOI18N
        return result;
    }

    @Override
    public String name() {
        return "JBoss AS7 Server";
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        index--;
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        listeners.add(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        listeners.remove(cl);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireChangeEvent();
    }

    protected final void fireChangeEvent() {
        ChangeEvent ev = new ChangeEvent(this);
        for (ChangeListener listener : listeners) {
            listener.stateChanged(ev);
        }
    }

    protected final WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = createPanels();
        }
        return panels;
    }

    protected WizardDescriptor.Panel[] createPanels() {
        if (locationPanel == null) {
            locationPanel = new AddServerLocationPanel(this);
            locationPanel.addChangeListener(this);
        }

        return new WizardDescriptor.Panel[]{
                    (WizardDescriptor.Panel) locationPanel
                };
    }
}
