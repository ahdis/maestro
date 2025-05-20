package net.ihe.gazelle.maestro.technical.ws;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.maestro.technical.dto.testrun.TestRunDTO;

import java.util.List;

@Path("/testRuns")
public interface TestRunController {

    /**
     * Different response can be sent depending on what happen during execution
     * <p>
     *     - <b>Execution went well</b> : 200 + TestReport in body
     *     - <b>Request couldn't be Parsed</b> : 400 + ParsingErrorException or MissingArgumentException
     *     - <b>TestRun isn't valid</b> : 400 + InvalidTestRunException
     *     - <b>Request try to execute an unsupported Step</b> : 400 + NotProvidedStep
     *     - <b>Request doesn't match with Json syntax</b> : 406 + Empty body
     *     - <b>Unknown Error occurred</b> : 500 + Exception
     *     - <b>Timeout exception</b> : 504 + message explaining why timeout occurred
     * </p>
     * @param testRuns A list of TestRun
     * @return A response with a status code and a body containing TestReport or an exception
     */
    @POST
    @Path("/run")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response run(List<TestRunDTO> testRuns);
}
