package datasafer.backup.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "login_gerenciador", "nome", "descricao", "backups" })
@Entity
public class Estacao {

	public Estacao() {
		this.id = null;
		this.gerenciador = null;
		this.backups = new ArrayList<Backup>();
		this.registros = new ArrayList<Registro>();
		this.nome = null;
		this.descricao = null;

		this.statusBackups = new HashMap<Operacao.Status, Long>();
		for (Operacao.Status s : Operacao.Status.values()) {
			this.statusBackups.put(s, 0L);
		}
	}

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "gerenciador_id")
	private Usuario gerenciador;

	@JsonIgnore
	@OneToMany(mappedBy = "estacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("nome")
	private List<Backup> backups;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "estacao_id")
	@OrderBy("data")
	private List<Registro> registros;

	@JsonProperty
	@Column(length = 40, unique = true, nullable = false)
	private String nome;

	@JsonProperty
	@Column(length = 100, nullable = true)
	private String descricao;

	@JsonProperty(value = "status_backups", access = Access.READ_ONLY)
	@Transient
	private Map<Operacao.Status, Long> statusBackups;

	public Map<Operacao.Status, Long> getStatusBackups() {
		return statusBackups;
	}

	public void setStatusBackups(Map<Operacao.Status, Long> statusBackups) {
		this.statusBackups = statusBackups;
	}

	public Usuario getGerenciador() {
		return gerenciador;
	}

	public void setGerenciador(Usuario gerenciador) {
		this.gerenciador = gerenciador;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Backup> getBackups() {
		return backups;
	}

	public void setBackups(List<Backup> backups) {
		this.backups = backups;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Registro> getRegistros() {
		return registros;
	}

	public void setRegistros(List<Registro> registros) {
		this.registros = registros;
	}

}
