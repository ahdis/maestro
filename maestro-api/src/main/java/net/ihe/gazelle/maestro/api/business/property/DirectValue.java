package net.ihe.gazelle.maestro.api.business.property;

class DirectValue implements ValueHolder {

    private final Object value;

    public DirectValue(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public boolean isValueDefined() {
        return value != null;
    }

}
