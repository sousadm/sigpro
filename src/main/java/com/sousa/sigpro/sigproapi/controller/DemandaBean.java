package com.sousa.sigpro.sigproapi.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.velocity.VelocityContext;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.Email;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.sigproapi.domain.Demanda;
import com.sousa.sigpro.sigproapi.repository.Demandas;
import com.sousa.sigpro.util.SendMail;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class DemandaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ParametroMail param;
	@Inject
	private Seguranca seguranca;

	private Demandas demandas;
	private Demanda demanda = new Demanda();
	private List<Demanda> lista;

	public DemandaBean() {
		demandas = new Demandas();
	}
	
	public void pesquisar() {
		lista = demandas.lista();
	}

	public void preencherListaAberta() {
		lista = demandas.lista();
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {

		}
	}

	public Demanda getDemanda() {
		return demanda;
	}

	public void limpar() {
		demanda = new Demanda();
		demanda.setNome(Suporte.USUARIO_LOGADO);
		demanda.setEmail(seguranca.getPessoaLogado().getEmail());
		demanda.setTelefone(seguranca.getPessoaLogado().getCelular());
	}

	public void salvar() {

		try {
			if (demanda.getDataEncerramento() != null)
				throw new NegocioException("registro está encerrado");
			demanda = demandas.salvar(demanda);
			FacesUtil.addInfoMessage("gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void setDemanda(Demanda demanda) {
		this.demanda = demanda;
	}

	public List<Demanda> getLista() {
		return lista;
	}

	public void EnviarEmail() {
		try {

			VelocityContext context = new VelocityContext();
			context.put("demanda", demanda.getDescritivo());
			context.put("nome", demanda.getNome());
			context.put("telefone", demanda.getTelefone());
			context.put("email", demanda.getEmail());
			context.put("data", SuporteData.formataDataToStr(new Date(), ""));

			Email email = new Email();
			email.setAssunto(demanda.getResumo());
			email.setConteudo(seguranca.conteudoTemplate("demanda.template", context));
			email.setDestinatarioMail("sousa.coach@gmail.com");
			email.setRemetenteNome(demanda.getNome());
			email.setDestinatarioNome("FRANCISCO SOUSA");

			param.ler();
			SendMail sm = new SendMail(param);
			sm.enviar(email);

			// demanda = demandas.guardar(demanda);

			FacesUtil.addInfoMessage("Enviado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}