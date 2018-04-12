package com.adeptj.modules.data.jpa.internal;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EntityManagerInvocationHandler implements InvocationHandler {

    private final EntityManager entityManager;

    EntityManagerInvocationHandler(EntityManager entityManager, ScheduledExecutorService executorService) {
        this.entityManager = entityManager;
        executorService.schedule(this.entityManager::close, 60, TimeUnit.SECONDS);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(this.entityManager, args);
    }
}
