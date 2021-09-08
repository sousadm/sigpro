package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.repository.Bancos;

@Named
@ViewScoped
public class SelecaoBancoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Bancos bancos;
	private String condicao;
	private List<Banco> lista;

	public void pesquisar() {
		lista = bancos.lista(condicao);
	}

	public String getCondicao() {
		return condicao;
	}

	public void setCondicao(String condicao) {
		this.condicao = condicao;
	}

	public List<Banco> getLista() {
		return lista;
	}

	public void selecionar(Banco banco) {
		PrimeFaces.current().dialog().closeDynamic(banco);
	}

}