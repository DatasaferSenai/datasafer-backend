package datasafer.backup.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"id","backup"})
@Entity
public class Operacao {
	
	private enum Status {
		SUCESSO("Sucesso"), EXECUTANDO("Executando"), FALHA("Falha"), AGENDADO("Agendado"), EXCLUIDO("Excluído");

		private String descricao;

		private Status(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	};
	
	// IDENTIFICADORES
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// RELAÇÕES
	@ManyToOne
	@JoinColumn(name = "backup_id")
	private Backup backup;
	
	// ATRIBUTOS
	@Column
	private Date data;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@Column
	private Long tamanho;
	
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
}
