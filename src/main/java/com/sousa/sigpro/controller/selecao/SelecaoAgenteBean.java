package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.repository.Pessoas;

@Named
@ViewScoped
public class SelecaoAgenteBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pessoas pessoas;

	private List<Pessoa> lista;

	public void pesquisar() {
		lista = pessoas.agentes();
	}

	public List<Pessoa> getLista() {
		return lista;
	}

	public void selecionar(Pessoa pessoa) {
		try {
			Pessoa choosen = pessoas.porId(pessoa.getId());
			PrimeFaces.current().dialog().closeDynamic(choosen);
		} catch (Exception e) {
			lista = null;
		}
	}

}
