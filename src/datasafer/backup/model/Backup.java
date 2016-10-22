package datasafer.backup.model;

import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;

import org.hibernate.FetchMode;
import org.hibernate.annotations.Fetch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({ "id", "host", "operacoes","proprietario","inseridoEm","inseridoPor","modificadoEm","modificadoPor","excluidoEm","excluidoPor" })
@Entity
public class Backup {

	public enum Frequencia {
		INTERVALO("Intervalo"), DIARIO("Diario"), SEMANAL("Semanal"), MENSAL("Mensal");

		private String descricao;

		private Frequencia(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	};

	@ManyToOne
	@JoinColumn(name = "host_id")
	private Host host;

	@OneToMany(mappedBy = "backup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Operacao> operacoes;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 40, nullable = false)
	private String nome;

	@Column(length = 100, nullable = true)
	private String descricao;

	@JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy hh:MM:ss")
	@Column(nullable = false)
	private Date inicio;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Frequencia frequencia;

	@Column(nullable = true)
	private Integer intervalo;

	@Column(nullable = false)
	private String pasta;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "proprietario_id")
	private Usuario proprietario;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = false)
	private Date inseridoEm;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario inseridoPor;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = true)
	private Date modificadoEm;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario modificadoPor;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = true)
	private Date excluidoEm;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario excluidoPor;

	public Usuario getProprietario() {
		return proprietario;
	}

	public void setProprietario(Usuario proprietario) {
		this.proprietario = proprietario;
	}

	public Date getInseridoEm() {
		return inseridoEm;
	}

	public void setInseridoEm(Date inseridoEm) {
		this.inseridoEm = inseridoEm;
	}

	public Usuario getInseridoPor() {
		return inseridoPor;
	}

	public void setInseridoPor(Usuario inseridoPor) {
		this.inseridoPor = inseridoPor;
	}

	public Date getModificadoEm() {
		return modificadoEm;
	}

	public void setModificadoEm(Date modificadoEm) {
		this.modificadoEm = modificadoEm;
	}

	public Usuario getModificadoPor() {
		return modificadoPor;
	}

	public void setModificadoPor(Usuario modificadoPor) {
		this.modificadoPor = modificadoPor;
	}

	public Date getExcluidoEm() {
		return excluidoEm;
	}

	public void setExcluidoEm(Date excluidoEm) {
		this.excluidoEm = excluidoEm;
	}

	public Usuario getExcluidoPor() {
		return excluidoPor;
	}

	public void setExcluidoPor(Usuario excluidoPor) {
		this.excluidoPor = excluidoPor;
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

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
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

	public Frequencia getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Frequencia frequencia) {
		this.frequencia = frequencia;
	}

	public Integer getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(Integer intervalo) {
		this.intervalo = intervalo;
	}

	public String getPasta() {
		return pasta;
	}

	public void setPasta(String pasta) {
		this.pasta = pasta;
	}

	@JsonProperty("status")
	public Operacao.Status getStatus() {
		if (operacoes.size() > 0) {
			Operacao ultimaOperacao = operacoes.get(0);
			for (Operacao operacao : operacoes) {
				if (operacao.getData().before(ultimaOperacao.getData())) {
					ultimaOperacao = operacao;
				}
			}
			return ultimaOperacao.getStatus();
		}
		return null;
	}

}
