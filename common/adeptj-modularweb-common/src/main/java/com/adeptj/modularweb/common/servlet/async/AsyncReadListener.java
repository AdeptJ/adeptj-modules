package com.adeptj.modularweb.common.servlet.async;

import java.io.IOException;

import javax.servlet.ReadListener;

public class AsyncReadListener implements ReadListener {
	
	@Override
	public void onDataAvailable() throws IOException {
	}

	@Override
	public void onAllDataRead() throws IOException {

	}

	@Override
	public void onError(Throwable th) {
	}

}
