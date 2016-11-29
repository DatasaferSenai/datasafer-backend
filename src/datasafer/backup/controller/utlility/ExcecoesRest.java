package datasafer.backup.controller.utlility;

import javax.validation.ConstraintViolationException;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@ControllerAdvice
public class ExcecoesRest extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(	final HttpRequestMethodNotSupportedException ex,
																			final HttpHeaders headers,
																			final HttpStatus status,
																			final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject().put("erro", "O método " + ex.getMethod() + " não suportado").toString();
		} catch (JSONException e) {
			response = null;
		}

		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(response, headers, HttpStatus.METHOD_NOT_ALLOWED);
	}
 
	@ExceptionHandler(value = { ConstraintViolationException.class })
	protected ResponseEntity<Object> responseErro(	final ConstraintViolationException ex,
													final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject()	.put("erro",
											((ConstraintViolationImpl<?>) ex.getConstraintViolations().toArray()[0]).getMessage())
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

	@ExceptionHandler(value = { UnrecognizedPropertyException.class })
	protected ResponseEntity<Object> responseErro(	final UnrecognizedPropertyException ex,
													final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject()	.put("erro",
											"O atributo " + ex.getPropertyName() + " não existe ou não é modificável")
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

	@ExceptionHandler(value = { JsonParseException.class, JsonMappingException.class })
	protected ResponseEntity<Object> respondeErroJson(	final Exception ex,
														final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject()	.put("erro",
											"Json inválido")
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

	@ExceptionHandler(value = { NoSuchFieldException.class })
	protected ResponseEntity<Object> respondeErroJson(	final NoSuchFieldException ex,
														final WebRequest request) {

		String response = null;
		try {
			response = new JSONObject()	.put("erro",
											"A coleção solicitada não existe ou não está disponível")
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

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<Object> responseErro(	final Exception ex,
													final WebRequest request) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		ex.printStackTrace();

		return handleExceptionInternal(	ex,
										null,
										headers,
										HttpStatus.INTERNAL_SERVER_ERROR,
										request);
	}
}
