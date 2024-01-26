package users;

import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import play.libs.concurrent.Futures;
import play.libs.concurrent.ClassLoaderExecutionContext;
import play.mvc.Results;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.mvc.Http.Status.*;

public class UserAction extends play.mvc.Action.Simple{
    private final ClassLoaderExecutionContext ec;
    private final Futures futures;

    @Singleton
    @Inject
    public UserAction(ClassLoaderExecutionContext ec, Futures futures) {
        this.ec = ec;
        this.futures = futures;
    }

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        if (request.accepts("application/json")) {
            return futures.timeout(doCall(request), 1L, TimeUnit.SECONDS).exceptionally(e -> {
                return (Results.status(GATEWAY_TIMEOUT, views.html.timeout.render()));
            }).whenComplete((r, e) -> System.out.println("done"));
        } else {
            return completedFuture(
                    status(NOT_ACCEPTABLE, "We only accept application/json")
            );
        }
    }

    private CompletionStage<Result> doCall(Http.Request request) {
        return delegate.call(request).handleAsync((result, e) -> {
            if (e != null) {
                if (e instanceof CompletionException) {
                    Throwable completionException = e.getCause();
                    return internalServerError();
                } else {
                    return internalServerError();
                }
            } else {
                return result;
            }
        }, ec.current());
    }
}
