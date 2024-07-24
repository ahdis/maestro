package net.ihe.gazelle.context;

import io.smallrye.common.constraint.NotNull;
import net.ihe.gazelle.step.Step;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Context implements Map<String, Object> {
    private static final String TEST_RUN            = "TEST_RUN";
    private static final String STEP                = "STEP";
    private static final String REPORT              = ".REPORT";

    private Map<String, Object> contextMap = new ConcurrentHashMap<>();

    public Context(Map<String, Object> context) {
        this.contextMap = context;
    }

    @Override
    public int size() {
        return contextMap.size();
    }

    @Override
    public boolean isEmpty() {
        return contextMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return contextMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return contextMap.containsValue(o);
    }

    @Override
    public Object get(Object o) {
        return contextMap.get(o);
    }

    @Override
    public Object put(@NotNull String s, Object o) {
        return contextMap.put(s, o);
    }


    @Override
    public Object remove(Object o) {
        return contextMap.remove(o);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> map) {
        contextMap.putAll(map);
    }

    @Override
    public void clear() {
        contextMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return contextMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return contextMap.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return contextMap.entrySet();
    }

    public Object getTestRun() {
        return contextMap.get(TEST_RUN);
    }

    public Step getStep() {
        return (Step) contextMap.get(STEP);
    }

    public Object getContent(String s) {
        if(s.charAt(0) == '$'){
            return contextMap.get(s);
        }
        return s;
    }

    public Object getReport(String s) {
        return contextMap.get(s + REPORT);
    }

    public void setTestRun(Object testRun) {
        contextMap.put(TEST_RUN, testRun);
    }

    public void setStep(Step step) {
        contextMap.put(STEP, step);
    }

    public void setReport(String s, String report) {
        contextMap.put(s + REPORT, report);
    }

}
