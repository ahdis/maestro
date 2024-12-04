package net.ihe.gazelle.testreport;

import java.util.Objects;

/**
 * Business rules <br>
 * at least 0 for every int <br>
 * total = sum(passed, failed, undefined)
 */
public class TestCounters {

    private int total;

    private int passed;

    private int failed;

    private int undefined;

    private int unexpectedErrors;

    public TestCounters() {
        total = 0;
        passed = 0;
        failed = 0;
        undefined = 0;
        unexpectedErrors = 0;
    }

    public int getTotal() {
        return total;
    }

    public TestCounters setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getPassed() {
        return passed;
    }

    public TestCounters setPassed(int passed) {
        this.passed = passed;
        return this;
    }

    public int getFailed() {
        return failed;
    }

    public TestCounters setFailed(int failed) {
        this.failed = failed;
        return this;
    }

    public int getUndefined() {
        return undefined;
    }

    public TestCounters setUndefined(int undefined) {
        this.undefined = undefined;
        return this;
    }

    public int getUnexpectedErrors() {
        return unexpectedErrors;
    }

    public TestCounters setUnexpectedErrors(int unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors;
        return this;
    }

    private void incrementTotal() {
        total++;
    }

    public void incrementPassed() {
        passed++;
        incrementTotal();
    }

    public void incrementFailed() {
        failed++;
        incrementTotal();
    }

    public void incrementUndefined() {
        undefined++;
        incrementTotal();
    }

    public void incrementUnexpectedErrors() {
        unexpectedErrors++;
    }

    public void incrementUnexpectedErrors(int unexpectedErrors) {
        this.unexpectedErrors += unexpectedErrors;
    }

    public void addNumbersFromSubCounters(TestCounters subCounters) {
        total += subCounters.getTotal();
        passed += subCounters.getPassed();
        failed += subCounters.getFailed();
        undefined += subCounters.getUndefined();
        unexpectedErrors += subCounters.getUnexpectedErrors();
    }

    public boolean isTotalValid() {
        return total >= 0 && total == passed + failed + undefined;
    }

    public boolean isPassedValid() {
        return total >= 0;
    }

    public boolean isFailedValid() {
        return failed >= 0;
    }

    public boolean isUndefinedValid() {
        return undefined >= 0;
    }

    public boolean isUnexpectedErrorsValid() {
        return unexpectedErrors >= 0;
    }

    public boolean isValid() {
        return isTotalValid()
                && isPassedValid()
                && isFailedValid()
                && isUndefinedValid()
                && isUnexpectedErrorsValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCounters that)) return false;
        return total == that.total
                && passed == that.passed
                && failed == that.failed
                && undefined == that.undefined
                && unexpectedErrors == that.unexpectedErrors;
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, passed, failed, undefined, unexpectedErrors);
    }
}
