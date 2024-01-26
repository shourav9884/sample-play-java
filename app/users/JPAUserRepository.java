package users;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.pekko.pattern.CircuitBreaker;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Singleton
public class JPAUserRepository implements UserRepository{
    private final JPAApi jpaApi;
    private final UserExecutionContext ec;

    @Inject
    public JPAUserRepository(JPAApi api, UserExecutionContext ec) {
        this.jpaApi = api;
        this.ec = ec;
    }


    @Override
    public CompletionStage<Stream<UserData>> list() {
        return supplyAsync(() -> wrap(em -> select(em)), ec);
    }

    @Override
    public CompletionStage<UserData> create(UserData postData) {
        return supplyAsync(() -> wrap(em -> insert(em, postData)), ec);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Stream<UserData> select(EntityManager em) {
        TypedQuery<UserData> query = em.createQuery("SELECT u FROM UserData u", UserData.class);
        return query.getResultList().stream();
    }

    private UserData insert(EntityManager em, UserData userData) {
        return em.merge(userData);
    }
}
