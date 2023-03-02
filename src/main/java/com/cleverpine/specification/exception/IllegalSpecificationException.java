package com.cleverpine.specification.exception;

/**
 * Thrown to indicate an illegal specification encounter error.
 */
public class IllegalSpecificationException extends RuntimeException {

    /**
     * Constructs a {@link  IllegalSpecificationException} with the specified
     * detail message.
     *
     * @param message the detail message.
     */
    public IllegalSpecificationException(String message) {
        super(message);
    }

}
