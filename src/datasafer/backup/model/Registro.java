package datasafer.backup.model;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
public class Registro {

	public Registro() {}

	public Registro(Usuario solicitante, Timestamp data, String atributo, String de, String para) {
		this.solicitante = solicitante;
		this.data = data;
		this.atributo = atributo;
		this.de = de;
		this.para = para;
	}

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "solicitante_id")
	private Usuario solicitante = null;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = false)
	private Timestamp data = null;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = false)
	private String atributo = null;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(length = 1023, nullable = true)
	private String de = null;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(length = 1023, nullable = false)
	private String para = null;

	@JsonProperty("login_solicitante")
	@Transient
	private String loginSolicitante = null;

	public String getLoginSolicitante() {
		return loginSolicitante;
	}

	public void setLoginSolicitante(String loginSolicitante) {
		this.loginSolicitante = loginSolicitante;
	}

	public String getAtributo() {
		return atributo;
	}

	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}

	public String getDe() {
		return de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getData() {
		return data;
	}

	public void setData(Timestamp data) {
		this.data = data;
	}

	public Usuario getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Usuario solicitante) {
		this.solicitante = solicitante;
	}

}
