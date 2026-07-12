package com.bhanu.leave.exception;

public class InvalidLeaveStateException extends RuntimeException{
    public InvalidLeaveStateException(String msg){
        super(msg);
    }
}
