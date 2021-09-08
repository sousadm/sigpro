package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.repository.Contas;

@Named
@ViewScoped
public class SelecaoContaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Contas contas;

	private List<ContaCorrente> lista;

	public List<ContaCorrente> getLista() {
		return lista;
	}

	public void pesquisar() {
		lista = contas.lista();
	}

	public void pesquisarContaCheque() {
		lista = contas.listaContaCheque();
	}

	public void pesquisarContaDebito() {
		lista = contas.listaContaDebito();
	}

	public void selecionar(ContaCorrente conta) {
		try {
			ContaCorrente choosen = contas.porId(conta.getId());
			PrimeFaces.current().dialog().closeDynamic(choosen);
		} catch (Exception e) {
			lista = null;
		}
	}

}