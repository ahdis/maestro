package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;
import java.util.List;

public class Step implements Serializable {
    private String id;

    private String type;

    private String result;

    private List<Data> outputs;

    public Step(String id, String type, String result) {
        this.id = id;
        this.type = type;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

    public List<Data> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Data> outputs) {
        this.outputs = outputs;
    }
}
