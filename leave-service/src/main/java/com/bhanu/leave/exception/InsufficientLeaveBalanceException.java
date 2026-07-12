package com.bhanu.leave.exception;

public class InsufficientLeaveBalanceException extends RuntimeException{
    public InsufficientLeaveBalanceException(String msg){
        super(msg);
    }
}
