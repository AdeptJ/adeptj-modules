package com.adeptj.modularweb.common.servlet.async;

import java.util.Map;

import javax.servlet.AsyncContext;

@FunctionalInterface
public interface AsyncTask {

	void doTask(AsyncContext ctx, Map<String, String[]> parameters);
}
