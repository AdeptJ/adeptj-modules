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

package com.adeptj.modules.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * TemplateContext containing required objects for template rendering.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public final class TemplateContext extends HashMap<String, Object> implements Iterable<Map.Entry<String, Object>> {

    private static final long serialVersionUID = 5698972457184163777L;

    private transient final HttpServletRequest request;

    private transient final HttpServletResponse response;

    private transient Locale locale;

    private TemplateContext(HttpServletRequest req, HttpServletResponse resp) {
        this.request = req;
        this.response = resp;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Locale getLocale() {
        return locale;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TemplateContext withData(String key, Object value) {
        super.put(key, value);
        return this;
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return super.entrySet().iterator();
    }

    /**
     * Builder for TemplateContext.
     *
     * @author Rakesh.Kumar, AdeptJ
     */
    public static class Builder {

        private HttpServletRequest request;

        private HttpServletResponse response;

        private Locale locale;

        private Map<String, Object> data;

        private Builder() {
        }

        public Builder request(HttpServletRequest request) {
            this.request = request;
            return this;
        }

        public Builder response(HttpServletResponse response) {
            this.response = response;
            return this;
        }

        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder withData(String key, Object value) {
            if (this.data == null) {
                this.data = new HashMap<>();
            }
            this.data.put(key, value);
            return this;
        }

        public TemplateContext build() {
            TemplateContext context = new TemplateContext(this.request, this.response);
            context.locale = (this.locale == null ? Locale.ENGLISH : this.locale);
            if (this.data != null) {
                context.putAll(this.data);
            }
            return context;
        }
    }
}
