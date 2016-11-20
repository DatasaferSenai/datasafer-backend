package datasafer.backup.model;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Notificacao {

	public enum Tipo {
		DISPOSITIVO_IOS,
		DISPOSITIVO_ANDROID,
		APLICACAO_WEB
	}

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@JsonProperty
	@Id
	@Column(length = 255, nullable = false)
	private String token;

	@JsonProperty
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

	@JsonProperty("login_usuario")
	public String getLoginUsuario() {
		return usuario.getLogin();
	}

	@JsonIgnore
	@Column(nullable = true)
	private Timestamp ultimaNotificacao;

	public Timestamp getUltimaNotificacao() {
		return ultimaNotificacao;
	}

	public void setUltimaNotificacao(Timestamp ultimaNotificacao) {
		this.ultimaNotificacao = ultimaNotificacao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

}
