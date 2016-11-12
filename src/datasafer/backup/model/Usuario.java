package datasafer.backup.model;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
@JsonPropertyOrder({ "login_superior", "delegacoes", "login", "senha", "status", "permissoes", "nome", "email", "armazenamento", "ocupado", "tentativas",
		"ultimaTentativa", "backups" })
@Entity
public class Usuario {

	public enum Status {
		ATIVO("Ativo"),
		INATIVO("Inativo"),
		SUSPENSO_ADMINISTRADOR("Suspenso pelo administrador"),
		SUSPENSO_TENTATIVAS("Suspenso por excesso de tentativas");

		private String descricao;

		private Status(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	};

	public enum Permissao {
		ADMINISTRADOR("Administrador"),

		VISUALIZAR_USUARIOS("Visualizar usuários"),
		INSERIR_USUARIOS("Inserir usuários"),
		MODIFICAR_USUARIOS("Modificar usuários"),
		EXCLUIR_USUARIOS("Excluir usuários"),

		VISUALIZAR_ESTACOES("Visualizar estacaos"),
		INSERIR_ESTACOES("Inserir estacaos"),
		MODIFICAR_ESTACOES("Modificar estacaos"),
		EXCLUIR_ESTACOES("Excluir estacaos"),

		VISUALIZAR_BACKUPS("Visualizar backups"),
		INSERIR_BACKUPS("Inserir backups"),
		MODIFICAR_BACKUPS("Modificar backups"),
		EXCLUIR_BACKUPS("Excluir backups"),

		VISUALIZAR_OPERACOES("Visualizar operações"),
		INSERIR_OPERACOES("Inserir operações"),
		MODIFICAR_OPERACOES("Modificar operações"),
		EXCLUIR_OPERACOES("Excluir operações");

		private String descricao;

		private Permissao(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	}

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = false)
	private int tentativas;

	@JsonProperty(access = Access.READ_ONLY)
	// @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = true)
	private Date ultimaTentativa;

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "superior_id")
	private Usuario superior;

	@JsonIgnore
	@OneToMany(mappedBy = "superior", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<Usuario> colaboradores;

	@JsonIgnore
	@OneToMany(mappedBy = "gerenciador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<Estacao> estacoes;

	@JsonIgnore
	@OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Backup> backups;

	@JsonIgnore
	@OneToMany(mappedBy = "solicitante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<Registro> registros;

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<Token> tokens;

	@JsonProperty(value = "login_superior")
	public String getLoginSuperior() {
		if (superior != null) {
			return superior.getLogin();
		} else {
			return null;
		}
	}

	@JsonProperty(value = "status_backups")
	public HashMap<Operacao.Status, Integer> getContagemBackups() {
		HashMap<Operacao.Status, Integer> operacoes = new HashMap<Operacao.Status, Integer>();

		for (Operacao.Status s : Operacao.Status.values()) {
			int contagem = 0;
			for (Backup b : this.getBackups()) {
				Operacao ultimaOperacao = b.getUltimaOperacao();
				if (ultimaOperacao != null && ultimaOperacao.getStatus() == s) {
					contagem++;
				}
			}
			operacoes.put(s, contagem);
		}

		return operacoes;
	}

	@JsonProperty(value = "armazenamento_ocupado")
	public long getOcupado() {
		long ocupado = 0L;

		for (Backup b : this.getBackups()) {
			Operacao ultimoSucesso = null;
			for (Operacao o : b.getOperacoes()) {
				if (o.getStatus() == Operacao.Status.SUCESSO) {
					if (ultimoSucesso == null) {
						ultimoSucesso = o;
					} else if (o.getData()
								.after(ultimoSucesso.getData())) {
						ultimoSucesso = o;
					}
				}
				if (ultimoSucesso != null) {
					ocupado += ultimoSucesso.getTamanho();
				}
			}
		}
		return ocupado;
	}

	@JsonProperty()
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Set<Permissao> delegacoes;

	@JsonProperty()
	@Column(length = 20, unique = true, nullable = false)
	private String login;

	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(nullable = false)
	private String senha;

	@JsonProperty()
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@JsonProperty()
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Set<Permissao> permissoes;

	@JsonProperty()
	@Column(length = 40, nullable = false)
	private String nome;

	@JsonProperty()
	@Column(length = 50, nullable = true)
	private String email;

	@JsonProperty()
	@Column(nullable = false)
	private long armazenamento;

	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	public List<Usuario> getColaboradores() {
		return colaboradores;
	}

	public void setColaboradores(List<Usuario> colaboradores) {
		this.colaboradores = colaboradores;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getTentativas() {
		return tentativas;
	}

	public void setTentativas(int tentativas) {
		this.tentativas = tentativas;
	}

	public Date getUltimaTentativa() {
		return ultimaTentativa;
	}

	public void setUltimaTentativa(Date ultimaTentativa) {
		this.ultimaTentativa = ultimaTentativa;
	}

	public Usuario getSuperior() {
		return superior;
	}

	public void setSuperior(Usuario superior) {
		this.superior = superior;
	}

	public List<Estacao> getEstacoes() {
		return estacoes;
	}

	public void setEstacoes(List<Estacao> estacoes) {
		this.estacoes = estacoes;
	}

	public List<Backup> getBackups() {
		return backups;
	}

	public void setBackups(List<Backup> backups) {
		this.backups = backups;
	}

	public Set<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(Set<Permissao> permissoes) {
		this.permissoes = permissoes;
	}

	public void setArmazenamento(long armazenamento) {
		this.armazenamento = armazenamento;
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
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(),
					"J0pjgqSuFXmCw8RQMPWaYT8XSBTneN0nDfMjLgUQ37Tp6l6I2SjQmhn5i7jCLZpO".getBytes(StandardCharsets.UTF_8), 65535, 128);
			SecretKey key = skf.generateSecret(spec);
			this.senha = Base64	.getEncoder()
								.encodeToString(key.getEncoded());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
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

	public Set<Permissao> getDelegacoes() {
		return delegacoes;
	}

	public void setDelegacoes(Set<Permissao> delegacoes) {
		this.delegacoes = delegacoes;
	}

	public List<Registro> getRegistros() {
		return registros;
	}

	public void setRegistros(List<Registro> registros) {
		this.registros = registros;
	}

}
