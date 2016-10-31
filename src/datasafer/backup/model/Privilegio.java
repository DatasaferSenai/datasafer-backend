package datasafer.backup.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import datasafer.backup.controller.deserializer.PrivilegioDeserializer;
import datasafer.backup.controller.serializer.PrivilegioSerializer;

@JsonDeserialize(using = PrivilegioDeserializer.class)
@JsonSerialize(using = PrivilegioSerializer.class)
@Entity
public class Privilegio {

	public enum Permissao {
		ADMINISTRADOR("Administrador"),

		VISUALIZAR_PRIVILEGIOS("Visualizar privilegios"), INSERIR_PRIVILEGIOS("Criar privilegios"), MODIFICAR_PRIVILEGIOS(
				"Modificar privilegios"), EXCLUIR_PRIVILEGIOS("Excluir privilegios"),

		VISUALIZAR_USUARIOS("Visualizar usuários"), INSERIR_USUARIOS("Inserir usuários"), MODIFICAR_USUARIOS("Modificar usuários"), EXCLUIR_USUARIOS(
				"Excluir usuários"),

		VISUALIZAR_HOSTS("Visualizar estacaos"), INSERIR_HOSTS("Inserir estacaos"), MODIFICAR_HOSTS("Modificar estacaos"), EXCLUIR_HOSTS("Excluir estacaos"),

		VISUALIZAR_BACKUPS("Visualizar backups"), INSERIR_BACKUPS("Inserir backups"), MODIFICAR_BACKUPS("Modificar backups"), EXCLUIR_BACKUPS(
				"Excluir backups"),

		VISUALIZAR_OPERACOES("Visualizar operações"), INSERIR_OPERACOES("Inserir operações"), MODIFICAR_OPERACOES("Modificar operações"), EXCLUIR_OPERACOES(
				"Excluir operações");

		private String descricao;

		private Permissao(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 40, unique = false, nullable = false)
	private String nome;

	@Column(length = 100, nullable = true)
	private String descricao;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Set<Permissao> permissoes;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "proprietario_id")
	private Usuario proprietario;

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

	public Set<Permissao> getPermissoes() {
		return this.permissoes;
	}

	public void setPermissoes(Set<Permissao> permissoes) {
		this.permissoes = permissoes;
	}

}
