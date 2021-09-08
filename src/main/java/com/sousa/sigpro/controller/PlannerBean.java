package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Planner;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Planners;
import com.sousa.sigpro.repository.filter.PlannerFilter;
import com.sousa.sigpro.util.jpa.Transactional;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PlannerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pessoas pessoas;
	@Inject
	private Planners planners;

	private PlannerFilter filtro;
	private Planner planner;
	private List<Planner> lista;

	public PlannerBean() {
		filtro = new PlannerFilter();
		limpar();
	}

	private void limpar() {
		planner = new Planner();
		planner.setData(new Date());
		planner.setQuando(new Date());
	}

	public List<Planner> getLista() {
		return lista;
	}

	public void setLista(List<Planner> lista) {
		this.lista = lista;
	}

	public Planner getPlanner() {
		return planner;
	}

	public void setPlanner(Planner planner) {
		this.planner = planner;
	}

	@Transactional
	public void salvar() {
		planner = planners.guardar(planner);
		FacesUtil.addInfoMessage("Registro salvo com sucesso!");
	}

	@Transactional
	public void excluir(Planner planner) {
		planners.remover(planner);
		pesquisar();
		FacesUtil.addInfoMessage("Registro excluído com sucesso.");
	}

	public void pesquisar() {
		lista = planners.filtrado(filtro);
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			filtro = new PlannerFilter();
		}
	}

	public List<Pessoa> completarPessoa(String nome) {
		return this.pessoas.porNome(nome);
	}

	public boolean isEditando() {
		return this.planner.getId() != null;
	}

	public PlannerFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(PlannerFilter filtro) {
		this.filtro = filtro;
	}

	public boolean isNaoPodeEnviar() {
		return filtro.getPessoa() == null;
	}

	public void enviar() {
		try {
//			MailMessage message = mailer.novaMensagem();
//
//			String mensagem = "Segue lista de atividades conforme plano de ação.";
//
//			message.to(filtro.getPessoa().getEmail()).subject("Plano de Ação")
//					.bodyHtml(new VelocityTemplate(getClass().getResourceAsStream("/emails/planner.template")))
//					.put("lista", this.lista).put("dateTool", new DateTool()).put("mensagem", mensagem)
//					.put("nome", filtro.getPessoa().getNome()).put("locale", new Locale("pt", "BR")).send();

			FacesUtil.addInfoMessage("Enviado por e-mail com sucesso!");

		} catch (Exception e) {
			FacesUtil.addInfoMessage("Erro ao enviar: " + e.getMessage());
		}
	}

	public Calendar getPrazoMinimo() {
		Calendar dia = Calendar.getInstance();
		dia.add(Calendar.DAY_OF_MONTH, 7);
		return dia;
	}

	public void solicitar() {
		try {
//			MailMessage message = mailer.novaMensagem();
//
//			String mensagem = "solicitação de ajustes: " + seguranca.getPessoaLogadoOrigem().getNome();
//
//			message.to("sousa.coach@gmail.com").subject("solicitação de ajustes")
//					.bodyHtml(new VelocityTemplate(getClass().getResourceAsStream("/emails/planner.template")))
//					.put("lista", this.lista).put("dateTool", new DateTool()).put("mensagem", mensagem)
//					.put("nome", seguranca.getPessoaLogado().getNome()).put("locale", new Locale("pt", "BR")).send();

			FacesUtil.addInfoMessage("Enviado por e-mail com sucesso!");

		} catch (Exception e) {
			FacesUtil.addInfoMessage("Erro ao enviar: " + e.getMessage());
		}
	}
}