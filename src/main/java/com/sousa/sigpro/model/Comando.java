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

import org.hibernate.validator.constraints.NotEmpty;

import com.sousa.sigpro.model.tipo.TipoComando;

@Entity
@NamedQueries({
		@NamedQuery(name = "Comando.lista", query = "select t from Comando t where t.origem = :origem order by t.descricao"),
		@NamedQuery(name = "Comando.porDescricao", query = "select t from Comando t where t.descricao like :descricao and t.origem = :origem order by t.descricao"),
		@NamedQuery(name = "Comando.porTipo", query = "select t from Comando t where t.tipo = :tipo and t.origem = :origem") })
public class Comando implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String descricao;
	private TipoComando tipo;
	private Pessoa origem;

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
	public Pessoa getOrigem() {
		return origem;
	}

	public void setOrigem(Pessoa origem) {
		this.origem = origem;
	}

	@Enumerated(EnumType.STRING)
	public TipoComando getTipo() {
		return tipo;
	}

	public void setTipo(TipoComando tipo) {
		this.tipo = tipo;
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
		Comando other = (Comando) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}