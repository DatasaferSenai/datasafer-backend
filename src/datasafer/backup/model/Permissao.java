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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "atribuidor_id")
	private Usuario atribuidor = null;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "recebedor_id")
	private Usuario recebedor = null;

	@JsonProperty
	@Column(nullable = true)
	private String atributo = null;

	@JsonProperty
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Tipo tipo = null;

	@JsonProperty
	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean permitido = false;

	public boolean isPermitido() {
		return permitido;
	}

	public void setPermitido(boolean permitido) {
		this.permitido = permitido;
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
