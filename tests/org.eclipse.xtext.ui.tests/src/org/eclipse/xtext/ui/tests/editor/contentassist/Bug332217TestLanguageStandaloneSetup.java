
package org.eclipse.xtext.ui.tests.editor.contentassist;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class Bug332217TestLanguageStandaloneSetup extends Bug332217TestLanguageStandaloneSetupGenerated{

	public static void doSetup() {
		new Bug332217TestLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

