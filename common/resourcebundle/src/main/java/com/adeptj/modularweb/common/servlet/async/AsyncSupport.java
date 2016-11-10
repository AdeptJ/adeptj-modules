package com.adeptj.modularweb.common.servlet.async;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AsyncSupport {

	private HttpServletRequest req;

	private HttpServletResponse resp;

	private AsyncContext asyncContext;

	public AsyncSupport(HttpServletRequest req, HttpServletResponse resp, boolean startAsyncEager) {
		this.req = req;
		this.resp = resp;
		if(startAsyncEager) {
			this.startAsyncContext();
		}
	}

	public void startAsyncContext() {
		if(!this.req.isAsyncStarted()) {
			this.asyncContext = this.req.startAsync();
		}
	}

	public void completeAsyncContext() {
		this.asyncContext.complete();
	}

	public void doAsyncRead(AsyncTask task) throws IOException {
		ServletInputStream is = this.req.getInputStream();
		is.setReadListener(new AsyncReadListener());
	}

	public void doAsyncWrite(AsyncTask task) throws IOException {
		ServletOutputStream os = this.resp.getOutputStream();
		os.setWriteListener(new AsyncWriteListener());
	}

	public AsyncContext getAsyncContext() {
		return asyncContext;
	}
}
