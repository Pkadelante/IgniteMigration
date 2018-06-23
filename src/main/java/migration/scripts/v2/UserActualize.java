package migration.scripts.v2;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import migration.ApplicationContextHolder;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;

import javax.cache.Cache;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public class UserActualize implements CustomTaskChange {


    @Override
    public void execute(Database database) throws CustomChangeException {
        Ignite ignite = ApplicationContextHolder.get().getBean(Ignite.class);
        IgniteCache<String, BinaryObject> userCache = ignite.cache("UserCache").withKeepBinary();

        for (Cache.Entry<String, BinaryObject> userEntry : userCache) {
            BinaryObject oldUser = userEntry.getValue();
            BinaryObject newUser = oldUser.toBuilder()
                    .setField("login", oldUser.field("name"))
                    .build();
            userCache.put(userEntry.getKey(), newUser);
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
}
