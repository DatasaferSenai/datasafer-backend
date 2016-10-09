package datasafer.backup.model;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"id","usuario", "backups"})
@Entity
public class Host {
	
	// IDENTIFICADORES
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 20, nullable = false)
	private String nome;
	
	// RELAÇÕES
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
	
	@OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
	private List<Backup> backups;

	// ATRIBUTOS
	@Column(length = 50)
	private String descricao;

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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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

	@JsonProperty("falha")
	public boolean isFalha() {
		for (Backup backup : backups) {
			if (backup.getStatus() == Operacao.Status.FALHA) {
				return true;
			}
		}
		return false;
	}
	
}
