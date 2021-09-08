package com.sousa.sigpro.model.evento;

import java.util.Date;

public class EventoHistorico {

	private Long id;
	private Date data;
	private String documento;
	private String descritivo;

	public EventoHistorico(Long id, Date data, String descritivo, String documento) {
		super();
		this.id = id;
		this.data = data;
		this.documento = documento;
		this.descritivo = descritivo;
	}

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

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

}