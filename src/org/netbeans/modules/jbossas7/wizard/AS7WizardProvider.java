/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7.wizard;

import org.netbeans.spi.server.ServerWizardProvider;
import org.openide.WizardDescriptor.InstantiatingIterator;

/**
 *
 * @author kulikov
 */
public class AS7WizardProvider implements ServerWizardProvider {

    private final static String SERVER_NAME = "JBoss AS7";

    private static AS7WizardProvider provider;

    public static AS7WizardProvider create() {
        if (provider == null) {
            provider = new AS7WizardProvider();
        }
        return provider;
    }

    @Override
    public String getDisplayName() {
        return SERVER_NAME;
    }

    @Override
    public InstantiatingIterator getInstantiatingIterator() {
        return new ServerWizardIterator();
    }
}
