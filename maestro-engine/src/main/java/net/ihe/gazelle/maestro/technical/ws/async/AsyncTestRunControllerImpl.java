package net.ihe.gazelle.maestro.technical.ws.async;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.InvalidTestRunException;
import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.maestro.technical.NotProvidedStep;
import net.ihe.gazelle.maestro.technical.jobs.SynchronousMaestro;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;
import net.ihe.gazelle.maestro.technical.ws.ParsingErrorException;
import net.ihe.gazelle.maestro.technical.ws.TestRunParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RequestScoped
public class AsyncTestRunControllerImpl implements AsyncTestRunController {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncTestRunControllerImpl.class);

    private final AsynchronousMaestro asynchronousMaestro;

    @Inject
    public AsyncTestRunControllerImpl(AsynchronousMaestro asynchronousMaestro) {
        this.asynchronousMaestro = asynchronousMaestro;
    }

    @Override
    public Response run(JsonNode testRun) {
        try {
            List<TestRun> testRuns = new TestRunParser().convertJsonToTestRuns(testRun);
            asynchronousMaestro.startTestSet(testRuns);
            return Response.ok().build();
        } catch (WebApplicationException e) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT.getStatusCode()).entity(SynchronousMaestro.TIMEOUT_MESSAGE).build();
        } catch (ParsingErrorException | MissingArgumentException e) {
            LOG.debug("Parsing error", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("the request doesn't match expected syntax " + e).build();
        } catch (InvalidTestRunException | IllegalArgumentException e) {
            LOG.debug("TestRun format Exception", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("the request contains a testRun with bad format" + e).build();
        } catch (NotProvidedStep e) {
            LOG.debug(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("Your script contains a non provided testreport" + e).build();
        } catch (Exception e) {
            LOG.error("Unknown error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity("an error occurred during execution: " + e).build();
        }
    }
}
