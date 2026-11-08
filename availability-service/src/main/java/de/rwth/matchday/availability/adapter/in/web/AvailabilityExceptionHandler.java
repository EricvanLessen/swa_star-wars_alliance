package de.rwth.matchday.availability.adapter.in.web;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class AvailabilityExceptionHandler {
  public record ErrorResponse(String error) {}
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse invalid(IllegalArgumentException ex) { return new ErrorResponse(ex.getMessage()); }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse invalidJson(HttpMessageNotReadableException ex) { return new ErrorResponse("Invalid JSON"); }
}

