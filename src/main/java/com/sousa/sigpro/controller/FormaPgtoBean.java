package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;

import org.primefaces.event.SelectEvent;

import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.FormaPgto;
import com.sousa.sigpro.model.RegraPgto;
import com.sousa.sigpro.repository.FormaPgtos;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class FormaPgtoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;

	@Inject
	private FormaPgtos formaPgtos;

	private List<FormaPgto> lista;
	private FormaPgto formaPgto;
	private RegraPgto regraPgto;

	public FormaPgtoBean() {
		novo();
	}

	public void consultar() {
		lista = formaPgtos.lista();
	}

	public void novo() {
		formaPgto = new FormaPgto();
		RegraPgto regra = new RegraPgto();
		regra.setQuantidade(1);
		regra.setDesconto(0);
		regra.setTempo(0);
		regra.setFormaPgto(formaPgto);
		formaPgto.getRegras().add(regra);
	}

	public void excluir(FormaPgto formaPgto) {
		try {
			formaPgtos.remover(formaPgto);
			consultar();
			novo();
			FacesUtil.addInfoMessage("Registro excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public RegraPgto getRegraPgto() {
		return regraPgto;
	}

	public void setRegraPgto(RegraPgto regraPgto) {
		this.regraPgto = regraPgto;
	}

	public List<FormaPgto> getLista() {
		return lista;
	}

	public FormaPgto getFormaPgto() {
		return formaPgto;
	}

	public void setFormaPgto(FormaPgto formaPgto) {
		this.formaPgto = formaPgto;
	}

	public void addRegra() {
		RegraPgto regra = new RegraPgto();
		int ind = formaPgto.getRegras().size() - 1;
		if (ind >= 0) {
			regra.setDesconto(formaPgto.getRegras().get(ind).getDesconto());
			regra.setQuantidade(formaPgto.getRegras().get(ind).getQuantidade() + 1);
			regra.setTempo(formaPgto.getRegras().get(ind).getTempo());
		} else {
			regra.setDesconto(0);
			regra.setQuantidade(0);
			regra.setTempo(0);
		}
		regra.setFormaPgto(formaPgto);
		this.formaPgto.getRegras().add(regra);
	}

	public void removerItem(int linha) {
		if (linha >= 0)
			this.formaPgto.getRegras().remove(linha);
	}

	public boolean naoMostrarItem(int linha) {
		return linha == 0;
	}

	public void onContaChosen(SelectEvent event) {
		ContaCorrente conta = (ContaCorrente) event.getObject();
		formaPgto.setConta(conta);
	}

	public void validarRegra() {
		int qt;
		if (formaPgto.getRegras() == null) {
			RegraPgto regra = new RegraPgto();
			regra.setQuantidade(1);
			regra.setDesconto(0);
			regra.setTempo(0);
			regra.setFormaPgto(formaPgto);
			formaPgto.getRegras().add(regra);
		} else {
			for (RegraPgto regra : formaPgto.getRegras()) {
				qt = 0;
				for (RegraPgto regra2 : formaPgto.getRegras()) {
					if (regra.getQuantidade() == regra2.getQuantidade()) {
						qt++;
					}
				}
				if (qt > 1) {
					throw new NegocioException("Existe item na regra de negociação com quantidades iguais.");
				}
			}
		}
	}

	public void salvar() {
		try {
			validarRegra();
			if (formaPgto.getEmpresa() == null)
				formaPgto.setEmpresa(seguranca.getPessoaLogado().getOrigem());
			formaPgto = formaPgtos.guardar(formaPgto);
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

}