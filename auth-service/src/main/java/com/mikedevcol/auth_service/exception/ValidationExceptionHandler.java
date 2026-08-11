package com.mikedevcol.auth_service.exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.yaml.snakeyaml.Yaml;

@RestControllerAdvice
public class ValidationExceptionHandler {

  private final Map<String, Map<String, String>> validationMessages;

  public ValidationExceptionHandler() {
    this(new ClassPathResource("validation-messages.yml"));
  }

  public ValidationExceptionHandler(Resource validationMessagesResource) {
    this.validationMessages = validationMessagesResource != null ? loadValidationMessages(validationMessagesResource)
        : Collections.emptyMap();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
    Map<String, List<String>> errors = new LinkedHashMap<>();

    for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
      String field = fieldError.getField();
      String resolvedMessage = resolveMessage(field, fieldError.getCode(), fieldError.getDefaultMessage());
      errors.computeIfAbsent(field, ignored -> new ArrayList<>()).add(resolvedMessage);
    }

    return ResponseEntity.badRequest()
        .body(new ValidationErrorResponse("Validation errors", errors));
  }

  private String resolveMessage(String field, String code, String defaultMessage) {
    Map<String, String> messagesByCode = validationMessages.getOrDefault(field, Collections.emptyMap());
    return messagesByCode.getOrDefault(code, defaultMessage != null ? defaultMessage : "Valor inválido");
  }

  private Map<String, Map<String, String>> loadValidationMessages(Resource resource) {
    try (InputStream inputStream = resource.getInputStream()) {
      Yaml yaml = new Yaml();
      Object root = yaml.load(inputStream);

      Map<?, ?> rootMap = castToMap(root);
      Map<?, ?> validationMap = castToMap(rootMap.get("validation"));
      Map<?, ?> messagesMap = castToMap(validationMap.get("messages"));

      return parseMessagesMap(messagesMap);
    } catch (IOException exception) {
      throw new IllegalStateException("No se pudieron cargar los mensajes de validación", exception);
    }
  }

  private Map<?, ?> castToMap(Object obj) {
    return obj instanceof Map<?, ?> map ? map : Collections.emptyMap();
  }

  private Map<String, Map<String, String>> parseMessagesMap(Map<?, ?> messagesMap) {
    Map<String, Map<String, String>> resolvedMessages = new LinkedHashMap<>();
    messagesMap.forEach((field, fieldMessagesNode) -> {
      Map<?, ?> fieldMessages = castToMap(fieldMessagesNode);
      Map<String, String> resolvedFieldMessages = new LinkedHashMap<>();
      fieldMessages
          .forEach((code, message) -> resolvedFieldMessages.put(String.valueOf(code), String.valueOf(message)));
      resolvedMessages.put(String.valueOf(field), resolvedFieldMessages);
    });
    return resolvedMessages;
  }

  public record ValidationErrorResponse(String message, Map<String, List<String>> errors) {
  }
}
