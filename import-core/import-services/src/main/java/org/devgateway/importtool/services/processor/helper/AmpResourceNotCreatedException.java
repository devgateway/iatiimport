package org.devgateway.importtool.services.processor.helper;

public class AmpResourceNotCreatedException extends Exception {

    public AmpResourceNotCreatedException(String message) {
        super(message);
    }

    public AmpResourceNotCreatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
