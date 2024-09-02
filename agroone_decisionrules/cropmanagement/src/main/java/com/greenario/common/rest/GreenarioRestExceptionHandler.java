package com.greenario.common.rest;

import static java.util.function.Predicate.not;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.greenario.common.api.exceptions.AutocommitFailedException;
import com.greenario.common.api.exceptions.ConcurrencyException;
import com.greenario.common.api.exceptions.ConnectionNotSetException;
import com.greenario.common.api.exceptions.DomainNotFoundException;
import com.greenario.common.api.exceptions.ErrorCode;
import com.greenario.common.api.exceptions.GreenarioException;
import com.greenario.common.api.exceptions.InvalidArgumentException;
import com.greenario.common.api.exceptions.NotAuthorizedException;
import com.greenario.common.api.exceptions.SystemException;
import com.greenario.common.api.exceptions.GreenarioRuntimeException;
import com.greenario.common.api.exceptions.UnsupportedDatabaseException;
import com.greenario.common.api.exceptions.WrongCustomHolidayFormatException;
import com.greenario.common.rest.models.ExceptionRepresentationModel;
import com.greenario.plot.api.exceptions.PlotAlreadyExistException;
import com.greenario.plot.api.exceptions.PlotInUseException;
import com.greenario.plot.api.exceptions.PlotNotFoundException;
import com.greenario.plot.api.exceptions.MalformedServiceLevelException;
import com.greenario.project.api.exceptions.ProjectDetailPersistenceException;
import com.greenario.project.api.exceptions.InvalidCallbackStateException;
import com.greenario.project.api.exceptions.InvalidOwnerException;
import com.greenario.project.api.exceptions.InvalidProjectStateException;
import com.greenario.project.api.exceptions.NotAuthorizedOnProjectPreviewException;
import com.greenario.project.api.exceptions.ProjectAlreadyExistException;
import com.greenario.project.api.exceptions.ProjectPreviewNotFoundException;
import com.greenario.project.api.exceptions.ProjectNotFoundException;
import com.greenario.projectline.api.exceptions.NotAuthorizedOnProjectLineException;
import com.greenario.projectline.api.exceptions.ProjectLineAccessItemAlreadyExistException;
import com.greenario.projectline.api.exceptions.ProjectLineAlreadyExistException;
import com.greenario.projectline.api.exceptions.ProjectLineInUseException;
import com.greenario.projectline.api.exceptions.ProjectLineMarkedForDeletionException;
import com.greenario.projectline.api.exceptions.ProjectLineNotFoundException;
import com.greenario.spi.history.api.exceptions.GreenarioHistoryEventNotFoundException;
import com.greenario.user.api.exceptions.UserAlreadyExistException;
import com.greenario.user.api.exceptions.UserNotFoundException;

/** This class handles GREENARIO exceptions. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GreenarioRestExceptionHandler extends ResponseEntityExceptionHandler {

  public static final String ERROR_KEY_QUERY_MALFORMED = "QUERY_PARAMETER_MALFORMED";
  public static final String ERROR_KEY_PAYLOAD = "PAYLOAD_TOO_LARGE";
  public static final String ERROR_KEY_UNKNOWN_ERROR = "UNKNOWN_ERROR";
  private static final Map<String, HttpStatus> HTTP_STATUS_BY_ERROR_CODE_KEY = Map.ofEntries(
      Map.entry(MalformedServiceLevelException.ERROR_KEY, HttpStatus.BAD_REQUEST),
      Map.entry(WrongCustomHolidayFormatException.ERROR_KEY, HttpStatus.BAD_REQUEST),
      Map.entry(DomainNotFoundException.ERROR_KEY, HttpStatus.BAD_REQUEST),
      Map.entry(InvalidArgumentException.ERROR_KEY, HttpStatus.BAD_REQUEST),
      Map.entry(ERROR_KEY_QUERY_MALFORMED, HttpStatus.BAD_REQUEST),
      Map.entry(InvalidCallbackStateException.ERROR_KEY, HttpStatus.BAD_REQUEST),
      Map.entry(InvalidOwnerException.ERROR_KEY, HttpStatus.BAD_REQUEST),
      Map.entry(InvalidProjectStateException.ERROR_KEY, HttpStatus.BAD_REQUEST),
      //
      Map.entry(NotAuthorizedException.ERROR_KEY, HttpStatus.FORBIDDEN),
      Map.entry(NotAuthorizedOnProjectPreviewException.ERROR_KEY, HttpStatus.FORBIDDEN),
      Map.entry(NotAuthorizedOnProjectLineException.ERROR_KEY_ID, HttpStatus.FORBIDDEN),
      Map.entry(NotAuthorizedOnProjectLineException.ERROR_KEY_KEY_DOMAIN, HttpStatus.FORBIDDEN),
      //
      Map.entry(PlotNotFoundException.ERROR_KEY_ID, HttpStatus.NOT_FOUND),
      Map.entry(PlotNotFoundException.ERROR_KEY_KEY_DOMAIN, HttpStatus.NOT_FOUND),
      Map.entry(ProjectPreviewNotFoundException.ERROR_KEY, HttpStatus.NOT_FOUND),
      Map.entry(ProjectNotFoundException.ERROR_KEY, HttpStatus.NOT_FOUND),
      Map.entry(UserNotFoundException.ERROR_KEY, HttpStatus.NOT_FOUND),
      Map.entry(ProjectLineNotFoundException.ERROR_KEY_ID, HttpStatus.NOT_FOUND),
      Map.entry(ProjectLineNotFoundException.ERROR_KEY_KEY_DOMAIN, HttpStatus.NOT_FOUND),
      Map.entry(GreenarioHistoryEventNotFoundException.ERROR_KEY, HttpStatus.NOT_FOUND),
      //
      Map.entry(ProjectDetailPersistenceException.ERROR_KEY, HttpStatus.CONFLICT),
      Map.entry(PlotAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
      Map.entry(ConcurrencyException.ERROR_KEY, HttpStatus.CONFLICT),
      Map.entry(ProjectAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
      Map.entry(UserAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
      Map.entry(ProjectLineAccessItemAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
      Map.entry(ProjectLineAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
      Map.entry(ProjectLineMarkedForDeletionException.ERROR_KEY, HttpStatus.CONFLICT),
      //
      Map.entry(ERROR_KEY_PAYLOAD, HttpStatus.PAYLOAD_TOO_LARGE),
      //
      Map.entry(PlotInUseException.ERROR_KEY, HttpStatus.LOCKED),
      Map.entry(ProjectLineInUseException.ERROR_KEY, HttpStatus.LOCKED),
      //
      Map.entry(AutocommitFailedException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR),
      Map.entry(ConnectionNotSetException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR),
      Map.entry(SystemException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR),
      Map.entry(UnsupportedDatabaseException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR),
      Map.entry(ERROR_KEY_UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex, WebRequest req) {
    HttpStatus status = HTTP_STATUS_BY_ERROR_CODE_KEY.getOrDefault(
        ERROR_KEY_PAYLOAD, HttpStatus.INTERNAL_SERVER_ERROR);
    return buildResponse(ErrorCode.of(ERROR_KEY_PAYLOAD), ex, req, status);
  }

  @ExceptionHandler(BeanInstantiationException.class)
  protected ResponseEntity<Object> handleBeanInstantiationException(
      BeanInstantiationException ex, WebRequest req) {
    if (ex.getCause() instanceof InvalidArgumentException) {
      InvalidArgumentException cause = (InvalidArgumentException) ex.getCause();
      return handleGreenarioRuntimeException(cause, req);
    }
    return buildResponse(null, ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // This ExceptionHandler exists to convert IllegalArgumentExceptions to
  // InvalidArgumentExceptions.
  // Once IllegalArgumentExceptions are no longer in use, you can delete this
  // \(*_*)/
  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest req) {
    HttpStatus status = HTTP_STATUS_BY_ERROR_CODE_KEY.getOrDefault(
        InvalidArgumentException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR);
    return buildResponse(ErrorCode.of(InvalidArgumentException.ERROR_KEY), ex, req, status);
  }

  @ExceptionHandler(GreenarioRuntimeException.class)
  protected ResponseEntity<Object> handleGreenarioRuntimeException(
      GreenarioRuntimeException ex, WebRequest req) {
    HttpStatus status = HTTP_STATUS_BY_ERROR_CODE_KEY.getOrDefault(
        ex.getErrorCode().getKey(), HttpStatus.INTERNAL_SERVER_ERROR);
    return buildResponse(ex.getErrorCode(), ex, req, status);
  }

  @ExceptionHandler(GreenarioException.class)
  protected ResponseEntity<Object> handleGreenarioException(GreenarioException ex, WebRequest req) {
    HttpStatus status = HTTP_STATUS_BY_ERROR_CODE_KEY.getOrDefault(
        ex.getErrorCode().getKey(), HttpStatus.INTERNAL_SERVER_ERROR);
    return buildResponse(ex.getErrorCode(), ex, req, status);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest req) {
    return buildResponse(
        ErrorCode.of(ERROR_KEY_UNKNOWN_ERROR), ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  @NonNull
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {

    MalformedQueryParameter[] wrongQueryParameters = ex.getBindingResult().getFieldErrors().stream()
        .map(this::extractMalformedQueryParameters)
        .flatMap(Collection::stream)
        .toArray(MalformedQueryParameter[]::new);

    // if we have no wrong query parameter then this BindException is representing
    // something else.
    // Therefore, we only create an ErrorCode when we have found a wrong query
    // parameter.
    ErrorCode errorCode = wrongQueryParameters.length != 0
        ? ErrorCode.of(
            ERROR_KEY_QUERY_MALFORMED, Map.of("malformedQueryParameters", wrongQueryParameters))
        : null;

    return buildResponse(errorCode, ex, request, HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<Object> buildResponse(
      ErrorCode errorCode, Throwable ex, WebRequest req, HttpStatus status) {
    ExceptionRepresentationModel errorData = new ExceptionRepresentationModel(errorCode, status, ex, req);
    logger.error(
        String.format("Error occurred during processing of rest request: %s", errorData), ex);
    return ResponseEntity.status(status).body(errorData);
  }

  private List<MalformedQueryParameter> extractMalformedQueryParameters(FieldError fieldError) {
    if (fieldError.contains(TypeMismatchException.class)) {
      TypeMismatchException typeMismatchException = fieldError.unwrap(TypeMismatchException.class);
      if (typeMismatchException.getCause() instanceof ConversionFailedException) {
        ConversionFailedException conversionFailedException = (ConversionFailedException) typeMismatchException
            .getCause();
        Class<?> targetType = conversionFailedException.getTargetType().getType();
        if (targetType.isEnum()) {
          String queryParameter = fieldError.getField();
          // the redundancy below exists because we want to keep the enums sorted by their
          // ordinal
          // value for the error output and want to use the contains performance boost of
          // a HashSet.
          List<String> enumConstants = Arrays.stream(targetType.getEnumConstants())
              .map(Enum.class::cast)
              .map(Enum::name)
              .collect(Collectors.toList());
          Set<String> enumConstantSet = new HashSet<>(enumConstants);

          return getRejectedValues(typeMismatchException)
              .filter(not(enumConstantSet::contains))
              .map(value -> new MalformedQueryParameter(queryParameter, value, enumConstants))
              .collect(Collectors.toList());
        }
      }
    }

    return Collections.emptyList();
  }

  private Stream<String> getRejectedValues(TypeMismatchException ex) {
    Object value = ex.getValue();
    if (value != null && value.getClass().isArray()) {
      return Arrays.stream((Object[]) value).map(Objects::toString);
    }
    if (value != null && value.getClass().isAssignableFrom(Collection.class)) {
      return ((Collection<?>) value).stream().map(Objects::toString);
    }
    return Stream.of(value).map(Objects::toString);
  }

  public static class MalformedQueryParameter implements Serializable {
    private final String queryParameter;
    private final String actualValue;
    private final Collection<String> expectedValues;

    MalformedQueryParameter(
        String queryParameter, String actualValue, Collection<String> expectedValues) {
      this.queryParameter = queryParameter;
      this.actualValue = actualValue;
      this.expectedValues = expectedValues;
    }

    @SuppressWarnings("unused")
    public String getActualValue() {
      return actualValue;
    }

    @SuppressWarnings("unused")
    public Collection<String> getExpectedValues() {
      return expectedValues;
    }

    @SuppressWarnings("unused")
    public String getQueryParameter() {
      return queryParameter;
    }
  }
}
