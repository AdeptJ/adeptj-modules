/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modularweb.thymeleaf.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.Bundle;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 * BundleTemplateResource.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class BundleTemplateResource implements ITemplateResource {

    private final Bundle bundle;

    private final String path;

    private final String characterEncoding;

    public BundleTemplateResource(Bundle bundle, String path, String characterEncoding) {
        this.bundle = bundle;
        this.path = path;
        this.characterEncoding = characterEncoding;
    }

    @Override
    public String getDescription() {
        return path;
    }

    @Override
    public String getBaseName() {
        return computeBaseName(path);
    }

    @Override
    public boolean exists() {
        return bundle.getEntry(path) != null;
    }

    @Override
    public Reader reader() throws IOException {
        InputStream is = IOUtils.toBufferedInputStream(this.bundle.getEntry(path).openStream());
        BufferedReader reader = null;
        if (StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            reader = IOUtils.toBufferedReader(new InputStreamReader(is));
        } else {
            reader = IOUtils.toBufferedReader(new InputStreamReader(is, this.characterEncoding));
        }
        return reader;
    }

    @Override
    public ITemplateResource relative(String relativeLocation) {
        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");
        final String fullRelativeLocation = computeRelativeLocation(this.path, relativeLocation);
        return new BundleTemplateResource(this.bundle, fullRelativeLocation, this.characterEncoding);
    }

    private String computeRelativeLocation(final String location, final String relativeLocation) {
        final int separatorPos = location.lastIndexOf('/');
        if (separatorPos != -1) {
            final StringBuilder relativeBuilder = new StringBuilder(location.length() + relativeLocation.length());
            relativeBuilder.append(location, 0, separatorPos);
            if (relativeLocation.charAt(0) != '/') {
                relativeBuilder.append('/');
            }
            relativeBuilder.append(relativeLocation);
            return relativeBuilder.toString();
        }
        return relativeLocation;
    }

    private String computeBaseName(final String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        // First remove a trailing '/' if it exists
        final String basePath = (path.charAt(path.length() - 1) == '/' ? path.substring(0, path.length() - 1) : path);

        final int slashPos = basePath.lastIndexOf('/');
        if (slashPos != -1) {
            final int dotPos = basePath.lastIndexOf('.');
            if (dotPos != -1 && dotPos > slashPos + 1) {
                return basePath.substring(slashPos + 1, dotPos);
            }
            return basePath.substring(slashPos + 1);
        }
        return (basePath.length() > 0 ? basePath : null);
    }

}
