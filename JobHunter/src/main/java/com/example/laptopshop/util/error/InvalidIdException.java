package com.example.laptopshop.util.error;

public class InvalidIdException extends Exception {
    public InvalidIdException() {
    }

    public InvalidIdException(String message) {
        super(message);
    }
}
