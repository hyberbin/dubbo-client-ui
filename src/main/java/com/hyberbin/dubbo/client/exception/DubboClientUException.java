package com.hyberbin.dubbo.client.exception;

public class DubboClientUException extends RuntimeException {

  public DubboClientUException() {
  }

  public DubboClientUException(String message) {
    super(message);
  }

  public DubboClientUException(String message, Throwable cause) {
    super(message, cause);
  }

  public DubboClientUException(Throwable cause) {
    super(cause);
  }

  public DubboClientUException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
