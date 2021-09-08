package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.validator.constraints.NotEmpty;

import com.fincatto.documentofiscal.DFUnidadeFederativa;

@Entity
@NamedQueries({
		@NamedQuery(name = "Municipio.lista", query = "select m from Municipio m where m.uf like :UF order by m.descricao") })
public class Municipio implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String descricao;
	private DFUnidadeFederativa uf;

	public Municipio() {

	}

	@Override
	public String toString() {
		return descricao + " - " + uf.getCodigo();
	}

	public Municipio(Long id, String descricao, String uf) {
		this.id = id;
		this.descricao = descricao;
		this.uf = DFUnidadeFederativa.valueOfCodigo(uf);
	}

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotEmpty
	@Column(length = 100, nullable = false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public DFUnidadeFederativa getUf() {
		return uf;
	}

	public void setUf(DFUnidadeFederativa uf) {
		this.uf = uf;
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
		Municipio other = (Municipio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}