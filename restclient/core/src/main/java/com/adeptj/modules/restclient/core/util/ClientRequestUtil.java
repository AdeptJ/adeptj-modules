/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.restclient.core.util;

import java.net.URI;
import java.util.Locale;

import static com.adeptj.modules.restclient.core.util.Assert.newIAE;


/**
 * @author Rakesh Kumar, AdeptJ
 */
public class ClientRequestUtil {

    private ClientRequestUtil() {
    }

    /**
     * Borrowed with love from JDK's HttpRequestBuilderImpl.
     * <p>
     * Check the given {@link URI} for scheme and host.
     *
     * @param uri the request {@link URI}
     */
    public static void checkURI(URI uri) {
        Assert.notNull(uri, "URI can't be null!");
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw newIAE("URI with undefined scheme");
        }
        scheme = scheme.toLowerCase(Locale.US);
        if (!(scheme.equals("https") || scheme.equals("http"))) {
            throw newIAE("invalid URI scheme %s", scheme);
        }
        if (uri.getHost() == null) {
            throw newIAE("unsupported URI %s", uri);
        }
    }
}
