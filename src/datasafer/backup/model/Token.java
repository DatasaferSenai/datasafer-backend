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
		this.chave = null;
		this.emissao = null;
		this.expiracao = null;
	}

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@JsonProperty(value = "token", access = Access.READ_ONLY)
	@Id
	@Column(length = 255, nullable = false)
	private String chave;

	@JsonIgnore
	@Column(length = 64, nullable = false)
	private String ip;

	@JsonIgnore
	@Column(nullable = false)
	private Date emissao;

	@JsonIgnore
	@Column(nullable = true)
	private Date expiracao;

	@JsonIgnore
	@Column(nullable = true)
	private Date ultimaUtilizacao;

	public Date getUltimaUtilizacao() {
		return ultimaUtilizacao;
	}

	public void setUltimaUtilizacao(Date ultimaUtilizacao) {
		this.ultimaUtilizacao = ultimaUtilizacao;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

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

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

}
