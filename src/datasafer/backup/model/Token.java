package datasafer.backup.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "usuario" })
@Entity
public class Token {
	
	@Id
	private String token;
	
	@OneToOne
	private Usuario usuario;

	public String getToken() {
		return token;
	}

	public void setToken(String chave) {
		this.token = chave;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
