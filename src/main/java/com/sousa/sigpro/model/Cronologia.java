package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Embeddable
public class Cronologia implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date inicio;
	private Date termino;

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getTermino() {
		return termino;
	}

	public void setTermino(Date termino) {
		this.termino = termino;
	}

	@Transient
	public boolean isConcluido() {
		return termino != null;
	}

	@Transient
	public Date getTempo() {
		Long diff = Long.valueOf(0);
		if (inicio != null && termino != null) {
			diff = termino.getTime() - inicio.getTime();
		}
		return new Date(diff / 1000);
	}

}