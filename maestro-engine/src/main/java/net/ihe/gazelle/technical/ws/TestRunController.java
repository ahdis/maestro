package net.ihe.gazelle.technical.ws;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.business.engine.NotInitializedReferenceException;
import net.ihe.gazelle.business.engine.NullPropertyValue;
import net.ihe.gazelle.business.step.NotProvidedStep;
import net.ihe.gazelle.business.test.TestRunImpl;
import net.ihe.gazelle.business.validation.InvalidTestRunException;
import net.ihe.gazelle.engine.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Path("/testRun")
@ApplicationScoped
public class TestRunController {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunController.class);

    public TestRunController() {/*Empty Constructor*/}

    @Inject
    Engine engine;

    @POST
    @Path("/run")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response run(InputStream bodyStream) throws IOException {
        try {
            List<TestRunImpl> testRunImpls = new TestRunParser().convertInputStreamToTestRuns(bodyStream);


            for (TestRunImpl testRunImpl : testRunImpls) {
                engine.run(testRunImpl);
                testRunImpl.setReport(engine.getReports());
            }
            return Response.ok(testRunImpls).build();
        } catch (IOException | ParsingErrorException | MissingArgumentException e) {
            LOG.debug("Parsing error", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("the request doesn't match expected syntax " + e).build();
        } catch (InvalidTestRunException e){
            LOG.debug("TestRun format Exception", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("the request contains a testRun with bad format" + e).build();
        } catch (NotInitializedReferenceException e){
            LOG.error("A variable isn't initialized");
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("Your script contains a variable not initialized" + e).build();
        } catch (NotProvidedStep e){
            LOG.error(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("Your script contains a non provided step" + e).build();
        } catch (NullPropertyValue e){
            LOG.error(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("One of the property has a null value" +e).build();
        }
        catch (Exception e) {
            LOG.error("Unknown error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity("an error occurred during execution: " + e).build();
        } finally {
            bodyStream.close();
        }

    }



}
