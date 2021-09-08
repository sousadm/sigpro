package com.sousa.sigpro.model.clinica;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sousa.sigpro.model.Colaborador;

@Embeddable
public class ClinicaAtendimento implements Serializable {

	private static final long serialVersionUID = 1L;

	private Colaborador consultor;
	private Date dataInicioAtendimento;
	private Date dataTerminoAtendimento;
	private String orientacao;
	private Doenca patologia;

	public ClinicaAtendimento() {
		consultor = new Colaborador();
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataInicioAtendimento() {
		return dataInicioAtendimento;
	}

	public void setDataInicioAtendimento(Date dataInicioAtendimento) {
		this.dataInicioAtendimento = dataInicioAtendimento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataTerminoAtendimento() {
		return dataTerminoAtendimento;
	}

	public void setDataTerminoAtendimento(Date dataTerminoAtendimento) {
		this.dataTerminoAtendimento = dataTerminoAtendimento;
	}

	@ManyToOne
	public Doenca getPatologia() {
		return patologia;
	}

	public void setPatologia(Doenca patologia) {
		this.patologia = patologia;
	}

	@ManyToOne
	public Colaborador getConsultor() {
		return consultor;
	}

	public void setConsultor(Colaborador consultor) {
		this.consultor = consultor;
	}

	@Column(columnDefinition = "text")
	public String getOrientacao() {
		return orientacao;
	}

	public void setOrientacao(String orientacao) {
		this.orientacao = orientacao;
	}

}