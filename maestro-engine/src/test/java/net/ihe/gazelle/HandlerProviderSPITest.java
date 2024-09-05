package net.ihe.gazelle;

import net.ihe.gazelle.business.step.validation.ValidationHandler;
import net.ihe.gazelle.handler.HandlerProviderSPI;
import net.ihe.gazelle.runner.Handler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HandlerProviderSPITest {

    @Test
    void getHandlerWithoutException(){
        HandlerProviderSPI handlerProviderSPI = new HandlerProviderSPI();
        Handler handler = handlerProviderSPI.getHandler(ValidationHandler.class, "matchbox");
        assertEquals(ValidationHandler.class, handler.getClass());
    }

}
