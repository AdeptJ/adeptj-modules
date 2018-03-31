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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Class providing utility methods for Type conversion.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class PropertiesUtil {

    // Deny direct instantiation.
    private PropertiesUtil() {
    }

    public static boolean toBoolean(Object propValue, boolean defaultValue) {
        Object value = toObject(propValue);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value != null) {
            return Boolean.parseBoolean(String.valueOf(value));
        }
        return defaultValue;
    }

    public static String toString(Object propValue, String defaultValue) {
        return Objects.toString(toObject(propValue), defaultValue);
    }

    public static long toLong(Object propValue, long defaultValue) {
        Object value = toObject(propValue);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value != null) {
            try {
                return Long.valueOf(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                // don't care, fall through to default value
            }
        }
        return defaultValue;
    }

    public static int toInteger(Object propValue, int defaultValue) {
        Object value = toObject(propValue);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value != null) {
            try {
                return Integer.valueOf(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                // don't care, fall through to default value
            }
        }

        return defaultValue;
    }

    public static Object toObject(Object propValue) {
        if (propValue == null) {
            return null;
        } else if (propValue.getClass().isArray()) {
            Object[] prop = (Object[]) propValue;
            return prop.length > 0 ? prop[0] : null;
        } else if (propValue instanceof Collection<?>) {
            Collection<?> prop = (Collection<?>) propValue;
            return prop.isEmpty() ? null : prop.iterator().next();
        } else {
            return propValue;
        }
    }

    public static String[] toStringArray(Object propValue) {
        return toStringArray(propValue, null);
    }

    public static String[] toStringArray(Object propValue, String[] defaultArray) {
        if (propValue == null) {
            // no value at all
            return defaultArray;
        } else if (propValue instanceof String) {
            // single string
            return new String[]{(String) propValue};
        } else if (propValue instanceof String[]) {
            // String[]
            return (String[]) propValue;
        } else if (propValue.getClass().isArray()) {
            // other array
            Object[] valueArray = (Object[]) propValue;
            List<String> values = new ArrayList<>(valueArray.length);
            for (Object value : valueArray) {
                if (value != null) {
                    values.add(value.toString());
                }
            }
            return values.toArray(new String[0]);
        } else if (propValue instanceof Collection<?>) {
            // collection
            Collection<?> valueCollection = (Collection<?>) propValue;
            List<String> valueList = new ArrayList<>(valueCollection.size());
            for (Object value : valueCollection) {
                if (value != null) {
                    valueList.add(value.toString());
                }
            }
            return valueList.toArray(new String[0]);
        }
        return defaultArray;
    }
}
