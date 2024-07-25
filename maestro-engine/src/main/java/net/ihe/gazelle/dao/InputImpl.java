package net.ihe.gazelle.dao;

import net.ihe.gazelle.test.Input;

public class InputImpl implements Input {
    private String name;
    private Object value;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public InputImpl() {
    }

    public InputImpl(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
