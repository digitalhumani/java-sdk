package com.digitalhumani.models;

import com.digitalhumani.exceptions.RaaSException;

public abstract class RaaSResult {
    private RaaSException exception = null;

    public Boolean isSuccess() {
        return this.exception == null;
    }

    public RaaSException getException() {
        return this.exception;
    }

    protected RaaSResult() { }

    protected RaaSResult(RaaSException exception) {
        this.exception = exception;
    }
}
