package datasafer.backup.model;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	private List<Backup> backups;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	@JoinColumn(name = "estacao_id")
	private List<Registro> registros;

	@Column(length = 40, unique = true, nullable = false)
	private String nome;

	@Column(length = 100, nullable = true)
	private String descricao;

	@JsonProperty("backups")
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

	@JsonIgnore
	public Registro getUltimoRegistro() {
		Registro ultimoRegistro = null;
		for (Registro r : this.getRegistros()) {
			if (ultimoRegistro == null) {
				ultimoRegistro = r;
			} else {
				if (r	.getData()
						.before(ultimoRegistro.getData())) {
					ultimoRegistro = r;
				}
			}
		}
		return ultimoRegistro;
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
