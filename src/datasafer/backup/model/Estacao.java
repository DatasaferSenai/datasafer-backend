package datasafer.backup.model;

import java.util.ArrayList;
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
import javax.persistence.OrderBy;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "login_gerenciador", "nome", "descricao", "backups" })
@Entity
public class Estacao {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "gerenciador_id")
	private Usuario gerenciador;

	@JsonIgnore
	@OneToMany(mappedBy = "estacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("nome")
	private List<Backup> backups = new ArrayList<Backup>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "estacao_id")
	@OrderBy("data")
	private List<Registro> registros = new ArrayList<Registro>();

	@JsonProperty(value = "login_gerenciador")
	public String getLoginGerenciador() {
		if (gerenciador != null) {
			return gerenciador.getLogin();
		} else {
			return null;
		}
	}

	@JsonProperty
	@NaturalId(mutable = true)
	@Column(length = 40, unique = true, nullable = false)
	private String nome;

	@JsonProperty
	@Column(length = 100, nullable = true)
	private String descricao;

	@JsonProperty(value = "status_backups")
	public HashMap<Operacao.Status, Integer> getContagemBackups() {
		HashMap<Operacao.Status, Integer> operacoes = new HashMap<Operacao.Status, Integer>();

		for (Operacao.Status s : Operacao.Status.values()) {
			int contagem = 0;
			for (Backup b : this.getBackups()) {
				Operacao ultimaOperacao = b.getUltimaOperacao();
				if (ultimaOperacao != null && ultimaOperacao.getStatus() == s) {
					contagem++;
				}
			}
			operacoes.put(s, contagem);
		}

		return operacoes;
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
