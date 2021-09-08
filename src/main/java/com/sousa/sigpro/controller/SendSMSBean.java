package com.sousa.sigpro.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import com.amazonaws.services.sns.model.PublishResult;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Sms;
import com.sousa.sigpro.model.evento.EventoExpedicao;
import com.sousa.sigpro.model.evento.EventoPessoa;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.SMSMensagem;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SendSMSBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;
	@Inject
	private Eventos eventos;

	private Sms sms = new Sms();

	public void enviar() {
		try {

			sms.setTelefone(Suporte.onlyNumbers(sms.getTelefone()));
			PublishResult status = SMSMensagem.Enviar(sms.numero(), sms.getMensagem());
			FacesUtil.addInfoMessage("Enviado com sucesso.");
			sms.setProtocolo(status.getMessageId());

			switch (sms.getTipoModulo()) {
			case CADASTRO:
				Pessoa pessoa = manager.find(Pessoa.class, sms.getId());
				EventoPessoa evento = new EventoPessoa(seguranca.getPessoaLogadoOrigem().getUsuario(), pessoa,
						sms.mensagemEnviada());
				evento = eventos.guardar(evento);
				break;

			case EXPEDICAO:
				Expedicao expedicao = manager.find(Expedicao.class, sms.getId());
				EventoExpedicao eventoExpedicao = new EventoExpedicao(seguranca.getPessoaLogadoOrigem().getUsuario(),
						expedicao, sms.mensagemEnviada());
				eventoExpedicao = eventos.guardar(eventoExpedicao);
				break;

			default:
				// eventos.guardar(evento, seguranca.getPessoaLogado());
				break;
			}

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Sms getSms() {
		return sms;
	}

	public void setSms(Sms sms) {
		this.sms = sms;
	}

}