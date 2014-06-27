/*
 * CODENVY CONFIDENTIAL
 * ________________
 * 
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.eclipse.ui.wizard.importer.pages;

import static com.codenvy.eclipse.core.utils.StringHelper.isNullOrEmpty;
import static com.codenvy.eclipse.ui.Images.WIZARD_LOGO;
import static java.util.Arrays.asList;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.codenvy.eclipse.ui.CodenvyUIPlugin;
import com.codenvy.eclipse.ui.preferences.CodenvyPreferencesInitializer;
import com.codenvy.eclipse.ui.utils.SecureStorageHelper;
import com.codenvy.eclipse.ui.widgets.ComboAutoCompleteField;
import com.codenvy.eclipse.ui.wizard.importer.ImportProjectFromCodenvyWizard;
import com.google.common.collect.ObjectArrays;

/**
 * Authentication wizard page. In this wizard page the user authenticates with the Codenvy platform by it's URL, Username and Password.
 * 
 * @author Kevin Pollet
 * @author Stéphane Daviet
 */
public class AuthenticationWizardPage extends WizardPage implements IPageChangingListener {
    @SuppressWarnings("unused")
    private ComboAutoCompleteField urlProposals;
    private Combo                  urls;
    @SuppressWarnings("unused")
    private ComboAutoCompleteField usernameProposals;
    private Combo                  usernames;
    private Text                   password;
    private Button                 storeUserCredentials;

    /**
     * Constructs an instance of {@link AuthenticationWizardPage}.
     */
    public AuthenticationWizardPage() {
        super(AuthenticationWizardPage.class.getSimpleName());

        setTitle("Codenvy Authentication");
        setDescription("Authenticate with your Codenvy account");
        setImageDescriptor(CodenvyUIPlugin.getDefault().getImageRegistry().getDescriptor(WIZARD_LOGO));
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;

        final Composite wizardContainer = new Composite(parent, SWT.NONE);
        wizardContainer.setLayout(gridLayout);

        final Label hostLabel = new Label(wizardContainer, SWT.NONE);
        hostLabel.setText("URL:");

        urls = new Combo(wizardContainer, SWT.DROP_DOWN | SWT.BORDER | SWT.FOCUSED);
        urls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        urlProposals = new ComboAutoCompleteField(urls);
        for (String url : CodenvyPreferencesInitializer.parseString(CodenvyUIPlugin.getDefault()
                                                                                   .getPreferenceStore()
                                                                                   .getString(CodenvyPreferencesInitializer.REMOTE_REPOSITORIES_LOCATION_KEY_NAME))) {
            urls.add(url);
        }
        urls.select(0);

        final Label usernameLabel = new Label(wizardContainer, SWT.NONE);
        usernameLabel.setText("Username:");

        usernames = new Combo(wizardContainer, SWT.DROP_DOWN | SWT.BORDER);
        usernames.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        usernameProposals =
                            new ComboAutoCompleteField(usernames);
        autoFillUsernames();

        final Label passwordLabel = new Label(wizardContainer, SWT.NONE);
        passwordLabel.setText("Password:");

        password = new Text(wizardContainer, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        storeUserCredentials = new Button(wizardContainer, SWT.CHECK | SWT.BORDER);
        storeUserCredentials.setText("Store these user credentials in Eclipse secure storage.");
        storeUserCredentials.setSelection(true);
        storeUserCredentials.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        final PageCompleteListener pageCompleteListener = new PageCompleteListener();
        urls.addKeyListener(pageCompleteListener);
        urls.addSelectionListener(pageCompleteListener);
        usernames.addKeyListener(pageCompleteListener);
        usernames.addSelectionListener(pageCompleteListener);
        password.addKeyListener(pageCompleteListener);
        storeUserCredentials.addKeyListener(pageCompleteListener);

        final AutofillFieldsListener autofillFieldsListener = new AutofillFieldsListener();
        urls.addModifyListener(autofillFieldsListener);
        usernames.addModifyListener(autofillFieldsListener);

        setControl(wizardContainer);
    }


    @Override
    public void handlePageChanging(final PageChangingEvent event) {
        final ImportProjectFromCodenvyWizard wizard = (ImportProjectFromCodenvyWizard)getWizard();
        final IWizardPage targetPage = (IWizardPage)event.getTargetPage();

        if (isCurrentPage() && wizard.getProjectWizardPage().getName().equals(targetPage.getName())) {
            // We add the new location to preferences
            final IPreferenceStore codenvyPreferenceStore = CodenvyUIPlugin.getDefault().getPreferenceStore();
            final String[] locations =
                                       CodenvyPreferencesInitializer.parseString(codenvyPreferenceStore.getString(CodenvyPreferencesInitializer.REMOTE_REPOSITORIES_LOCATION_KEY_NAME));

            if (!asList(locations).contains(urls.getText())) {
                codenvyPreferenceStore.setValue(CodenvyPreferencesInitializer.REMOTE_REPOSITORIES_LOCATION_KEY_NAME,
                                                CodenvyPreferencesInitializer.createList(ObjectArrays.concat(urls.getText(), locations)));
            }
        }
    }

    /**
     * Returns the Codenvy platform url.
     * 
     * @return the Codenvy platform url never {@code null}.
     */
    public String getURL() {
        return urls.getText();
    }

    /**
     * Returns the user name.
     * 
     * @return the username never {@code null}.
     */
    public String getUsername() {
        return usernames.getText();
    }

    /**
     * Returns the user password.
     * 
     * @return the password never {@code null}.
     */
    public String getPassword() {
        return password.getText();
    }

    /**
     * Returns if the user password must be stored for later use.
     * 
     * @return {@code true} if the user password must be stored for later use, {@code false} otherwise.
     */
    public boolean isStoreUserCredentials() {
        return storeUserCredentials.getSelection();
    }

    private void autoFillUsernames() {
        if (!isNullOrEmpty(urls.getText())) {
            final String currentUsername = usernames.getText();
            usernames.removeAll();

            for (String oneUsername : SecureStorageHelper.getUsernamesForURL(urls.getText())) {
                if (!currentUsername.equals(oneUsername)) {
                    usernames.add(oneUsername);
                }
            }

            usernames.setText(currentUsername);
        }
    }

    private void autoFillPassword() {
        if (!isNullOrEmpty(usernames.getText())) {
            final String storedPassword = SecureStorageHelper.getPassword(urls.getText(), usernames.getText());
            if (storedPassword != null && !storedPassword.isEmpty()) {
                password.setText(storedPassword);
            }
        }
    }

    private boolean isBlankFields() {
        final boolean isUrlsBlank = isNullOrEmpty(urls.getText());
        final boolean isUsernameBlank = isNullOrEmpty(usernames.getText());
        final boolean isPasswordBlank = isNullOrEmpty(password.getText());

        return isUrlsBlank || isUsernameBlank || isPasswordBlank;
    }

    private class PageCompleteListener implements KeyListener, SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            setPageComplete(!isBlankFields());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            setPageComplete(!isBlankFields());
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }
    }

    private class AutofillFieldsListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            if (e.widget == urls) {
                autoFillUsernames();
            }
            if (e.widget == urls || e.widget == usernames) {
                autoFillPassword();
            }
            setPageComplete(!isBlankFields());
        }
    }
}
