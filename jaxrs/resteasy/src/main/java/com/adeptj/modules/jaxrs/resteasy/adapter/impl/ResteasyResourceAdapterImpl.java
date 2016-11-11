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
package com.adeptj.modules.jaxrs.resteasy.adapter.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import com.adeptj.modules.jaxrs.resteasy.adapter.api.ResteasyResourceAdapter;
import com.adeptj.modules.jaxrs.resteasy.common.JaxrsResourceCollector;
import com.adeptj.modules.jaxrs.resteasy.internal.JaxrsResourcePackageBundleListener;
import com.adeptj.modules.jaxrs.resteasy.osgi.dispatcher.ExtHttpServletDispatcher;

import javax.servlet.Servlet;

/**
 * ResteasyResourceAdapter Implementation.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Service(ResteasyResourceAdapter.class)
@Component(immediate = true)
public class ResteasyResourceAdapterImpl implements ResteasyResourceAdapter {

    private BundleContext bundleContext;

    private JaxrsResourcePackageBundleListener listener;

    @Reference(target = "(osgi.http.whiteboard.servlet.name=Resteasy HttpServletDispatcher)")
    private Servlet servlet;
    
    private ExtHttpServletDispatcher dispatcherServlet;

    @Override
    public void addSingletonResource(String alias, Object resource) {
        if (dispatcherServlet.isInitialized() && !dispatcherServlet.getProcessedResources().containsKey(alias)) {
            dispatcherServlet.getDispatcher().getRegistry().addSingletonResource(resource);
        } else {
            // Collect the resource and process when HttpServletDispatcher initialized completely.
            JaxrsResourceCollector.INSTANCE.addResource(alias, resource);
        }
    }

    @Override
    public <T> void removeSingletonResource(String alias, Class<T> clazz) {
    	dispatcherServlet.getDispatcher().getRegistry().removeRegistrations(clazz);
    }

    @Activate
    protected void activate(ComponentContext context) {
    	dispatcherServlet = (ExtHttpServletDispatcher) servlet;
        bundleContext = context.getBundleContext();
        listener = new JaxrsResourcePackageBundleListener(bundleContext, this);
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        listener.unregisterAll();
        bundleContext = null;
    }
}
