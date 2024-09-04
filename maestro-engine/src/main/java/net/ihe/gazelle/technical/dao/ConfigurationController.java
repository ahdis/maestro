package net.ihe.gazelle.technical.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/configurations")
@ApplicationScoped
public class ConfigurationController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfigurations() {
        return Response.ok("Hello World!").build();
    }


}
