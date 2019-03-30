package com.adeptj.modules.security.core.identitystore;

import java.util.HashMap;
import java.util.Map;

import static com.adeptj.modules.security.core.identitystore.CredentialValidationOutcome.Outcome.INVALID;
import static com.adeptj.modules.security.core.identitystore.CredentialValidationOutcome.Outcome.NOT_VALIDATED;

public class CredentialValidationOutcome {

    public static final CredentialValidationOutcome INVALID_OUTCOME = new CredentialValidationOutcome(INVALID);

    public static final CredentialValidationOutcome NOT_VALIDATED_OUTCOME = new CredentialValidationOutcome(NOT_VALIDATED);

    private Map<String, Object> data = new HashMap<>();

    private Outcome outcome;

    private String identityStoreName;

    public enum Outcome {

        /**
         * Indicates that the credential could not be validated
         */
        NOT_VALIDATED,

        /**
         * Indicates that the credential is not valid after a validation attempt.
         */
        INVALID,

        /**
         * Indicates that the credential is valid after a validation attempt.
         */
        VALID
    }

    public CredentialValidationOutcome(Outcome outcome) {
        this(outcome, null);
    }

    public CredentialValidationOutcome(Outcome outcome, String identityStoreName) {
        this.outcome = outcome;
        this.identityStoreName = identityStoreName;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public CredentialValidationOutcome withData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Outcome getOutcome() {
        return this.outcome;
    }

    public String getIdentityStoreId() {
        return this.identityStoreName;
    }
}
