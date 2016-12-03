package datasafer.backup.controller.utlility;

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

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@ControllerAdvice
public class ExcecoesRest extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
																			final HttpRequestMethodNotSupportedException ex,
																			final HttpHeaders headers,
																			final HttpStatus status,
																			final WebRequest request) {

		String responde = null;
		try {
			responde = new JSONObject().put("erro", "Método " + ex.getMethod() + " não suportado").toString();
		} catch (JSONException e) {
			responde = null;
		}

		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<>(responde,
									headers,
									HttpStatus.METHOD_NOT_ALLOWED);
	}

	// @ExceptionHandler(value = { ConstraintViolationException.class })
	// protected ResponseEntity<Object> respondeErro( final
	// ConstraintViolationException ex,
	// final WebRequest request) {
	//
	// String responde = null;
	// try {
	// responde = new JSONObject() .put("erro",
	// ((ConstraintViolationImpl<?>)
	// ex.getConstraintViolations().toArray()[0]).getMessage())
	// .toString();
	// } catch (JSONException e) {
	// responde = null;
	// }
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	//
	// return this.handleExceptionInternal(ex,
	// responde,
	// headers,
	// HttpStatus.BAD_REQUEST,
	// request);
	// }
	//
	@ExceptionHandler(value = { UnrecognizedPropertyException.class })
	protected ResponseEntity<Object> respondeErro(	final UnrecognizedPropertyException ex,
													final WebRequest request) {

		String responde = null;
		try {
			responde = new JSONObject()	.put("erro", "O atributo " + ex.getPropertyName() + " não existe ou não é modificável")
										.toString();
		} catch (JSONException e) {
			responde = null;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return this.handleExceptionInternal(ex,
											responde,
											headers,
											HttpStatus.BAD_REQUEST,
											request);
	}
	//
	// @ExceptionHandler(value = { DataIntegrityViolationException.class })
	// protected ResponseEntity<Object> respondeErro( final
	// DataIntegrityViolationException ex,
	// final WebRequest request) {
	//
	// String responde = null;
	// try {
	// responde = new JSONObject() .put("erro",
	// ex.getMessage())
	// .toString();
	// } catch (JSONException e) {
	// responde = null;
	// }
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	//
	// return this.handleExceptionInternal(ex,
	// responde,
	// headers,
	// HttpStatus.CONFLICT,
	// request);
	// }
	//
	// @ExceptionHandler(value = { JsonParseException.class,
	// JsonMappingException.class })
	// protected ResponseEntity<Object> respondeErroJson( final Exception ex,
	// final WebRequest request) {
	//
	// String responde = null;
	// try {
	// responde = new JSONObject() .put("erro",
	// "Json inválido")
	// .toString();
	// } catch (JSONException e) {
	// responde = null;
	// }
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	//
	// return this.handleExceptionInternal(ex,
	// responde,
	// headers,
	// HttpStatus.BAD_REQUEST,
	// request);
	// }

	// @ExceptionHandler(value = { NoSuchFieldException.class })
	// protected ResponseEntity<Object> respondeErroJson( final
	// NoSuchFieldException ex,
	// final WebRequest request) {
	//
	// String responde = null;
	// try {
	// responde = new JSONObject() .put("erro",
	// "A coleção solicitada não existe ou não está disponível")
	// .toString();
	// } catch (JSONException e) {
	// responde = null;
	// }
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	//
	// return this.handleExceptionInternal(ex,
	// responde,
	// headers,
	// HttpStatus.BAD_REQUEST,
	// request);
	// }

	// @ExceptionHandler(value = { AccessDeniedException.class })
	// protected ResponseEntity<Object> respondeAcessoNegado( final
	// AccessDeniedException ex,
	// final WebRequest request) {
	//
	// String responde = null;
	// try {
	// responde = new JSONObject() .put("erro",
	// ex.getMessage())
	// .toString();
	// } catch (JSONException e) {
	// responde = null;
	// }
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	//
	// return this.handleExceptionInternal(ex,
	// responde,
	// headers,
	// HttpStatus.FORBIDDEN,
	// request);
	// }
	//
	// @ExceptionHandler(value = { RuntimeException.class })
	// protected ResponseEntity<Object> respondeErro( final RuntimeException ex,
	// final WebRequest request) {
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	//
	// ex.printStackTrace();
	//
	// return this.handleExceptionInternal(ex,
	// null,
	// headers,
	// HttpStatus.INTERNAL_SERVER_ERROR,
	// request);
	// }
}
