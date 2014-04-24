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
package com.codenvy.eclipse.ui.test;

import static com.codenvy.eclipse.ui.test.mock.ProjectServiceMock.MOCK_PROJECT_DESCRIPTION;
import static com.codenvy.eclipse.ui.test.mock.ProjectServiceMock.MOCK_PROJECT_NAME;
import static com.codenvy.eclipse.ui.test.mock.ProjectServiceMock.MOCK_PROJECT_TYPE_NAME;
import static com.codenvy.eclipse.ui.test.mock.WorkspaceServiceMock.MOCK_WORKSPACE_NAME;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.tableHasRows;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The import project wizard test.
 * 
 * @author Kevin Pollet
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ImportProjectFromCodenvyWizardTest {
    @Test
    public void testThatImportProjectFromCodenvyWizardIsInNewProjectDialog() {
        final SWTWorkbenchBot bot = new SWTWorkbenchBot();
        bot.menu("File").menu("New").menu("Project...").click();

        final SWTBotShell shell = bot.shell("New Project");
        shell.activate();

        bot.tree().expandNode("Codenvy").select("Projects from Codenvy");
        shell.close();
    }

    @Test
    public void testThatImportProjectFromCodenvyWizardIsInOtherProjectDialog() {
        final SWTWorkbenchBot bot = new SWTWorkbenchBot();
        bot.menu("File").menu("New").menu("Other...").click();

        final SWTBotShell shell = bot.shell("New");
        shell.activate();

        bot.tree().expandNode("Codenvy").select("Projects from Codenvy");
        shell.close();
    }

    @Test
    public void testThatImportProjectFromCodenvyWizardIsInImportDialog() {
        final SWTWorkbenchBot bot = new SWTWorkbenchBot();
        bot.menu("File").menu("Import...").click();

        final SWTBotShell shell = bot.shell("Import");
        shell.activate();

        bot.tree().expandNode("Codenvy").select("Existing Codenvy Projects");
        shell.close();
    }

    @Test
    public void testImportProjectFromCodenvyWizard() {
        final SWTWorkbenchBot bot = new SWTWorkbenchBot();
        bot.menu("File").menu("Import...").click();

        final SWTBotShell shell = bot.shell("Import");
        shell.activate();

        bot.tree().expandNode("Codenvy").select("Existing Codenvy Projects");
        bot.button("Next >").click();

        bot.comboBox(0).typeText("http://localhost:8080");
        Assert.assertFalse(bot.button("Next >").isEnabled());

        bot.text(0).typeText("codenvy@codenvy.com");
        Assert.assertFalse(bot.button("Next >").isEnabled());

        bot.text(1).typeText("secret");
        Assert.assertTrue(bot.button("Next >").isEnabled());

        bot.button("Next >").click();
        bot.waitUntil(tableHasRows(bot.table(0), 4));
        Assert.assertFalse(bot.button("Next >").isEnabled());

        Assert.assertEquals(MOCK_WORKSPACE_NAME, bot.table(0).cell(0, 0));

        bot.table(0).getTableItem(0).check();
        Assert.assertTrue(bot.button("Next >").isEnabled());

        bot.button("Next >").click();
        bot.waitUntil(tableHasRows(bot.table(0), 4));
        Assert.assertFalse(bot.button("Finish").isEnabled());

        Assert.assertEquals(MOCK_PROJECT_NAME, bot.table(0).cell(0, 0));
        Assert.assertEquals(MOCK_PROJECT_TYPE_NAME, bot.table(0).cell(0, 1));
        Assert.assertEquals(MOCK_PROJECT_DESCRIPTION, bot.table(0).cell(0, 2));

        bot.table(0).getTableItem(0).check();
        Assert.assertTrue(bot.button("Finish").isEnabled());

        bot.button("Finish").click();
    }
}