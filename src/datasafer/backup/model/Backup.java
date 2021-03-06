package datasafer.backup.model;

import java.sql.Timestamp;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import datasafer.backup.dao.utility.annotations.FormulaHql;
import datasafer.backup.dao.utility.annotations.Identificador;
import datasafer.backup.dao.utility.annotations.Indireto;

@Entity
public class Backup {

	public static class Views {

	}

	public Backup() {}

	public Backup(Usuario proprietario, Estacao estacao, String nome, String descricao, Timestamp inicio, Long intervalo, String pasta) {
		this.proprietario = proprietario;
		this.estacao = estacao;
		this.nome = nome;
		this.descricao = descricao;
		this.inicio = inicio;
		this.intervalo = intervalo;
		this.pasta = pasta;
	}

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@Identificador
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "priorietario_id")
	private Usuario proprietario = null;

	@Identificador
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "estacao_id")
	private Estacao estacao = null;

	@JsonIgnore
	@OneToMany(mappedBy = "backup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Operacao> operacoes = new ArrayList<Operacao>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "backup_id")
	private List<Registro> registros = new ArrayList<Registro>();

	@Identificador
	@JsonProperty
	@Column(length = 40, nullable = false)
	private String nome = null;

	@JsonProperty
	@Column(length = 100, nullable = true)
	private String descricao = "";

	@JsonProperty
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = false)
	private Timestamp inicio = null;

	@JsonProperty
	@Column(nullable = false)
	private Long intervalo = 0L;

	@JsonProperty
	@Column(nullable = false)
	private String pasta = null;

	@JsonProperty(value = "ultima_operacao", access = Access.READ_ONLY)
	@Transient
	@FormulaHql(identificador = "id",
				formula = "SELECT operacao FROM Operacao operacao "
						+ "WHERE operacao.backup.id = :id "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE ultimaOperacao.backup = operacao.backup)")
	private Operacao ultimaOperacao = null;

	@JsonProperty(value = "status_operacoes", access = Access.READ_ONLY)
	@Transient
	@FormulaHql(identificador = "id",
				formula = "SELECT operacao.status, COUNT(operacao.status) FROM Operacao operacao "
						+ "WHERE operacao.backup.id = :id "
						+ "GROUP BY operacao.status ")
	private Map<Operacao.Status, Long> statusOperacoes = Arrays.stream(Operacao.Status.values()).collect(Collectors.toMap(Function.identity(), p -> 0L));

	@JsonProperty(value = "armazenamento_ocupado", access = Access.READ_ONLY)
	@Transient
	@FormulaHql(identificador = "id",
				formula = "SELECT SUM(operacao.tamanho) FROM Operacao operacao "
						+ "WHERE operacao.backup.id = :id "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup AND ultimaOperacao.status = 'EXECUTADO') ")
	private Long armazenamentoOcupado = 0L;

	@JsonProperty("nome_estacao")
	@Transient
	@FormulaHql(identificador = "id",
				formula = "SELECT backup.estacao.nome FROM Backup backup "
						+ "WHERE backup.id = :id")
	private String nomeEstacao = null;

	@JsonProperty("login_proprietario")
	@Transient
	@Indireto(atributo = "proprietario", identificador = "login")
	private String loginProprietario = null;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "backup_id")
	private List<Permissao> permissoes = new ArrayList<Permissao>();

	@JsonProperty
	@Column(nullable = false /* , columnDefinition = "TINYINT(1)" */)
	private Boolean ativo = true;

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

	public Estacao getEstacao() {
		return estacao;
	}

	public void setEstacao(Estacao estacao) {
		this.estacao = estacao;
	}

	public List<Operacao> getOperacoes() {
		return operacoes;
	}

	public void setOperacoes(List<Operacao> operacoes) {
		this.operacoes = operacoes;
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

	public Timestamp getInicio() {
		return inicio;
	}

	public void setInicio(Timestamp inicio) {
		this.inicio = inicio;
	}

	public Long getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(Long intervalo) {
		this.intervalo = intervalo;
	}

	public String getPasta() {
		return pasta;
	}

	public void setPasta(String pasta) {
		this.pasta = pasta;
	}

	public Operacao getUltimaOperacao() {
		return ultimaOperacao;
	}

	public void setUltimaOperacao(Operacao ultimaOperacao) {
		this.ultimaOperacao = ultimaOperacao;
	}

	public Map<Operacao.Status, Long> getStatusOperacoes() {
		return statusOperacoes;
	}

	public void setStatusOperacoes(Map<Operacao.Status, Long> statusOperacoes) {
		this.statusOperacoes = statusOperacoes;
	}

	public Long getArmazenamentoOcupado() {
		return armazenamentoOcupado;
	}

	public void setArmazenamentoOcupado(Long armazenamentoOcupado) {
		this.armazenamentoOcupado = armazenamentoOcupado;
	}

	public String getNomeEstacao() {
		return nomeEstacao;
	}

	public void setNomeEstacao(String nomeEstacao) {
		this.nomeEstacao = nomeEstacao;
	}

	public String getLoginProprietario() {
		return loginProprietario;
	}

	public void setLoginProprietario(String loginProprietario) {
		this.loginProprietario = loginProprietario;
	}

	public List<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissao> permissoes) {
		this.permissoes = permissoes;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

}
