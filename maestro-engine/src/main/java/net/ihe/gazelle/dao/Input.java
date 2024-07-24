package net.ihe.gazelle.dao;

public class Input {
    private String name;
    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Input() {
    }

    public Input(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
