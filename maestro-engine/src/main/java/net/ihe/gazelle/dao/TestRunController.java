package net.ihe.gazelle.dao;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.business.engine.TestEngine;
import net.ihe.gazelle.business.exception.InvalidTestRunException;
import net.ihe.gazelle.business.exception.MissingArgumentException;
import net.ihe.gazelle.business.exception.ParsingErrorException;
import net.ihe.gazelle.business.test.TestRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Path("/testRun")
public class TestRunController {

    private final static Logger LOG = LoggerFactory.getLogger(TestRunController.class);

    public TestRunController() {/*Empty Constructor*/}

    @POST
    @Path("/run")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response run(InputStream bodyStream) throws IOException {
        try {
            List<TestRun> testRuns = new TestRunParser().convertInputStreamToTestRuns(bodyStream);

            TestEngine testEngine = new TestEngine();
            for (TestRun testRun : testRuns) {
                testEngine.run(testRun);
                testRun.setReport(testEngine.getReports());
            }
            return Response.ok(testRuns).build();
        } catch (IOException | ParsingErrorException | MissingArgumentException e) {
            LOG.debug("Parsing error", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "the request doesn't match expected syntax " + e).build();
        } catch (InvalidTestRunException e){
            LOG.debug("TestRun format Exception", e);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "the request contains a testRun with bad format" + e).build();
        }
        catch (Exception e) {
            LOG.error("Unknown error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "an error occurred during execution: " + e).build();
        } finally {
            bodyStream.close();
        }

    }



}
