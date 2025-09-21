package com.ritmofit.app.data.dto;

public abstract class ApiResult<T> {
    
    public static class Loading<T> extends ApiResult<T> {
        @Override
        public String toString() {
            return "Loading";
        }
    }
    
    public static class Success<T> extends ApiResult<T> {
        private final T data;
        
        public Success(T data) {
            this.data = data;
        }
        
        public T getData() {
            return data;
        }
        
        @Override
        public String toString() {
            return "Success{" +
                    "data=" + data +
                    '}';
        }
    }
    
    public static class Error<T> extends ApiResult<T> {
        private final String message;
        private final Throwable throwable;
        
        public Error(String message) {
            this.message = message;
            this.throwable = null;
        }
        
        public Error(String message, Throwable throwable) {
            this.message = message;
            this.throwable = throwable;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Throwable getThrowable() {
            return throwable;
        }
        
        @Override
        public String toString() {
            return "Error{" +
                    "message='" + message + '\'' +
                    ", throwable=" + throwable +
                    '}';
        }
    }
}
