package org.devgateway.importtool.rest.error;

import org.devgateway.importtool.exceptions.MissingPrerequisitesException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = RestController.class)
//TODO now the error will be returned as a key, on scope of IATIIMPORT-277 we will refasctor the tool
//TODO so we can return the lang pack with all translations, server side.
public class GlobalExceptionHandler {

    private final MediaType jsonMediaType = MediaType.parseMediaType("application/json");

    @ExceptionHandler(MissingPrerequisitesException.class)
    protected ResponseEntity<Object> handleServerMissingConfiguration(
            MissingPrerequisitesException ex) {
        String error = ex.getMessage();

        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
    }
    //TODO we need to do a whole refactoring to do proper exception handling. I'm adding this
    //TODO global so all exceptions are wrapped in APIERROR json and its safe to parse the value
    //TODO from the javascript client.
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleGenericRunTimeException(
            RuntimeException ex) {
        String error = ex.getMessage();
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(this.jsonMediaType);
        return new ResponseEntity<>(apiError, httpHeaders,apiError.getStatus());
    }
}