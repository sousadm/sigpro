package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.PlanoConta;
import com.sousa.sigpro.repository.PlanoContas;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PlanoContaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private PlanoContas contas;

	private List<PlanoConta> todasContas;
	private PlanoConta contaEdicao = new PlanoConta();
	private PlanoConta contaSelecionada;

	public void prepararNovoCadastro() {
		contaEdicao = new PlanoConta();
	}

	public void salvar() {
		contas.guardar(contaEdicao);
		consultar();
		FacesUtil.addInfoMessage("Gravado com sucesso !");
		// RequestContext.getCurrentInstance().update(Arrays.asList("frm:msgs",
		// "frm:contas-table"));
	}

	public void excluir() {
		contas.remover(contaSelecionada);
		contaSelecionada = null;
		consultar();
	}

	public void consultar() {
		todasContas = contas.todas();
	}

	public PlanoConta getContaEdicao() {
		return contaEdicao;
	}

	public void setContaEdicao(PlanoConta contaEdicao) {
		this.contaEdicao = contaEdicao;
	}

	public PlanoConta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(PlanoConta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public List<PlanoConta> getTodasContas() {
		return todasContas;
	}

}
