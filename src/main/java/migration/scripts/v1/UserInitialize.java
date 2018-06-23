package migration.scripts.v1;

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

public class UserInitialize implements CustomTaskChange {


    @Override
    public void execute(Database database) throws CustomChangeException {
        Ignite ignite = ApplicationContextHolder.get().getBean(Ignite.class);

        IgniteCache<String, BinaryObject> userCache = ignite.cache("UserCache").withKeepBinary();

        BinaryObject adminBinaryObject = ignite.binary().builder("model.User")
                .setField("name", "admin")
                .setField("isAdmin", true)
                .build();

        userCache.putIfAbsent("admin", adminBinaryObject);
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
