package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

public class ResponseReport {

    private String status;

    private String date;

    private TestReport report;

    public ResponseReport(String status, String date, TestReport report) {
        this.status = status;
        this.date = date;
        this.report = report;
    }

    public TestReport getReport() {
        return report;
    }

    public void setReport(TestReport report) {
        this.report = report;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
