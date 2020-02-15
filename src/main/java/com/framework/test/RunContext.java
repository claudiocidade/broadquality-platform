package main.java.com.framework.test;

import main.java.com.framework.configuration.ConfigurationManager;
import main.java.com.framework.test.model.TestCase;
import main.java.com.framework.test.model.TestPlan;
import main.java.com.framework.test.model.TestStep;
import main.java.com.framework.test.model.serialization.TestRunSerializer;
import main.java.com.framework.test.selenium.WebDriverManager;

public class RunContext {

    private static RunContext current;

    private TestPlan run;

    private RunContext() {
        this.setCurrentTestRun(new TestRunSerializer().retrieve(ConfigurationManager.TESTRUN_SETTINGS_FILEPATH, true));
        this.setCurrentTestRun(new TestRunSerializer().retrieve(ConfigurationManager.TESTRUN_SETTINGS_FILEPATH, true));

        Runtime.getRuntime().addShutdownHook(new Thread(this::onExitRuntime));
    }

    public synchronized TestPlan getCurrentTestRun() {
        return this.run;
    }

    public synchronized void setCurrentTestRun(TestPlan value) {
        this.run  = value;
    }

    public static synchronized RunContext getCurrent() {
        if (RunContext.current == null)
            RunContext.current = new RunContext();

        return RunContext.current;
    }

    public void save() throws Throwable {
        String resultFile = ConfigurationManager.getCurrent().getApplicationSettings().getTestRunResultFilePath();

        TestRunSerializer testRunSerializer = new TestRunSerializer();

        testRunSerializer.save(this.getCurrentTestRun(), resultFile);
    }

    public TestCase getTestCaseById(String testCaseId) {
        for (TestCase tc : this.run.getTestCaseList()) {
            if (tc.getExternalId().equals(testCaseId)) return tc;
        }
        return null;
    }

    public TestStep getTestStepById(String testCaseId, String actionPath) {
        for (TestStep ts : this.getTestCaseById(testCaseId).getTestStepList()) {
            if (ts.getActionPath().equals(actionPath)) return ts;
        }
        return null;
    }

    public void onExitRuntime() {
        try {
            this.save();

            WebDriverManager.getCurrent().stopWebDriver();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
