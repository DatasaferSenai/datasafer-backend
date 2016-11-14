package datasafer.backup.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "login_proprietario", "nome", "descricao", "inicio", "intervalo", "pasta", "ultimaOperacao", "status_operacoes" })
@Entity
public class Backup {

	public Backup() {
		this.id = null;
		this.proprietario = null;
		this.estacao = null;
		this.operacoes = new ArrayList<Operacao>();
		this.registros = new ArrayList<Registro>();
		this.nome = null;
		this.descricao = null;
		this.inicio = null;
		this.intervalo = 0L;
		this.pasta = "";

		this.statusOperacoes = new HashMap<Operacao.Status, Integer>();
		for (Operacao.Status s : Operacao.Status.values()) {
			this.statusOperacoes.put(s, 0);
		}
	}

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "priorietario_id")
	private Usuario proprietario;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "estacao_id")
	private Estacao estacao;

	@JsonIgnore
	@OneToMany(mappedBy = "backup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("data")
	private List<Operacao> operacoes;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "backup_id")
	@OrderBy("data")
	private List<Registro> registros;

	@JsonProperty
	@Column(length = 40, nullable = false)
	private String nome;

	@JsonProperty
	@Column(length = 100, nullable = true)
	private String descricao;

	@JsonProperty
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = false)
	private Date inicio;

	@JsonProperty
	@Column(nullable = false)
	private long intervalo;

	@JsonProperty
	@Column(nullable = false)
	private String pasta;

	@JsonProperty(value = "ultimaOperacao")
	public Operacao getUltimaOperacao() {
		Operacao ultimaOperacao = null;

		for (Operacao o : this.getOperacoes()) {
			if (ultimaOperacao == null) {
				ultimaOperacao = o;
			} else {
				if (o	.getData()
						.before(ultimaOperacao.getData())) {
					ultimaOperacao = o;
				}
			}
		}

		return ultimaOperacao;
	}

	@JsonProperty(value = "status_operacoes", access = Access.READ_ONLY)
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "status")
	@MapKeyEnumerated(EnumType.STRING)
	@Column(name = "contagem")
	@CollectionTable(name = "backup_status_operacoes", joinColumns = @JoinColumn(name = "backup_id"))
	private Map<Operacao.Status, Integer> statusOperacoes;

	public Map<Operacao.Status, Integer> getStatusOperacoes() {
		return statusOperacoes;
	}

	public void setStatusOperacoes(Map<Operacao.Status, Integer> statusOperacoes) {
		this.statusOperacoes = statusOperacoes;
	}

	public Usuario getProprietario() {
		return proprietario;
	}

	public void setProprietario(Usuario proprietario) {
		this.proprietario = proprietario;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public long getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(long intervalo) {
		this.intervalo = intervalo;
	}

	public String getPasta() {
		return pasta;
	}

	public void setPasta(String pasta) {
		this.pasta = pasta;
	}

	public List<Registro> getRegistros() {
		return registros;
	}

	public void setRegistros(List<Registro> registros) {
		this.registros = registros;
	}

}
