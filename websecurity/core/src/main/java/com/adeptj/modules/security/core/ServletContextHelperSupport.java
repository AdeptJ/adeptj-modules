package com.adeptj.modules.security.core;

import org.osgi.framework.Bundle;
import org.osgi.service.http.context.ServletContextHelper;

/**
 * ServletContextHelperSupport
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class ServletContextHelperSupport extends ServletContextHelper {

    ServletContextHelperSupport(Bundle bundle) {
        super(bundle);
    }
}
