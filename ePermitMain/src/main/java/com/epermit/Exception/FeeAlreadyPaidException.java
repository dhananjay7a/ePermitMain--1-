package com.epermit.Exception;

public class FeeAlreadyPaidException extends RuntimeException {
    public FeeAlreadyPaidException(String msg) {
        super(msg);
    }
}
