package users;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface UserRepository {
    CompletionStage<Stream<UserData>> list();
    CompletionStage<UserData> create(UserData postData);

}
