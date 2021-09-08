package com.sousa.sigpro.model;

import com.sousa.sigpro.model.tipo.TipoModulo;

public class Sms {

	private static String ddi = "55";
	private String ddd;
	private String telefone;
	private String mensagem;
	private Long id;
	private TipoModulo tipoModulo;
	private String protocolo;

	public String numero() {
		return "+" + ddi + ddd + telefone;
	}

	public String mensagemEnviada() {
		return numero() + ":" + mensagem + " /" + protocolo;
	}

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoModulo getTipoModulo() {
		return tipoModulo;
	}

	public void setTipoModulo(TipoModulo tipoModulo) {
		this.tipoModulo = tipoModulo;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

}