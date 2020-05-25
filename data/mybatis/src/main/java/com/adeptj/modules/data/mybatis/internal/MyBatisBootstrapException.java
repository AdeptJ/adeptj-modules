package com.adeptj.modules.data.mybatis.internal;

class MyBatisBootstrapException extends RuntimeException {

    private static final long serialVersionUID = 6412897480542683420L;

    MyBatisBootstrapException(String message) {
        super(message);
    }

    MyBatisBootstrapException(Throwable cause) {
        super(cause);
    }
}
