package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;

class DirectValue implements ValueHolder {

   @Serial
   private static final long serialVersionUID = 5435920465930342207L;

   private final Serializable value;

    public DirectValue(Serializable value) {
        this.value = value;
    }

    @Override
    public Serializable getValue() {
        return value;
    }

    public boolean isValueDefined() {
        return value != null;
    }

    public boolean isValueOptional() {
       return true;
    }

}
