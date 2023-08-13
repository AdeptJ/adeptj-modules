package com.adeptj.modules.data.mybatis.internal;

import java.io.Serial;

class MyBatisBootstrapException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6412897480542683420L;

    public MyBatisBootstrapException(Throwable cause) {
        super(cause);
    }
}
