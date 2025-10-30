package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;

class DirectValue implements ValueHolder {

   @Serial
   private static final long serialVersionUID = 5435920465930342207L;

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

    public boolean isValueOptional() {
       return true;
    }

}
