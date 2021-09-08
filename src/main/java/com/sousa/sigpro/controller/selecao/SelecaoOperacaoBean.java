package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.OperacaoFiscal;
import com.sousa.sigpro.repository.OperacaoFiscais;
import com.sousa.sigpro.repository.filter.CondicaoFilter;

@Named
@ViewScoped
public class SelecaoOperacaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private OperacaoFiscais operacoes;
	private CondicaoFilter filtro;
	private List<OperacaoFiscal> lista;

	public SelecaoOperacaoBean() {
		filtro = new CondicaoFilter();
	}

	public void pesquisarOperacaoCFe() {
		lista = operacoes.listaCFe();
	}

	public void pesquisarOperacaoNFe() {
		lista = operacoes.listaNFe();
	}

	public void pesquisarOperacaoNFSe() {
		lista = operacoes.listaNFSe();
	}

	public void selecionar(OperacaoFiscal operacao) {
		try {
			OperacaoFiscal choosen = operacoes.porId(operacao.getId());
			PrimeFaces.current().dialog().closeDynamic(choosen);
		} catch (Exception e) {
			lista = null;
		}
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public List<OperacaoFiscal> getLista() {
		return lista;
	}

	public void setLista(List<OperacaoFiscal> lista) {
		this.lista = lista;
	}
}