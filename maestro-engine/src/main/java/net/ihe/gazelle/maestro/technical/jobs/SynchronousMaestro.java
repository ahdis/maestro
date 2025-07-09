package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.business.engine.MaestroProducer;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.FinishTestSetEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Class used to do request to Maestro synchronously. When Maestro is called to execute a list of TestRun,
 * we create a Completable future, and when execution is done, completable future is completed with TestReport.
 * There is a Timeout on completable future, if time is up, completableFuture is completed with a TestReport with
 * undefined result and an unexpectedError TimeoutException.
 */
@ApplicationScoped
@Default
public class SynchronousMaestro {

    private static final Logger LOG = LoggerFactory.getLogger(SynchronousMaestro.class);
    public static final String TIMEOUT_MESSAGE = "The test execution timed out. Either the test was too long for a synchronous call, either something fatal happened and you may need to contact an administrator";

    private final Map<String, CompletableFuture<TestReport>> completableFutures = new ConcurrentHashMap<>();
    private final int defaultTimeout;
    private final MaestroProducer maestroProducer;

    @Inject
    public SynchronousMaestro(@ConfigProperty(name = "gzl.maestro.timeout", defaultValue = "60") int defaultTimeout, MaestroProducer maestroProducer) {
        this.defaultTimeout = defaultTimeout;
        this.maestroProducer = maestroProducer;
    }

    public TestReport executeTestSet(List<TestRun> testRuns) throws ExecutionException, InterruptedException {
        CompletableFuture<TestReport> future = startTestSet(testRuns);
        return future.orTimeout(defaultTimeout, TimeUnit.SECONDS)
                .exceptionally(ex ->
                {
                    if (ex instanceof TimeoutException) {
                        LOG.error(TIMEOUT_MESSAGE, ex);
                        return new TestReportBuilder()
                                .addUnexpectedError(new UnexpectedErrorBuilder().fromTrowable(ex))
                                .build();
                    }
                    throw new CompletionException(ex);
                })
                .get();
    }

    private CompletableFuture<TestReport> startTestSet(List<TestRun> testRuns) {
        TestSetRun testSetRun = new TestSetRun(UUID.randomUUID().toString(), testRuns);
        CompletableFuture<TestReport> future = new CompletableFuture<>();
        completableFutures.put(testSetRun.testSetId(), future);
        maestroProducer.executeTestSet(testSetRun);
        return future;
    }



    public void completeFuture(FinishTestSetEvent finishTestSetEvent) {
        completableFutures.get(finishTestSetEvent.getTestSetRun().testSetId()).complete(finishTestSetEvent.getTestReport());
    }
}
