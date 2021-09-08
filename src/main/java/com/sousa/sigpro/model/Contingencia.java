package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.sousa.sigpro.model.tipo.TipoMedidaContingencia;

@Entity
@NamedQueries({
		@NamedQuery(name = "Contingencia.lista", query = "select d from Contingencia d where d.empresa = :empresa order by d.medida, d.descricao"),
		@NamedQuery(name = "Contingencia.medida", query = "select d from Contingencia d where d.medida = :medida and d.empresa = :empresa order by d.descricao") })
public class Contingencia implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa empresa;
	private String descricao;
	private TipoMedidaContingencia medida;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	@Column(length = 8)
	@Enumerated(EnumType.STRING)
	public TipoMedidaContingencia getMedida() {
		return medida;
	}

	public void setMedida(TipoMedidaContingencia medida) {
		this.medida = medida;
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
		Contingencia other = (Contingencia) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}