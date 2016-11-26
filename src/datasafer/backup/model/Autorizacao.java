package datasafer.backup.model;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
public class Autorizacao {

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario = null;

	@JsonProperty(access = Access.READ_ONLY)
	@Id
	@Column(length = 255, nullable = false)
	private String token = null;

	@JsonIgnore
	@Column(length = 64, nullable = false)
	private String ip = null;

	@JsonIgnore
	@Column(nullable = false)
	private Timestamp emissao = null;

	@JsonIgnore
	@Column(nullable = true)
	private Timestamp ultimoAcesso = null;

	public Timestamp getUltimoAcesso() {
		return ultimoAcesso;
	}

	public void setUltimoAcesso(Timestamp ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Timestamp getEmissao() {
		return emissao;
	}

	public void setEmissao(Timestamp emissao) {
		this.emissao = emissao;
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

}
