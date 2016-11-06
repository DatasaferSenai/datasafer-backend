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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Operacao {

	public enum Status {
		SUCESSO("sucesso"), EXECUTANDO("executando"), FALHA("falha"), AGENDADO("agendado"), EXCLUIDO("excluído");

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
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "backup_id")
	private Backup backup;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "operacao_id")
	private List<Registro> registros;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = false)
	private Date data;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private long tamanho;

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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
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
