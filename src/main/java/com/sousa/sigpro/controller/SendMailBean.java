package com.sousa.sigpro.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.Email;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.SendMail;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SendMailBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ParametroMail param;
	@Inject
	private Seguranca seguranca;

	private Email email = new Email();

	public void enviar() {
		try {
			param.ler();
			SendMail sm = new SendMail(param);
			if (email.getRemetenteNome() == null)
				email.setRemetenteNome(seguranca.getPessoaLogado().getNome());
			sm.enviar(email);
			FacesUtil.addInfoMessage("Enviado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public void prepara() {
		param.ler();
		email.setConteudo(param.getAssinatura());
	}
}