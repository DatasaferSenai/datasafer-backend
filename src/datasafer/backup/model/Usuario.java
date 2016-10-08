package datasafer.backup.model;

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
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"id","hosts"})
@Entity
public class Usuario {

	private enum Status {
		ATIVO("Ativo"), SUSPENSO_ADMINISTRADOR("Suspenso pelo administrador"), SUSPENSO_TENTATIVAS(
				"Suspenso por excesso de tentativas");

		private String descricao;

		private Status(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	};

	private enum Privilegio {
		COMUM("Comum"), ADMINISTRADOR("Administrador");

		private String descricao;

		private Privilegio(String descricao) {
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

	@Column(length = 20)
	private String nome;

	// RELAÇÕES
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Host> hosts;

	// ATRIBUTOS
	@Column(length = 20, nullable = false, unique = true)
	private String login;

	@Column
	private String senha;

	@Enumerated(EnumType.STRING)
	private Status status;

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

	public List<Host> getHosts() {
		return hosts;
	}

	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		
		
		
		return senha;
	}

	public void setSenha(String senha) {
		/*JWTSigner signer = new JWTSigner(UsuarioRestController.SECRET);
		HashMap<String,Object> claims = new HashMap<>();
		this.senha = signer.sign(claims);*/
		this.senha = senha;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getArmazenamento() {
		return armazenamento;
	}

	public void setArmazenamento(Long armazenamento) {
		this.armazenamento = armazenamento;
	}

	public Privilegio getPrivilegio() {
		return privilegio;
	}

	public void setPrivilegio(Privilegio privilegio) {
		this.privilegio = privilegio;
	}

	@Column
	private Long armazenamento;

	@Enumerated(EnumType.STRING)
	private Privilegio privilegio;

}
