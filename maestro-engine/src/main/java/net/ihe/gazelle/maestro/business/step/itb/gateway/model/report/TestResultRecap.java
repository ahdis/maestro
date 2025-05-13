package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;

/**
 * Model for results in figures
 */
public class TestResultRecap implements Serializable {
    private int total;

    private int run;

    private int passed;

    private int failed;

    private int undefined;

    private int skipped;

    public TestResultRecap() {
    }

    public TestResultRecap(int total, int run, int passed, int failed, int undefined, int skipped) {
        this.total = total;
        this.run = run;
        this.passed = passed;
        this.failed = failed;
        this.undefined = undefined;
        this.skipped = skipped;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getUndefined() {
        return undefined;
    }

    public void setUndefined(int undefined) {
        this.undefined = undefined;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }
}
