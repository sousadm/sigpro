package com.sousa.sigpro.model;

import java.io.Serializable;

public class Email implements Serializable {

	private static final long serialVersionUID = 1L;

	private String remetenteNome;
	private String destinatarioNome;
	private String destinatarioMail;
	private String assunto;
	private String conteudo;
	private String anexo;

	public String getRemetenteNome() {
		return remetenteNome;
	}

	public void setRemetenteNome(String remetenteNome) {
		this.remetenteNome = remetenteNome;
	}

	public String getDestinatarioNome() {
		return destinatarioNome;
	}

	public void setDestinatarioNome(String destinatarioNome) {
		this.destinatarioNome = destinatarioNome;
	}

	public String getDestinatarioMail() {
		return destinatarioMail;
	}

	public void setDestinatarioMail(String destinatarioMail) {
		this.destinatarioMail = destinatarioMail;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getAnexo() {
		return anexo;
	}

	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
}