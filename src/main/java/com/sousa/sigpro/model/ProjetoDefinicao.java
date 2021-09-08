package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProjetoDefinicao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(columnDefinition = "text")
	private String sobreEntrega;
	
	@Column(columnDefinition = "text")
	private String sobrePremissa;
	
	@Column(columnDefinition = "text")
	private String sobreRestricao;
	
	@Column(columnDefinition = "text")
	private String sobreMarco;
	
	@Column(columnDefinition = "text")
	private String sobreParticipante;
	
	@Column(columnDefinition = "text")
	private String sobreRisco;

	public String getSobreEntrega() {
		return sobreEntrega;
	}

	public void setSobreEntrega(String sobreEntrega) {
		this.sobreEntrega = sobreEntrega;
	}

	public String getSobrePremissa() {
		return sobrePremissa;
	}

	public void setSobrePremissa(String sobrePremissa) {
		this.sobrePremissa = sobrePremissa;
	}

	public String getSobreRestricao() {
		return sobreRestricao;
	}

	public void setSobreRestricao(String sobreRestricao) {
		this.sobreRestricao = sobreRestricao;
	}

	public String getSobreMarco() {
		return sobreMarco;
	}

	public void setSobreMarco(String sobreMarco) {
		this.sobreMarco = sobreMarco;
	}

	public String getSobreParticipante() {
		return sobreParticipante;
	}

	public void setSobreParticipante(String sobreParticipante) {
		this.sobreParticipante = sobreParticipante;
	}

	public String getSobreRisco() {
		return sobreRisco;
	}

	public void setSobreRisco(String sobreRisco) {
		this.sobreRisco = sobreRisco;
	}

}