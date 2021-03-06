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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import datasafer.backup.dao.utility.annotations.Indireto;

@Entity
public class Notificacao {

	public Notificacao() {}

	public Notificacao(Usuario usuario, String token, Tipo tipo) {
		this.usuario = usuario;
		this.token = token;
		this.tipo = tipo;
	}

	public enum Tipo {
		DISPOSITIVO_IOS,
		DISPOSITIVO_ANDROID,
		APLICACAO_WEB
	}

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario = null;

	@JsonProperty
	@Id
	@Column(length = 255, nullable = false)
	private String token = null;

	@JsonProperty
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Tipo tipo = null;

	@JsonIgnore
	@Column(nullable = true)
	private Timestamp ultimaNotificacao = null;

	@JsonProperty("login_usuario")
	@Transient
	@Indireto(atributo = "usuario", identificador = "login")
	private String loginUsuario = null;

	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}

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
