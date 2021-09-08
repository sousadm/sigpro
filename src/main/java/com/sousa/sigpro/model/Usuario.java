package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sousa.sigpro.model.tipo.TipoGrupo;
import com.sousa.sigpro.model.tipo.TipoRotina;

@Entity
@Table(name = "usuario")
@NamedQueries({
		@NamedQuery(name = "Usuario.lista", query = "select u from Pessoa p join p.usuario u where p.origem = :empresa") })
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date dataCadastro = new Date();
	private String nome;
	private String senha;
	private boolean permiteEmailProprio;
	private List<TipoGrupo> grupos;
	private List<TipoRotina> rotinas;

	public Usuario() {
		grupos = new ArrayList<TipoGrupo>();
		rotinas = new ArrayList<>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ElementCollection
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public List<TipoRotina> getRotinas() {
		return rotinas;
	}

	public void setRotinas(List<TipoRotina> rotinas) {
		this.rotinas = rotinas;
	}

	@ElementCollection
	@Enumerated(EnumType.STRING)
	@Column(length = 3)
	public List<TipoGrupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<TipoGrupo> grupos) {
		this.grupos = grupos;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	// @Column(length = 100)
	@Column(length = 100, nullable = false, unique = true)
	// @Column(length = 100, nullable = false, updatable = false, unique = true)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(length = 40)
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public boolean isPermiteEmailProprio() {
		return permiteEmailProprio;
	}

	public void setPermiteEmailProprio(boolean permiteEmailProprio) {
		this.permiteEmailProprio = permiteEmailProprio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}