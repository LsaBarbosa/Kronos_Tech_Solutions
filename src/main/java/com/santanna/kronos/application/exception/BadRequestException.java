package com.santanna.kronos.application.exception;

public class BadRequestException extends RuntimeException {

        public BadRequestException(String message) {
            super(message);
        }
}
