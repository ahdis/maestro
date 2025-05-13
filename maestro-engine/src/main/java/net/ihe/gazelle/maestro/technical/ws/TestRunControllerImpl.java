package net.ihe.gazelle.maestro.technical.ws;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.InvalidTestRunException;
import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.maestro.technical.NotProvidedStep;
import net.ihe.gazelle.maestro.technical.jobs.SynchronousMaestro;
import net.ihe.gazelle.testreport.TestReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class TestRunControllerImpl implements TestRunController {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunControllerImpl.class);

    private final SynchronousMaestro maestro;

    @Inject
    public TestRunControllerImpl(SynchronousMaestro maestro) {
        this.maestro = maestro;
    }

    @Override
    public Response run(JsonNode testRun) {
        try {
            List<TestRun> testRuns = new TestRunParser().convertJsonToTestRuns(testRun);
            TestReport testReport = maestro.executeTestSet(testRuns);
            return Response.ok(testReport).build();
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
