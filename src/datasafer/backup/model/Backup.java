package datasafer.backup.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Backup {

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
	@OneToMany(mappedBy = "backup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Operacao> operacoes;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinColumn(name = "backup_id")
	private List<Registro> registros;

	@JsonProperty(index = 0, value = "login_proprietario")
	public String getLoginProprietario() {
		if (proprietario != null) {
			return proprietario.getLogin();
		} else {
			return null;
		}
	}
	
	@JsonProperty(index = 1)
	@Column(length = 40, nullable = false)
	private String nome;

	@JsonProperty(index = 2)
	@Column(length = 100, nullable = true)
	private String descricao;

	@JsonProperty(index = 3)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = false)
	private Date inicio;

	@JsonProperty(index = 4)
	@Column(nullable = false)
	private long intervalo;

	@JsonProperty(index = 5)
	@Column(nullable = false)
	private String pasta;

	@JsonProperty(index = 6, value = "ultimaOperacao")
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

	@JsonProperty(index = 7, value = "operacoes")
	public HashMap<Operacao.Status, Integer> getContagemOperacoes() {
		HashMap<Operacao.Status, Integer> operacoes = new HashMap<Operacao.Status, Integer>();

		for (Operacao.Status s : Operacao.Status.values()) {
			int contagem = 0;
			for (Operacao o : this.getOperacoes()) {
				if (o.getStatus() == s) {
					contagem++;
				}
			}
			operacoes.put(s, contagem);
		}

		return operacoes;
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
