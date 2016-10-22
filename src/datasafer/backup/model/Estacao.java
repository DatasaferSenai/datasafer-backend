package datasafer.backup.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * Representa uma estação ou "estacao"
 */
@JsonIgnoreProperties({ "id", "usuario","backups", "proprietario","inseridoEm","inseridoPor","modificadoEm","modificadoPor","excluidoEm","excluidoPor" })
@Entity
public class Estacao {

	// ---------------------------------------------------------------------------------------------------

//	@ManyToOne
//	@JoinColumn(name = "usuario_id")
//	private Usuario usuario;

	@OneToMany(mappedBy = "estacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Backup> backups;

	// ---------------------------------------------------------------------------------------------------

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 40, nullable = false)
	private String nome;

	@Column(length = 100, nullable = true)
	private String descricao;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "proprietario_id")
	private Usuario proprietario;

	@JsonFormat(shape = Shape.STRING, pattern = "dd/mm/yyyy hh:MM:ss")
	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = false)
	private Date inseridoEm;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario inseridoPor;

	@JsonFormat(shape = Shape.STRING, pattern = "dd/mm/yyyy hh:MM:ss")
	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = true)
	private Date modificadoEm;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario modificadoPor;

	@JsonFormat(shape = Shape.STRING, pattern = "dd/mm/yyyy hh:MM:ss")
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

//	public Usuario getUsuario() {
//		return usuario;
//	}
//
//	public void setUsuario(Usuario usuario) {
//		this.usuario = usuario;
//	}

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

	@JsonProperty("operacoes")
	public Map<Operacao.Status, Integer> getOperacoes() {
		Map<Operacao.Status, Integer> map = new LinkedHashMap<Operacao.Status, Integer>();
		for (Operacao.Status s : Operacao.Status.values()) {
			Integer count = 0;
			for (Backup b : this.getBackups()) {
				for (Operacao p : b.getOperacoes()) {
					if (p.getStatus() == s) {
						count++;
					}
				}
			}
			map.put(s, count);
		}
		return map;
	}
}
