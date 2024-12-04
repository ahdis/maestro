package net.ihe.gazelle.maestro;

import java.util.ArrayList;
import java.util.List;

/**
 * A structure contains in TestRun defining TestRun's steps.
 * <p>
 *     - <b>id</b> TestRun id
 *     - <b>name</b> a string of test name
 *     - <b>steps</b> a list of step
 * </p>
 * @see Step
 */
public class Test {

    private String id;
    private String name;
    private List<Step> steps = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Step> getSteps() {
        return new ArrayList<>(steps);
    }

    public Test setSteps(List<Step> steps) {
        this.steps = steps;
        return this;
    }

    public Test addStep(Step step) {
        steps.add(step);
        return this;
    }

}

