package datasafer.backup.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import datasafer.backup.controller.UsuarioRestController;

@JsonIgnoreProperties({ "id", "hosts", "token" })
@Entity
public class Usuario {

	public enum Status {
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

	public enum Privilegio {
		VISUALIZACAO("Visualização"), OPERADOR("Operador"), ADMINISTRADOR("Administrador"), SUPER("Super");

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

	@Column(length = 20, nullable = false)
	private String nome;

	// RELAÇÕES
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Host> hosts;

	// ATRIBUTOS
	@Column(length = 20, unique = true, nullable = false)
	private String login;

	@Column(nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String senha;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private Long armazenamento;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Privilegio privilegio;

	@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Token token;

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
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

	@JsonProperty("operacoes")
	public Map<String, Integer> getOperacoes() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		for (Operacao.Status s : Operacao.Status.values()) {
			Integer count = 0;
			for (Host h : this.getHosts()) {
				for (Backup b : h.getBackups()) {
					for (Operacao p : b.getOperacoes()) {
						if (p.getStatus() == s) {
							count++;
						}
					}
				}
			}
			map.put(s.toString(), count);
		}
		return map;
	}

	private String protegeSenha(String senha) {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), UsuarioRestController.SECRET.getBytes(), 65535, 128);
			SecretKey key = skf.generateSecret(spec);
			return new String(key.getEncoded(), "UTF-8");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
