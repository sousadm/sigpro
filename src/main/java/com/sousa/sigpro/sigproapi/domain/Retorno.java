package com.sousa.sigpro.sigproapi.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Retorno {

	private Long id;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date data;

	private String descritivo;

	private TipoRetorno tipo;

	private Demanda demanda;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDescritivo() {
		return descritivo;
	}

	public void setDescritivo(String descritivo) {
		this.descritivo = descritivo;
	}

	public Demanda getDemanda() {
		return demanda;
	}

	public void setDemanda(Demanda demanda) {
		this.demanda = demanda;
	}

	public TipoRetorno getTipo() {
		return tipo;
	}

	public void setTipo(TipoRetorno tipo) {
		this.tipo = tipo;
	}
}