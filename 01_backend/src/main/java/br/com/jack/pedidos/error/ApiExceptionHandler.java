package br.com.jack.pedidos.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(NotFoundException exception) { return response(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage(), new LinkedHashMap<>()); }
    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class})
    ResponseEntity<ApiErrorResponse> invalid(RuntimeException exception) { return response(HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE", exception.getMessage(), new LinkedHashMap<>()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException exception) {
        var fields = new LinkedHashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Revise os campos informados.", fields);
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception exception) { return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Não foi possível concluir sua solicitação.", new LinkedHashMap<>()); }
    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String code, String message, LinkedHashMap<String, String> fields) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(code, message, fields, LocalDateTime.now()));
    }
}
