package datasafer.backup.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "id", "usuarios" })
@Entity
public class Privilegio {

	public enum Permissao {
		ADMINISTRADOR("Administrador"),

		VISUALIZAR_PRIVILEGIOS("Visualizar privilegios"), CRIAR_PRIVILEGIOS("Criar privilegios"), MODIFICAR_PRIVILEGIOS(
				"Modificar privilegios"), EXCLUIR_PRIVILEGIOS("Excluir privilegios"),

		VISUALIZAR_USUARIOS("Visualizar usu�rios"), INSERIR_USUARIOS("Inserir usu�rios"), MODIFICAR_USUARIOS(
				"Modificar usu�rios"), EXCLUIR_USUARIOS("Excluir usu�rios"),

		VISUALIZAR_HOSTS("Visualizar hosts"), INSERIR_HOSTS("Inserir hosts"), MODIFICAR_HOSTS(
				"Modificar hosts"), EXCLUIR_HOSTS("Excluir hosts"),

		VISUALIZAR_BACKUPS("Visualizar backups"), INSERIR_BACKUPS("Inserir backups"), MODIFICAR_BACKUPS(
				"Modificar backups"), EXCLUIR_BACKUPS("Excluir backups"),

		VISUALIZAR_OPERACOES("Visualizar opera��es"), INSERIR_OPERACOES("Inserir opera��es"), MODIFICAR_OPERACOES(
				"Modificar opera��es"), EXCLUIR_OPERACOES("Excluir opera��es");

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

	@Column(unique = true, nullable = false)
	private String nome;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Set<Permissao> permissoes;

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
