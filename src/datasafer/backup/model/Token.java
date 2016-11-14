package datasafer.backup.model;

import java.util.Date;

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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "token", "emissao", "expiracao" })
@Entity
public class Token {

	public Token() {
		this.usuario = null;
		this.token = null;
		this.emissao = null;
		this.expiracao = null;
	}

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@JsonProperty(access = Access.READ_ONLY)
	@Id
	@Column(length = 255, nullable = false)
	private String token;

	@JsonIgnore
	@Column(nullable = false)
	private Date emissao;

	@JsonIgnore
	@Column(nullable = true)
	private Date expiracao;

	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	public Date getExpiracao() {
		return expiracao;
	}

	public void setExpiracao(Date expiracao) {
		this.expiracao = expiracao;
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
