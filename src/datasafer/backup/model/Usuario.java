package datasafer.backup.model;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import datasafer.backup.controller.UsuarioRestController;

@JsonIgnoreProperties({ "id", "hosts" })
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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 40, nullable = false)
	private String nome;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Host> hosts;

	@Column(length = 20, unique = true, nullable = false)
	private String login;

	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(nullable = false)
	private String senha;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@Column(nullable = false)
	private Long armazenamento;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "privilegio_id")
	private Privilegio privilegio;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario superior;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = false)
	private Date inseridoEm;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario inseridoPor;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = true)
	private Date modificadoEm;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario modificadoPor;

	@JsonProperty(access = Access.READ_ONLY)
	@Column(nullable = true)
	private Date excluidoEm;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Usuario excluidoPor;

	public Usuario getSuperior() {
		return superior;
	}

	public void setSuperior(Usuario superior) {
		this.superior = superior;
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

	public Privilegio getPrivilegio() {
		return privilegio;
	}

	public void setPrivilegio(Privilegio privilegio) {
		this.privilegio = privilegio;
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
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(),
					UsuarioRestController.SECRET.getBytes(StandardCharsets.UTF_8), 65535, 128);
			SecretKey key = skf.generateSecret(spec);
			this.senha = Base64.getEncoder().encodeToString(key.getEncoded());
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

	@JsonProperty("operacoes")
	public Map<Operacao.Status, Integer> getOperacoes() {
		Map<Operacao.Status, Integer> map = new LinkedHashMap<Operacao.Status, Integer>();
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
			map.put(s, count);
		}
		return map;
	}
}
