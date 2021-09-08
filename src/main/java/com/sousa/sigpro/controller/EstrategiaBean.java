package com.sousa.sigpro.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.ValorEstrategico;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class EstrategiaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pessoas pessoas;

	@Inject
	private Seguranca seguranca;

	private Pessoa pessoa;
	private ValorEstrategico valorEstrategico;

	public void salvar() {
		try {
			pessoa = this.pessoas.guardar(this.pessoa);
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Pessoas getPessoas() {
		return pessoas;
	}

	public void setPessoas(Pessoas pessoas) {
		this.pessoas = pessoas;
	}

	public ValorEstrategico getValorEstrategico() {
		return valorEstrategico;
	}

	public void setValorEstrategico(ValorEstrategico valorEstrategico) {
		this.valorEstrategico = valorEstrategico;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			pessoa = seguranca.getPessoaLogado().getOrigem();
			valorEstrategico = new ValorEstrategico();
		}
	}

	public void excluirItem(int linha) {
		FacesUtil.addInfoMessage("rotina em desenvolvimento");
		// this.pessoa.getValores().remove(linha);
	}

	public void newItem() {
		valorEstrategico = new ValorEstrategico();
	}

	public void addItem() {
		if (valorEstrategico.getPessoa() == null) {
			valorEstrategico.setPessoa(pessoa);
			// pessoa.getValores().add(valorEstrategico);
		}
	}
}