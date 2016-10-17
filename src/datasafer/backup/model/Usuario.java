package datasafer.backup.model;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

	// IDENTIFICADORES
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, nullable = false)
	private String nome;

	// RELAÇÕES
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Host> hosts;

//	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	private Usuario usuarioAcima;
//	
//
//	public Usuario getUsuarioAcima() {
//		return usuarioAcima;
//	}
//
//	public void setUsuarioAcima(Usuario usuarioAcima) {
//		this.usuarioAcima = usuarioAcima;
//	}
//
//	public List<Usuario> getUsuariosAbaixo() {
//		return usuariosAbaixo;
//	}
//
//	public void setUsuariosAbaixo(List<Usuario> usuariosAbaixo) {
//		this.usuariosAbaixo = usuariosAbaixo;
//	}
//
//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//	@Fetch(FetchMode.SUBSELECT)
//	private List<Usuario> usuariosAbaixo;
	
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

	@Column(nullable = true)
	private String privilegio;

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
		this.senha = protegeSenha(senha);
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

	public String getPrivilegio() {
		return privilegio;
	}

	public void setPrivilegio(String privilegio) {
		this.privilegio = privilegio;
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

	private String protegeSenha(String senha) {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), UsuarioRestController.SECRET.getBytes(StandardCharsets.UTF_8), 65535, 128);
			SecretKey key = skf.generateSecret(spec);
			return Base64.getEncoder().encodeToString(key.getEncoded());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

}
