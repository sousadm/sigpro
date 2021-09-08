package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class TermoDeAbertura implements Serializable {

	private static final long serialVersionUID = 1L;

	private double valorPrevisto;
	private Date dataAbertura;
	private Date dataPrevistaTermino;
	private String objetivo;
	private String justificativa;
	private String estruturaAnalitica;

	@Column(precision = 12, scale = 2)
	public double getValorPrevisto() {
		return valorPrevisto;
	}

	public void setValorPrevisto(double valorPrevisto) {
		this.valorPrevisto = valorPrevisto;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(Date dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataPrevistaTermino() {
		return dataPrevistaTermino;
	}

	public void setDataPrevistaTermino(Date dataPrevistaTermino) {
		this.dataPrevistaTermino = dataPrevistaTermino;
	}

	@Column(columnDefinition = "text")
	public String getEstruturaAnalitica() {
		return estruturaAnalitica;
	}

	public void setEstruturaAnalitica(String estruturaAnalitica) {
		this.estruturaAnalitica = estruturaAnalitica;
	}

	@Column(columnDefinition = "text")
	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	@Column(columnDefinition = "text")
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

}