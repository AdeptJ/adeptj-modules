package com.adeptj.modules.commons.crypto;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@HttpWhiteboardResource(pattern = {"/files/*"}, prefix = "WEB-INF")
@Component(service = ResourceService.class)
public class ResourceService {
}
