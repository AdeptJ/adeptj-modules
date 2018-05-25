package com.adeptj.modules.commons.utils.service;

import java.util.concurrent.ExecutorService;

public interface ExecutorServiceProvider {

    ExecutorService getExecutorService(String name);
}
