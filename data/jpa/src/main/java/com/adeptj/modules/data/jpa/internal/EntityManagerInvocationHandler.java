package com.adeptj.modules.data.jpa.internal;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class EntityManagerInvocationHandler implements InvocationHandler {

    private final EntityManager entityManager;

    EntityManagerInvocationHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(this.entityManager, args);
    }
}
