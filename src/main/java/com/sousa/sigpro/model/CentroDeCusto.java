package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.sousa.sigpro.model.tipo.TipoCentroCusto;

@Entity
@Table(name = "centro_custo")
@NamedQueries({
		@NamedQuery(name = "CentroCusto.lista", query = "select c from CentroDeCusto c where c.empresa = :empresa order by c.descricao"),
		@NamedQuery(name = "CentroCusto.listaTipo", query = "select c from CentroDeCusto c where c.empresa = :empresa and c.tipo like :tipo order by c.descricao"),
		@NamedQuery(name = "CentroCusto.ordem", query = "select c from CentroDeCusto c where c.empresa = :empresa order by c.tipo") })
public class CentroDeCusto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String descricao;
	private TipoCentroCusto tipo;
	private Pessoa empresa;
	private boolean permiteAnaliseDre;
	private boolean comissao;
	private boolean abastecimento;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@ManyToOne
	@JoinColumn(name = "empresa", nullable = false)
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	@Column(length = 30)
	@Enumerated(EnumType.STRING)
	public TipoCentroCusto getTipo() {
		return tipo;
	}

	public void setTipo(TipoCentroCusto tipo) {
		this.tipo = tipo;
	}

	public boolean isComissao() {
		return comissao;
	}

	public void setComissao(boolean comissao) {
		this.comissao = comissao;
	}

	public boolean isAbastecimento() {
		return abastecimento;
	}

	public void setAbastecimento(boolean abastecimento) {
		this.abastecimento = abastecimento;
	}

	public boolean isPermiteAnaliseDre() {
		return permiteAnaliseDre;
	}

	public void setPermiteAnaliseDre(boolean permiteAnaliseDre) {
		this.permiteAnaliseDre = permiteAnaliseDre;
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
		CentroDeCusto other = (CentroDeCusto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}