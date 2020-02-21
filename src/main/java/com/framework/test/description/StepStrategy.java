package main.java.com.framework.test.description;

import main.java.com.framework.test.ExecutionContext;
import main.java.com.framework.test.model.TestField;
import main.java.com.framework.test.model.TestStep;

import java.time.LocalDateTime;

public abstract class StepStrategy implements IStepStrategy {
    private ExecutionContext executionContext;
    private TestStep step;

    public StepStrategy(ExecutionContext executionContext, TestStep step) {
        this.executionContext = executionContext;
        this.step = step;
    }

    public final void execute() {
        try {
            this.executionContext.takeScreenshot(this.step, "BEFORE");
            this.step.setStart(LocalDateTime.now().toString());
            this.onExecute();
            this.step.setFinish(LocalDateTime.now().toString());
            this.executionContext.takeScreenshot(this.step, "AFTER");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public abstract void onExecute();

    protected void setOutputValue(String key, String value) {
        this.step.getTestCase().getOutputValueList().put(key, value);
    }

    protected void setFieldValue(String key, String value) {
        TestField existing = null;

        for (TestField field : this.step.getTestFieldList()) {
            if (field.getKey().equals(key)) {
                existing = field;
            }
        }

        if (existing == null) existing = this.executionContext.getTestFieldFromSettings(key);

        existing.setValue(value);
    }
}