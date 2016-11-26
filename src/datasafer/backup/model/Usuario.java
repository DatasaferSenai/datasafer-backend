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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import datasafer.backup.dao.utility.Carregador.FormulaHql;
import datasafer.backup.model.utility.Validador.Email;
import datasafer.backup.model.utility.Validador.Senha;

@Entity
public class Usuario {

	public enum Status {
		ATIVO("Usuário ativo"),
		INATIVO("Usuário inativo"),
		SUSPENSO_ADMINISTRADOR("Usuário suspenso pelo administrador"),
		SUSPENSO_TENTATIVAS("Usuário suspenso por excesso de tentativas");

		private String descricao;

		private Status(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return this.descricao;
		}
	};

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

	@JsonProperty
	@Size(min = 4, max = 20, message = "O login deve ter no mínimo de 6 e no máximo 20 caracteres")
	@NotNull(message = "Login inválido")
	@Column(unique = true)
	private String login = null;

	@JsonProperty(access = Access.WRITE_ONLY)
	@Senha
	@Size(max = 32, message = "A senha deve ter no máximo 32 caracteres")
	@NotNull(message = "Senha inválida")
	private String senha = null;

	// @Validar
	@JsonProperty
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Status inválido")
	private Status status = Usuario.Status.ATIVO;

	// @Validar
	@JsonProperty
	@Size(min = 3, max = 40, message = "O nome deve ter no mínimo 3 e no máximo 40 caracteres")
	@NotNull(message = "O nome não pode ser nulo")
	private String nome = null;

	@JsonProperty
	@Email
	@Size(max = 50, message = "O email deve ter máximo 50 caracteres")
	@NotNull(message = "O email não pode ser nulo")
	private String email = null;

	@JsonProperty
	@Min(value = 0, message = "O armazenamento dever ser no mínimo 0")
	@NotNull(message = "O armazenamento não pode ser nulo")
	private long armazenamento = 0L;

	@JsonProperty(value = "armazenamento_ocupado", access = Access.READ_ONLY)
	@Transient
	@FormulaHql(identificador = "id",
				formula = "SELECT SUM(operacao.tamanho) FROM Operacao operacao "
						+ "WHERE operacao.backup.proprietario.id = :id "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup AND ultimaOperacao.status = 'EXECUTADO') ")
	private Long armazenamentoOcupado = 0L;

	@JsonProperty(value = "status_backups", access = Access.READ_ONLY)
	@Transient
	@FormulaHql(identificador = "id",
				formula = "SELECT operacao.status, COUNT(DISTINCT operacao.backup) FROM Operacao operacao "
						+ "WHERE operacao.backup.proprietario.id = :id "
						+ "AND operacao.data = (SELECT MAX(ultimaOperacao.data) FROM Operacao ultimaOperacao WHERE operacao.backup = ultimaOperacao.backup) "
						+ "GROUP BY operacao.status ")
	private Map<Operacao.Status, Long> statusBackups = Arrays.stream(Operacao.Status.values()).collect(Collectors.toMap(Function.identity(), p -> 0L));

	@JsonProperty(access = Access.READ_ONLY)
	@Column
	private Integer tentativas = 0;

	@JsonProperty(access = Access.READ_ONLY)
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(nullable = true)
	private Timestamp ultimaTentativa = null;

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Notificacao> notificacoes = new ArrayList<Notificacao>();

	@JsonProperty("login_superior")
	@Transient
	@FormulaHql(identificador = "id",
				formula = "SELECT u.superior.login FROM Usuario u "
						+ "WHERE u.id = :id ")
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
												"J0pjgqSuFXmCw8RQMPWaYT8XSBTneN0nDfMjLgUQ37Tp6l6I2SjQmhn5i7jCLZpO".getBytes(StandardCharsets.UTF_8),
												65535,
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
