package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.PropertyRef;
import net.ihe.gazelle.maestro.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResolvablePropertyRefTest {

    Context context;

    @BeforeEach
    public void setUp() {
        context = new Context();
    }

    @Test
    void resolveRefTest(){
        Property<String> property = new Property<>("saucisse", "puré");
        context.addProperty("menu", property);
        PropertyRef<String> propertyRef = new PropertyRef<>("saucisse", "menu");
        ResolvablePropertyRef<String> resolvablePropertyRef = new ResolvablePropertyRef<>(propertyRef, context);
        assertEquals("puré", resolvablePropertyRef.getValue());
    }

    @Test
    void notInContextTest(){
        PropertyRef<String> propertyRef = new PropertyRef<>("saucisse", "menu");
        ResolvablePropertyRef<String> resolvablePropertyRef = new ResolvablePropertyRef<>(propertyRef, context);
        assertThrows(NoSuchElementException.class, resolvablePropertyRef::getValue);
    }

}
