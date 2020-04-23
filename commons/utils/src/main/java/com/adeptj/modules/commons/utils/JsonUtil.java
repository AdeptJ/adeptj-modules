/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
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
package com.adeptj.modules.commons.utils;

import javax.json.Json;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriterFactory;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Collections;

/**
 * Provides {@link Jsonb} and other objects from Jakarta Json-P.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JsonUtil {

    private static final Jsonb JSONB = JsonbBuilder.create(new JsonbConfig());

    private static final JsonReaderFactory READER_FACTORY = Json.createReaderFactory(Collections.emptyMap());

    private static final JsonWriterFactory WRITER_FACTORY = Json.createWriterFactory(Collections.emptyMap());

    public static Jsonb jsonb() {
        return JSONB;
    }

    public static JsonReaderFactory readerFactory() {
        return READER_FACTORY;
    }

    public static JsonWriterFactory writerFactory() {
        return WRITER_FACTORY;
    }
}
