package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;
import java.util.List;

/**
 * Model for a single test case result
 */
public class Result implements Serializable {
    private TestInfo test;

    private EntityID simulatorID;

    private String status;

    private List<Text> notes;

    private String urlToTestInstance;

    private int instanceId;

    private String dateTime;

    private List<Step> steps;

    private List<Data> inputs;

    public Result() {
    }

    public Result(TestInfo info, String status, int instanceId, String dateTime) {
        this.test = info;
        this.status = status;
        this.instanceId = instanceId;
        this.dateTime = dateTime;
    }

    public TestInfo getTest() {
        return test;
    }

    public void setTest(TestInfo test) {
        this.test = test;
    }

    public EntityID getSimulatorID() {
        return simulatorID;
    }

    public void setSimulatorID(EntityID simulatorID) {
        this.simulatorID = simulatorID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Text> getNotes() {
        return notes;
    }

    public void setNotes(List<Text> notes) {
        this.notes = notes;
    }

    public String getUrlToTestInstance() {
        return urlToTestInstance;
    }

    public void setUrlToTestInstance(String urlToTestInstance) {
        this.urlToTestInstance = urlToTestInstance;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Data> getInputs() {
        return inputs;
    }

    public void setInputs(List<Data> inputs) {
        this.inputs = inputs;
    }
}
