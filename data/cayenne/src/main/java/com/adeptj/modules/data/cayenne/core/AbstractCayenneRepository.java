package com.adeptj.modules.data.cayenne.core;

import com.adeptj.modules.data.cayenne.CayenneRepository;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public class AbstractCayenneRepository implements CayenneRepository {

    private ServerRuntime cayenne;

    public ServerRuntime getCayenne() {
        return cayenne;
    }

    public void setCayenne(ServerRuntime cayenne) {
        this.cayenne = cayenne;
    }
}
