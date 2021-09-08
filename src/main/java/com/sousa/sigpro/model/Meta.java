package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoMeta;
import com.sousa.sigpro.util.SuporteData;

@Entity
@NamedQueries({
		@NamedQuery(name = "Meta.consulta", query = "select m from Meta m where m.tipo = :tipo and m.ano = :ano and m.mes = :mes and m.pessoa = :pessoa") })
public class Meta implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private int ano;
	private int mes;
	private double valor;
	private TipoMeta tipo;
	private Pessoa pessoa;

	public Meta() {
		valor = 0;
	}

	@Transient
	public String getNomeMes() {
		if (mes >= 0) {
			return SuporteData.nomeMes(mes);
		} else {
			return null;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public TipoMeta getTipo() {
		return tipo;
	}

	public void setTipo(TipoMeta tipo) {
		this.tipo = tipo;
	}

	@ManyToOne
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
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
		Meta other = (Meta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}