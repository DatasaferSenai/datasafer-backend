package datasafer.backup.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Registro {

	public Registro() {}

	public Registro(Usuario solicitante, Tipo tipo, Date data) {
		this.solicitante = solicitante;
		this.tipo = tipo;
		this.data = data;
	}

	public enum Tipo {
		INSERIDO("Inserido"),
		MODIFICADO("Modificado"),
		EXCLUIDO("Excluido");

		private String descricao;

		private Tipo(String descricao) {
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
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Usuario solicitante;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Tipo tipo;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = true)
	private Date data;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Usuario getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Usuario solicitante) {
		this.solicitante = solicitante;
	}

}
