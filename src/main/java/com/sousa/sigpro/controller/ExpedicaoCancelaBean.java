package com.sousa.sigpro.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.evento.EventoExpedicao;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.ExpedicaoCancelaService;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@RequestScoped
public class ExpedicaoCancelaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ExpedicaoCancelaService expedicaoCancelaService;
	@Inject
	private Event<ExpedicaoModificadoEvent> expedicaoModificadoEvent;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Eventos eventos;

	@Inject
	@ExpedicaoEdicao
	private Expedicao expedicao;

	public void cancelarExpedicao() {
		this.expedicao = this.expedicaoCancelaService.cancelar(this.expedicao);
		this.expedicaoModificadoEvent.fire(new ExpedicaoModificadoEvent(this.expedicao));
		eventos.guardar(new EventoExpedicao(seguranca.getPessoaLogadoOrigem().getUsuario(), expedicao,
				"cancelamento de pedido " + expedicao.getId()));
		FacesUtil.addInfoMessage("Cancelado com sucesso.");
	}

}
