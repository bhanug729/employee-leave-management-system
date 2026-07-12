package com.bhanu.leave.exception;

public class LeaveNotFoundException extends RuntimeException{
    public LeaveNotFoundException(String msg){
        super(msg);
    }
}
