package datasafer.backup.controller.utlility;

import javax.validation.ConstraintViolationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExcecoesRest extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(	final HttpRequestMethodNotSupportedException ex,
																			final HttpHeaders headers,
																			final HttpStatus status,
																			final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject().put("erro", "Método " + ex.getMethod() + " não suportado").toString();
		} catch (JSONException e) {
			response = null;
		}

		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(response, headers, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(value = { DataIntegrityViolationException.class })
	protected ResponseEntity<Object> respondeConflito(	final RuntimeException ex,
														final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject().put("erro", ex.getMessage()).toString();
		} catch (JSONException e) {
			response = null;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return handleExceptionInternal(	ex,
										response,
										headers,
										HttpStatus.CONFLICT,
										request);
	}

	@ExceptionHandler(value = { AccessDeniedException.class })
	protected ResponseEntity<Object> respondeProibido(	final RuntimeException ex,
														final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject().put("erro", ex.getMessage()).toString();
		} catch (JSONException e) {
			response = null;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return handleExceptionInternal(	ex,
										response,
										headers,
										HttpStatus.FORBIDDEN,
										request);
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	protected ResponseEntity<Object> responseErroValidacao(	final ConstraintViolationException ex,
															final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject()	.put("erro",
											ex)
										.toString();
		} catch (JSONException e) {
			response = null;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return handleExceptionInternal(	ex,
										response,
										headers,
										HttpStatus.BAD_REQUEST,
										request);
	}

}
