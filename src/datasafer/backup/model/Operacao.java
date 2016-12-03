package datasafer.backup.model;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import datasafer.backup.dao.utility.annotations.Indireto;

@Entity
public class Operacao {

	public Operacao() {}

	public Operacao(Backup backup, Timestamp data, Status status, Long tamanho) {
		this.backup = backup;
		this.data = data;
		this.status = status;
		this.tamanho = tamanho;
	}

	public enum Status {
		AGENDADO("Agendado"),
		EXECUTADO("Executado"),
		EXECUTANDO("Executando"),
		FALHA("Falha"),
		RESTAURAR("Restaurar"),
		RESTAURANDO("Restaurando"),
		RESTAURADO("Restaurado");

		private String descricao;

		private Status(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	};

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "backup_id")
	private Backup backup = null;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "operacao_id")
	private List<Registro> registros = new ArrayList<Registro>();

	@JsonProperty
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = false)
	private Timestamp data = null;

	@JsonProperty
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = null;

	@JsonProperty
	@Column(nullable = false)
	private Long tamanho = null;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "operacao_id")
	private List<Permissao> permissoes = new ArrayList<Permissao>();

	@JsonProperty("nome_backup")
	@Transient
	@Indireto(atributo = "backup", identificador = "nome")
	private String nomeBackup = null;

	public String getNomeBackup() {
		return nomeBackup;
	}

	public void setNomeBackup(String nomeBackup) {
		this.nomeBackup = nomeBackup;
	}

	public List<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissao> permissoes) {
		this.permissoes = permissoes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Backup getBackup() {
		return backup;
	}

	public void setBackup(Backup backup) {
		this.backup = backup;
	}

	public Timestamp getData() {
		return data;
	}

	public void setData(Timestamp data) {
		this.data = data;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getTamanho() {
		return tamanho;
	}

	public void setTamanho(Long tamanho) {
		this.tamanho = tamanho;
	}

	public List<Registro> getRegistros() {
		return registros;
	}

	public void setRegistros(List<Registro> registros) {
		this.registros = registros;
	}

}
