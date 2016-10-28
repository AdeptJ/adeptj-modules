package com.adeptj.modularweb.common.servlet.async;

import java.io.IOException;

import javax.servlet.WriteListener;

public class AsyncWriteListener implements WriteListener {

	@Override
	public void onWritePossible() throws IOException {
	}

	@Override
	public void onError(Throwable th) {
	}

}
