package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
		@NamedQuery(name = "Parametro.definicao", query = "select p from Parametro p where p.grupo = :grupo and p.codigo = :codigo and p.empresa = :empresa") })
public class Parametro implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa empresa;
	private String codigo;
	private String grupo;
	private String valor;

	public Parametro() {

	}

	public Parametro(String codigo, String grupo, String valor, Pessoa empresa) {
		this.codigo = codigo;
		this.grupo = grupo;
		this.valor = valor;
		this.empresa = empresa;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	@Column(name = "codigo", columnDefinition = "text", nullable = false)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(columnDefinition = "text")
	public String getValor() {
		return valor;
	}

	@Column(name = "grupo", columnDefinition = "text", nullable = false)
	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parametro other = (Parametro) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}