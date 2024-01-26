package users;

import org.apache.pekko.actor.ActorSystem;
import play.api.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

public class UserExecutionContext extends CustomExecutionContext {
    @Inject
    public UserExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "user.repository");
    }

}
