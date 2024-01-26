import com.google.inject.AbstractModule;
import users.JPAUserRepository;
import users.UserRepository;

public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(UserRepository.class).to(JPAUserRepository.class).asEagerSingleton();
    }
}
