package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.Marca;
import com.sousa.sigpro.repository.Marcas;

@Named
@ViewScoped
public class SelecaoMarcaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Marcas marcas;
	private String condicao;
	private List<Marca> lista;

	public SelecaoMarcaBean() {
		condicao = "";
	}

	public void pesquisar() {
		lista = marcas.lista(condicao);
	}

	public void selecionar(Marca marca) {
		Marca choosen = new Marca(marca.getId(), marca.getDescricao());
		PrimeFaces.current().dialog().closeDynamic(choosen);
	}

	public String getCondicao() {
		return condicao;
	}

	public void setCondicao(String condicao) {
		this.condicao = condicao;
	}

	public List<Marca> getLista() {
		return lista;
	}

	public void setLista(List<Marca> lista) {
		this.lista = lista;
	}
}