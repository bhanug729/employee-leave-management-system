package com.bhanu.employee.exception;

public class InsufficientLeaveBalanceException extends RuntimeException{
    public InsufficientLeaveBalanceException(String msg){
        super(msg);
    }
}
