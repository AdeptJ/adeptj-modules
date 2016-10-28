package com.adeptj.modularweb.security.shiro.common;

import org.apache.shiro.ShiroException;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.apache.shiro.web.env.DefaultWebEnvironment;

public class OSGiWebEnvironment extends DefaultWebEnvironment implements Initializable, Destroyable {

	@Override
	public void init() throws ShiroException {
		
	}

}
