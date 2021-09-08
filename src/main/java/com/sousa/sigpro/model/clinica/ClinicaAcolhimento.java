package com.sousa.sigpro.model.clinica;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class ClinicaAcolhimento implements Serializable {

	private static final long serialVersionUID = 1L;

	private double altura = 0;
	private double peso = 0;
	private double batimento = 0;
	private double temperatura = 0;
	private double pressaoSistolica = 0;
	private double pressaoDiastolica = 0;
	private TipoAtendimentoPadrao padrao = TipoAtendimentoPadrao.NORMAL;
	private Date dataAcolhimento = new Date();
	private Date dataLiberaClinica;
	private String sintoma;

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataLiberaClinica() {
		return dataLiberaClinica;
	}

	public void setDataLiberaClinica(Date dataLiberaClinica) {
		this.dataLiberaClinica = dataLiberaClinica;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataAcolhimento() {
		return dataAcolhimento;
	}

	public void setDataAcolhimento(Date dataAcolhimento) {
		this.dataAcolhimento = dataAcolhimento;
	}

	@Column(precision = 12, scale = 2)
	public double getAltura() {
		return altura;
	}

	public void setAltura(double altura) {
		this.altura = altura;
	}

	@Column(precision = 12, scale = 2)
	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	@Column(precision = 12, scale = 2)
	public double getBatimento() {
		return batimento;
	}

	public void setBatimento(double batimento) {
		this.batimento = batimento;
	}

	@Column(precision = 12, scale = 0)
	public double getPressaoSistolica() {
		return pressaoSistolica;
	}

	public void setPressaoSistolica(double pressaoSistolica) {
		this.pressaoSistolica = pressaoSistolica;
	}

	@Column(precision = 12, scale = 0)
	public double getPressaoDiastolica() {
		return pressaoDiastolica;
	}

	public void setPressaoDiastolica(double pressaoDiastolica) {
		this.pressaoDiastolica = pressaoDiastolica;
	}

	@Column(precision = 12, scale = 2)
	public double getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(double temperatura) {
		this.temperatura = temperatura;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public TipoAtendimentoPadrao getPadrao() {
		return padrao;
	}

	public void setPadrao(TipoAtendimentoPadrao padrao) {
		this.padrao = padrao;
	}

	@Column(columnDefinition = "text")
	public String getSintoma() {
		return sintoma;
	}

	public void setSintoma(String sintoma) {
		this.sintoma = sintoma;
	}

}
