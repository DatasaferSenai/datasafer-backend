package datasafer.backup.model;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import datasafer.backup.dao.utility.Modificador.Modificavel;
import datasafer.backup.dao.utility.Validador.Validar;

@JsonPropertyOrder({	"login_superior", "delegacoes", "login", "senha", "status", "permissoes", "nome", "email", "armazenamento", "ocupado", "tentativas",
						"ultimaTentativa", "backups" })
@Entity
public class Usuario {

	public Usuario() {
		this.id = null;
		this.superior = null;
		this.colaboradores = new HashSet<Usuario>();
		this.estacoes = new HashSet<Estacao>();
		this.backups = new HashSet<Backup>();
		this.registros = new HashSet<Registro>();
		this.autorizacoes = new HashSet<Autorizacao>();
		this.delegacoes = new HashSet<Permissao>();
		this.login = null;
		this.senha = null;
		this.status = null;
		this.permissoes = new HashSet<Permissao>();
		this.nome = null;
		this.email = null;
		this.armazenamento = 0L;
		this.armazenamentoOcupado = 0L;
		this.tentativas = 0;
		this.ultimaTentativa = null;

		this.statusBackups = new HashMap<Operacao.Status, Long>();
		for (Operacao.Status s : Operacao.Status.values()) {
			this.statusBackups.put(s, 0L);
		}

		this.notificacoes = new HashSet<Notificacao>();
	}

	public enum Status {
		ATIVO("Usuário ativo"),
		INATIVO("Usuário inativo"),
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
		EXCLUIR_ESTACOES("Excluir estacaos"),

		VISUALIZAR_BACKUPS("Visualizar backups"),
		INSERIR_BACKUPS("Inserir backups"),
		MODIFICAR_BACKUPS("Modificar backups"),
		EXCLUIR_BACKUPS("Excluir backups"),

		VISUALIZAR_OPERACOES("Visualizar operações"),
		INSERIR_OPERACOES("Inserir operações"),
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

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "superior_id")
	private Usuario superior;

	@JsonIgnore
	@OneToMany(mappedBy = "superior", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Usuario> colaboradores;

	@JsonIgnore
	@OneToMany(mappedBy = "gerenciador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Estacao> estacoes;

	@JsonIgnore
	@OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Backup> backups;

	@JsonIgnore
	@OneToMany(mappedBy = "solicitante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Registro> solicitacoes;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Set<Registro> registros;

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Autorizacao> autorizacoes;

	@Modificavel(autoModificavel = false)
	@Validar
	@JsonProperty
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Set<Permissao> delegacoes;

	@Modificavel(autoModificavel = false)
	@Validar
	@JsonProperty
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Set<Permissao> permissoes;

	@Modificavel(autoModificavel = false)
	@Validar(comprimentoMinimo = 3, comprimentoMaximo = 20)
	@JsonProperty
	@Column(length = 20, unique = true, nullable = false)
	private String login;

	@Modificavel(autoModificavel = true)
	@Validar(comprimentoMinimo = 6, comprimentoMaximo = 32)
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(length = 32, nullable = false)
	private String senha;

	@Modificavel(autoModificavel = false)
	@Validar
	@JsonProperty
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@Modificavel
	@Validar
	@JsonProperty
	@Column(length = 40, nullable = false)
	private String nome;

	@Modificavel
	@Validar
	@JsonProperty
	@Column(length = 50, nullable = false)
	private String email;

	@Modificavel(autoModificavel = false)
	@Validar
	@JsonProperty
	@Column(nullable = false)
	private long armazenamento;

	@JsonProperty(value = "armazenamento_ocupado", access = Access.READ_ONLY)
	@Transient
	private long armazenamentoOcupado;

	@JsonProperty(value = "status_backups", access = Access.READ_ONLY)
	@Transient
	private Map<Operacao.Status, Long> statusBackups;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = false)
	private int tentativas;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = true)
	private Timestamp ultimaTentativa;

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Notificacao> notificacoes;

	@JsonProperty("login_superior")
	public String getLoginSuperior() {
		return superior.getLogin();
	}

	public Set<Registro> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Set<Registro> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Set<Notificacao> getNotificacoes() {
		return notificacoes;
	}

	public void setNotificacoes(Set<Notificacao> notificacoes) {
		this.notificacoes = notificacoes;
	}

	public long getArmazenamentoOcupado() {
		return armazenamentoOcupado;
	}

	public void setArmazenamentoOcupado(long armazenamentoOcupado) {
		this.armazenamentoOcupado = armazenamentoOcupado;
	}

	public Map<Operacao.Status, Long> getStatusBackups() {
		return statusBackups;
	}

	public void setStatusBackups(Map<Operacao.Status, Long> statusBackups) {
		this.statusBackups = statusBackups;
	}

	public Set<Autorizacao> getAutorizacoes() {
		return autorizacoes;
	}

	public void setAutorizacoes(Set<Autorizacao> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}

	public Set<Usuario> getColaboradores() {
		return colaboradores;
	}

	public void setColaboradores(Set<Usuario> colaboradores) {
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

	public Timestamp getUltimaTentativa() {
		return ultimaTentativa;
	}

	public void setUltimaTentativa(Timestamp ultimaTentativa) {
		this.ultimaTentativa = ultimaTentativa;
	}

	public Usuario getSuperior() {
		return superior;
	}

	public void setSuperior(Usuario superior) {
		this.superior = superior;
	}

	public Set<Estacao> getEstacoes() {
		return estacoes;
	}

	public void setEstacoes(Set<Estacao> estacoes) {
		this.estacoes = estacoes;
	}

	public Set<Backup> getBackups() {
		return backups;
	}

	public void setBackups(Set<Backup> backups) {
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
			PBEKeySpec spec = new PBEKeySpec(	senha.toCharArray(),
												"J0pjgqSuFXmCw8RQMPWaYT8XSBTneN0nDfMjLgUQ37Tp6l6I2SjQmhn5i7jCLZpO".getBytes(StandardCharsets.UTF_8), 65535,
												128);
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

	public Set<Registro> getRegistros() {
		return registros;
	}

	public void setRegistros(Set<Registro> registros) {
		this.registros = registros;
	}

}
