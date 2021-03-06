package datasafer.backup.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import datasafer.backup.dao.utility.annotations.FormulaHql;
import datasafer.backup.dao.utility.annotations.Identificador;
import datasafer.backup.dao.utility.annotations.Indireto;

@Entity
public class Estacao {

	public Estacao() {}

	public Estacao(Usuario proprietario, String nome, String descricao) {
		this.proprietario = proprietario;
		this.nome = nome;
		this.descricao = descricao;
	}

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "proprietario_id")
	private Usuario proprietario = null;

	@JsonIgnore
	@OneToMany(mappedBy = "estacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Backup> backups = new ArrayList<Backup>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "estacao_id")
	private List<Registro> registros = new ArrayList<Registro>();

	@Identificador
	@JsonProperty
	@Column(length = 40, unique = true, nullable = false)
	private String nome = null;

	@JsonProperty
	@Column(length = 100, nullable = true)
	private String descricao = "";

	@JsonProperty(value = "status_backups", access = Access.READ_ONLY)
	@Transient
	@FormulaHql(identificador = "id",
				formula = "SELECT operacao.status, COUNT(DISTINCT operacao.backup) FROM Operacao operacao "
						+ "WHERE operacao.backup.estacao.id = :id  "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup) "
						+ "GROUP BY operacao.status ")
	private Map<Operacao.Status, Long> statusBackups = Arrays.stream(Operacao.Status.values()).collect(Collectors.toMap(Function.identity(), p -> 0L));

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "estacao_id")
	private List<Permissao> permissoes = new ArrayList<Permissao>();

	@JsonProperty
	@Column(nullable = false /* , columnDefinition = "TINYINT(1)" */)
	private Boolean ativo = true;

	@JsonProperty("login_proprietario")
	@Transient
	@Indireto(atributo = "proprietario", identificador = "login")
	private String loginProprietario = null;

	public String getLoginProprietario() {
		return loginProprietario;
	}

	public void setLoginProprietario(String loginProprietario) {
		this.loginProprietario = loginProprietario;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getProprietario() {
		return proprietario;
	}

	public void setProprietario(Usuario proprietario) {
		this.proprietario = proprietario;
	}

	public List<Backup> getBackups() {
		return backups;
	}

	public void setBackups(List<Backup> backups) {
		this.backups = backups;
	}

	public List<Registro> getRegistros() {
		return registros;
	}

	public void setRegistros(List<Registro> registros) {
		this.registros = registros;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Map<Operacao.Status, Long> getStatusBackups() {
		return statusBackups;
	}

	public void setStatusBackups(Map<Operacao.Status, Long> statusBackups) {
		this.statusBackups = statusBackups;
	}

	public List<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissao> permissoes) {
		this.permissoes = permissoes;
	}

}
