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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import datasafer.backup.dao.utility.annotations.FormulaHql;
import datasafer.backup.dao.utility.annotations.Identificador;
import datasafer.backup.dao.utility.annotations.Indireto;

@Entity
public class Usuario {

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id = null;
	
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "proprietario_id")
	private Usuario proprietario = null;

	@JsonIgnore
	@OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Usuario> usuarios = new ArrayList<Usuario>();

	@JsonIgnore
	@OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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

	@Identificador
	@JsonProperty
	@Size(min = 4, max = 20, message = "O login deve ter no mínimo de 6 e no máximo 20 caracteres")
	@NotNull(message = "Login não especificado")
	@Column(unique = true)
	private String login = null;

	@NotNull(message = "Senha não especificada")
	@Pattern(	regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,32}$",
				message = "Senha inválida. Deve conter ao menos um número, uma letra minúscula e uma letra maíuscula, não pode conter espaços e deve ter no mínimo 8 e no máximo 32 caracteres")
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(nullable = false)
	private String senha = null;

	@NotNull(message = "Especificar se o usuário deve estar ativo ou não")
	@JsonProperty
	@Column(nullable = false /* , columnDefinition = "TINYINT(1)" */)
	private Boolean ativo = true;

	@JsonProperty
	@Size(min = 3, max = 40, message = "O nome deve ter no mínimo 3 e no máximo 40 caracteres")
	@NotNull(message = "Nome não especificado")
	private String nome = null;

	@JsonProperty
	@Pattern(	regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
				message = "Email inválido")
	@Size(max = 50, message = "O email deve ter máximo 50 caracteres")
	@NotNull(message = "Email não especificado")
	private String email = null;

	@JsonProperty
	@Min(value = 0, message = "O armazenamento dever ser no mínimo 0")
	@NotNull(message = "Armazenamento não especificado")
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

	@JsonProperty("login_superior")
	@Transient
	@Indireto(atributo = "proprietario", identificador = "login")
	private String loginProprietario = null;

	public String getLoginProprietario() {
		return loginProprietario;
	}

	public void setLoginProprietario(String loginProprietario) {
		this.loginProprietario = loginProprietario;
	}

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

	public Usuario getProprietario() {
		return proprietario;
	}

	public void setProprietario(Usuario proprietario) {
		this.proprietario = proprietario;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
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

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
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

	public Long getArmazenamentoOcupado() {
		return armazenamentoOcupado;
	}

	public void setArmazenamentoOcupado(Long armazenamentoOcupado) {
		this.armazenamentoOcupado = armazenamentoOcupado;
	}

	public Map<Operacao.Status, Long> getStatusBackups() {
		return statusBackups;
	}

	public void setStatusBackups(Map<Operacao.Status, Long> statusBackups) {
		this.statusBackups = statusBackups;
	}

	public Integer getTentativas() {
		return tentativas;
	}

	public void setTentativas(Integer tentativas) {
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
