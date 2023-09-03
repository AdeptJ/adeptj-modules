package com.adeptj.modules.commons.email;

import java.io.Serial;

public class EmailException extends RuntimeException {

	@Serial
    private static final long serialVersionUID = -3117803548267393781L;

	public EmailException(Throwable cause) {
        super(cause);
    }
}
