package users;

import play.libs.concurrent.ClassLoaderExecutionContext;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class UserResourceHandler {
    private final ClassLoaderExecutionContext ec;
    private final UserRepository userRepository;

    @Inject
    public UserResourceHandler(UserRepository userRepository, ClassLoaderExecutionContext ec) {
        this.userRepository = userRepository;
        this.ec = ec;
    }

    public CompletionStage<Stream<UserData>> find(Http.Request request) {
        return userRepository.list().thenApplyAsync(userDataStream -> {
            return userDataStream;
        }, ec.current());
    }

    public CompletionStage<UserData> create(Http.Request request, UserData resource) {
        return userRepository.create(resource).thenApplyAsync(savedData -> {
            return savedData;
        }, ec.current());
    }

}
