package net.ihe.gazelle.maestro.technical.ws.tmintegration;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * This controller is temporary while TM script is not up-to-date
 */
@Path("/v0")
public interface TMTestRunController {

    /**
     * The post method that run Maestro from a TM call
     * @param jsonNode The TM script received
     * @return status code representing if the run is well started
     */
    @POST
    @Path("/run")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response run(JsonNode tmTestRunScript);
}
