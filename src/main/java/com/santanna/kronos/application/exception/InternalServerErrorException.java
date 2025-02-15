package com.santanna.kronos.application.exception;

public class InternalServerErrorException extends RuntimeException {

        public InternalServerErrorException(String message) {
            super(message);
        }
}
