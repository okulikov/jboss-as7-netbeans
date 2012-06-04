/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.wizard;

import java.awt.Component;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author kulikov
 */
public class AddServerLocationPanel implements WizardDescriptor.FinishablePanel, ChangeListener {

    private ServerWizardIterator wizardIterator;
    private AddServerLocationVisualPanel component;
    private WizardDescriptor wizard;
    private transient List<ChangeListener> listeners = new CopyOnWriteArrayList<ChangeListener>();

    public AddServerLocationPanel(ServerWizardIterator wizardIterator) {
        this.wizardIterator = wizardIterator;
        wizard = null;
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new AddServerLocationVisualPanel(wizardIterator);
            component.addChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("jboss-as7");
    }

    @Override
    public void readSettings(Object data) {
        if (wizard == null) {
            wizard = (WizardDescriptor)data;
        }
    }

    @Override
    public void storeSettings(Object data) {
    }

    @Override
    public boolean isValid() {
        //TODO: add dir checking
        return true;
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
        fireChangeEvent(e);
    }

    private void fireChangeEvent(ChangeEvent e) {
        for (ChangeListener l : listeners) {
            l.stateChanged(e);
        }
    }

    public String getServerLocation() {
        return component.getServerLocation();
    }

    public String getUserName() {
        return component.getUserName();
    }
    public String getPassword() {
        return component.getPassword();
    }

    public boolean isDomain() {
        return component.isDomain();
    }
}
