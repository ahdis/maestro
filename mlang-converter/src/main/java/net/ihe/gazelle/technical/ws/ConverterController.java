package net.ihe.gazelle.technical.ws;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;

@Path("/convert")
public interface ConverterController {

    @POST
    @Path("/mlang")
    @Produces("text/mlang")
    @Consumes(MediaType.APPLICATION_JSON)
    Response convertToMlang(InputStream inputStream);

    @POST
    @Path("/json")
    @Consumes("text/mlang")
    @Produces(MediaType.APPLICATION_JSON)
    Response convertToJson(InputStream inputStream);

}
