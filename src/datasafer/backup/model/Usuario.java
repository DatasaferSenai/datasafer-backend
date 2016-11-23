package datasafer.backup.model;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import datasafer.backup.dao.utility.Modificador.Modificavel;
import datasafer.backup.dao.utility.Validador.Validar;

@Entity
public class Usuario {

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

	// public enum Permissao {
	// ADMINISTRADOR("Administrador"),
	//
	// VISUALIZAR_USUARIOS("Visualizar usuários"),
	// INSERIR_USUARIOS("Inserir usuários"),
	// MODIFICAR_USUARIOS("Modificar usuários"),
	// EXCLUIR_USUARIOS("Excluir usuários"),
	//
	// VISUALIZAR_ESTACOES("Visualizar estacaos"),
	// INSERIR_ESTACOES("Inserir estacaos"),
	// EXCLUIR_ESTACOES("Excluir estacaos"),
	//
	// VISUALIZAR_BACKUPS("Visualizar backups"),
	// INSERIR_BACKUPS("Inserir backups"),
	// MODIFICAR_BACKUPS("Modificar backups"),
	// EXCLUIR_BACKUPS("Excluir backups"),
	//
	// VISUALIZAR_OPERACOES("Visualizar operações"),
	// INSERIR_OPERACOES("Inserir operações"),
	// EXCLUIR_OPERACOES("Excluir operações");
	//
	// private String descricao;
	//
	// private Permissao(String descricao) {
	// this.descricao = descricao;
	// }
	//
	// @Override
	// public String toString() {
	// return this.descricao;
	// }
	// }

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "superior_id")
	private Usuario superior = null;

	@JsonIgnore
	@OneToMany(mappedBy = "superior", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Usuario> colaboradores = new ArrayList<Usuario>();

	@JsonIgnore
	@OneToMany(mappedBy = "gerenciador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Estacao> estacoes = new ArrayList<Estacao>();

	@JsonIgnore
	@OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Backup> backups = new ArrayList<Backup>();

	@JsonIgnore
	@OneToMany(mappedBy = "solicitante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Registro> solicitacoes = new ArrayList<Registro>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private List<Registro> registros = new ArrayList<Registro>();

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Autorizacao> autorizacoes = new ArrayList<Autorizacao>();

	// @Modificavel(autoModificavel = false)
	// @Validar
	// @JsonProperty
	// @ElementCollection(fetch = FetchType.EAGER)
	// @Column(nullable = true)
	// @Enumerated(EnumType.STRING)
	// private List<Permissao> delegacoes;

	// @Modificavel(autoModificavel = false)
	// @Validar
	// @JsonProperty
	// @ElementCollection(fetch = FetchType.EAGER)
	// @Column(nullable = true)
	// @Enumerated(EnumType.STRING)
	// private List<Permissao> permissoes;

	@Modificavel(autoModificavel = false)
	@Validar(comprimentoMinimo = 3, comprimentoMaximo = 20)
	@JsonProperty
	@Column(length = 20, unique = true, nullable = false)
	private String login = null;

	@Modificavel(autoModificavel = true)
	@Validar(comprimentoMinimo = 6, comprimentoMaximo = 32)
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(length = 32, nullable = false)
	private String senha = null;

	@Modificavel(autoModificavel = false)
	@Validar
	@JsonProperty
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Usuario.Status.ATIVO;

	@Modificavel
	@Validar
	@JsonProperty
	@Column(length = 40, nullable = false)
	private String nome = null;

	@Modificavel
	@Validar
	@JsonProperty
	@Column(length = 50, nullable = false)
	private String email = null;

	@Modificavel(autoModificavel = false)
	@Validar
	@JsonProperty
	@Column(nullable = false)
	private long armazenamento = 0L;

	@JsonProperty(value = "armazenamento_ocupado", access = Access.READ_ONLY)
	@Transient
	private long armazenamentoOcupado = 0L;

	@JsonProperty(value = "status_backups", access = Access.READ_ONLY)
	@Transient
	private Map<Operacao.Status, Long> statusBackups = Arrays.stream(Operacao.Status.values()).collect(Collectors.toMap(Function.identity(), p -> 0L));

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = false)
	private int tentativas = 0;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = true)
	private Timestamp ultimaTentativa = null;

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Notificacao> notificacoes = new ArrayList<Notificacao>();

	@JsonProperty("login_superior")
	@Transient
	private String loginSuperior = null;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private List<Permissao> permissoes = new ArrayList<Permissao>();

	@JsonIgnore
	@OneToMany(mappedBy = "atribuidor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Permissao> permissoesAtribuidas = new ArrayList<Permissao>();

	@JsonIgnore
	@OneToMany(mappedBy = "recebedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Permissao> permissoesRecebidas = new ArrayList<Permissao>();

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getSuperior() {
		return superior;
	}

	public void setSuperior(Usuario superior) {
		this.superior = superior;
	}

	public List<Usuario> getColaboradores() {
		return colaboradores;
	}

	public void setColaboradores(List<Usuario> colaboradores) {
		this.colaboradores = colaboradores;
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

	public List<Registro> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(List<Registro> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public List<Registro> getRegistros() {
		return registros;
	}

	public void setRegistros(List<Registro> registros) {
		this.registros = registros;
	}

	public List<Autorizacao> getAutorizacoes() {
		return autorizacoes;
	}

	public void setAutorizacoes(List<Autorizacao> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getArmazenamento() {
		return armazenamento;
	}

	public void setArmazenamento(long armazenamento) {
		this.armazenamento = armazenamento;
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

	public List<Notificacao> getNotificacoes() {
		return notificacoes;
	}

	public void setNotificacoes(List<Notificacao> notificacoes) {
		this.notificacoes = notificacoes;
	}

	public String getLoginSuperior() {
		return loginSuperior;
	}

	public void setLoginSuperior(String loginSuperior) {
		this.loginSuperior = loginSuperior;
	}

	public List<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissao> permissoes) {
		this.permissoes = permissoes;
	}

	public List<Permissao> getPermissoesAtribuidas() {
		return permissoesAtribuidas;
	}

	public void setPermissoesAtribuidas(List<Permissao> permissoesAtribuidas) {
		this.permissoesAtribuidas = permissoesAtribuidas;
	}

	public List<Permissao> getPermissoesRecebidas() {
		return permissoesRecebidas;
	}

	public void setPermissoesRecebidas(List<Permissao> permissoesRecebidas) {
		this.permissoesRecebidas = permissoesRecebidas;
	}

	public String getSenha() {
		return senha;
	}

}
