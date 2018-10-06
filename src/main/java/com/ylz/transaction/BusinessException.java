package com.ylz.transaction;

public class BusinessException extends IllegalArgumentException {
    public BusinessException(String s) {
        super(s);
    }
}
