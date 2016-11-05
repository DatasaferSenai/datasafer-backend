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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import datasafer.backup.controller.deserializer.EstacaoDeserializer;
import datasafer.backup.controller.serializer.EstacaoSerializer;

@JsonDeserialize(using = EstacaoDeserializer.class)
@JsonSerialize(using = EstacaoSerializer.class)
@Entity
public class Estacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "estacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Backup> backups;

	@Column(length = 40, unique = true, nullable = false)
	private String nome;

	@Column(length = 100, nullable = true)
	private String descricao;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "gerenciador_id")
	private Usuario gerenciador;

	@Column(nullable = false)
	private Date inseridoEm;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario inseridoPor;

	@Column(nullable = true)
	private Date modificadoEm;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario modificadoPor;

	@Column(nullable = true)
	private Date excluidoEm;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario excluidoPor;

	public Usuario getGerenciador() {
		return gerenciador;
	}

	public void setGerenciador(Usuario gerenciador) {
		this.gerenciador = gerenciador;
	}
	
	public Usuario getProprietario() {
		return gerenciador;
	}

	public void setProprietario(Usuario proprietario) {
		this.gerenciador = proprietario;
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

	// public Usuario getUsuario() {
	// return usuario;
	// }
	//
	// public void setUsuario(Usuario usuario) {
	// this.usuario = usuario;
	// }

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
