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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@JsonIgnoreProperties({ "id", "backup" })
@Entity
public class Operacao {

	public enum Status {
		SUCESSO("Sucesso"), EXECUTANDO("Executando"), FALHA("Falha"), AGENDADO("Agendado"), EXCLUIDO("Exclu�do");

		private String descricao;

		private Status(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	};

	//---------------------------------------------------------------------------------------------------
	
	@ManyToOne
	@JoinColumn(name = "backup_id")
	private Backup backup;
	
	//---------------------------------------------------------------------------------------------------
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//@JsonFormat(shape = Shape.STRING, pattern = "yyyy-mm-dd hh:MM:ss")
	@JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy hh:MM:ss")
	@Column(nullable = false)
	private Date data;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private Long tamanho;

	@Column(nullable = true)
	private Date dataInclusao;
	
	@Column(nullable = true)
	private Date dataModificacao;
	
	@Column(nullable = true)
	private Date dataExclusao;
	
	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public Date getDataModificacao() {
		return dataModificacao;
	}

	public void setDataModificacao(Date dataModificacao) {
		this.dataModificacao = dataModificacao;
	}

	public Date getDataExclusao() {
		return dataExclusao;
	}

	public void setDataExclusao(Date dataExclusao) {
		this.dataExclusao = dataExclusao;
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
