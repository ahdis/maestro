package net.ihe.gazelle;

import net.ihe.gazelle.dao.InputImpl;
import org.junit.jupiter.api.Test;

class InputImplTest {

    @Test
    void setName(){
        InputImpl inputImpl = new InputImpl();
        inputImpl.setName("a");
        org.junit.jupiter.api.Assertions.assertEquals("a", inputImpl.getName());
    }

    @Test
    void setValue(){
        InputImpl input = new InputImpl("a", "b");
        input.setValue("c");
        org.junit.jupiter.api.Assertions.assertEquals("c", input.getValue());
    }

}
