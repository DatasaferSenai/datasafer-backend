package datasafer.backup.controller.utlility;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { DataIntegrityViolationException.class })
	protected ResponseEntity<Object> handleConflict(RuntimeException ex,
													WebRequest request) {

		String response = null;
		try {
			response = new JSONObject().put("erro", ex.getMessage()).toString();
		} catch (JSONException e) {
			response = null;
		}

		return handleExceptionInternal(	ex, response,
										new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(value = { AccessDeniedException.class })
	protected ResponseEntity<Object> handleBadRequest(	RuntimeException ex,
														WebRequest request) {

		String response = null;
		try {
			response = new JSONObject().put("erro", ex.getMessage()).toString();
		} catch (JSONException e) {
			response = null;
		}

		return handleExceptionInternal(	ex, response,
										new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

}
