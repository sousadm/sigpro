package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.service.TabelaOcupacaoService;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SelecaoOcupacaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String descricao;
	private List<Profissao> lista;
	TabelaOcupacaoService servico;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			servico = new TabelaOcupacaoService();
		}
	}

	public void pesquisar() throws UnsupportedEncodingException {
		lista = servico.listaOcupacao(descricao, 0);
	}

	public void selecionar(Profissao profissao) {
		PrimeFaces.current().dialog().closeDynamic(profissao);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Profissao> getLista() {
		return lista;
	}

}
