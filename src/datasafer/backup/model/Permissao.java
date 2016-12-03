package datasafer.backup.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import datasafer.backup.dao.utility.annotations.Identificador;
import datasafer.backup.dao.utility.annotations.Indireto;

@Entity
public class Permissao {

	public Permissao() {};

	public Permissao(Usuario atribuidor, Usuario recebedor, String atributo, Tipo tipo, boolean permitido) {
		this.atribuidor = atribuidor;
		this.recebedor = recebedor;
		this.atributo = atributo;
		this.tipo = tipo;
		this.permitido = permitido;
	}

	public enum Tipo {
		VISUALIZAR,
		EDITAR,
		INSERIR,
		REMOVER
	};

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@Identificador
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "atribuidor_id")
	private Usuario atribuidor = null;

	@Identificador
	@NotNull(message = "Recebedor não especificado")
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "recebedor_id")
	private Usuario recebedor = null;

	@Identificador
	@JsonProperty
	@Column(nullable = true)
	private String atributo = null;

	@Identificador
	@NotNull(message = "Tipo não especificado")
	@JsonProperty
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Tipo tipo = null;

	@NotNull(message = "Especificar deve ser permitido ou não")
	@JsonProperty
	@Column(nullable = false /* , columnDefinition = "TINYINT(1)" */)
	private Boolean permitido = false;

	@JsonProperty("login_atribudor")
	@Transient
	@Indireto(atributo = "atribuidor", identificador = "login")
	private String loginAtribuidor = null;

	@JsonProperty("login_recebedor")
	@Transient
	@Indireto(atributo = "recebedor", identificador = "login")
	private String loginRecebedor = null;

	public Boolean getPermitido() {
		return permitido;
	}

	public void setPermitido(Boolean permitido) {
		this.permitido = permitido;
	}

	public String getLoginAtribuidor() {
		return loginAtribuidor;
	}

	public void setLoginAtribuidor(String loginAtribuidor) {
		this.loginAtribuidor = loginAtribuidor;
	}

	public String getLoginRecebedor() {
		return loginRecebedor;
	}

	public void setLoginRecebedor(String loginRecebedor) {
		this.loginRecebedor = loginRecebedor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Usuario getAtribuidor() {
		return atribuidor;
	}

	public void setAtribuidor(Usuario atribuidor) {
		this.atribuidor = atribuidor;
	}

	public Usuario getRecebedor() {
		return recebedor;
	}

	public void setRecebedor(Usuario recebedor) {
		this.recebedor = recebedor;
	}

	public String getAtributo() {
		return atributo;
	}

	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}

}
