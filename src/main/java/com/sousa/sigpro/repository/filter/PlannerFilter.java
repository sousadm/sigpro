package com.sousa.sigpro.repository.filter;

import java.io.Serializable;
import java.util.Date;

import com.sousa.sigpro.model.Pessoa;

public class PlannerFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Pessoa pessoa;
	private Date inicio;
	private Date termino;

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getTermino() {
		return termino;
	}

	public void setTermino(Date termino) {
		this.termino = termino;
	}

}
