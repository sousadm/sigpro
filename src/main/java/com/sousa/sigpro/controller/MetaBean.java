package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Meta;
import com.sousa.sigpro.model.tipo.TipoMeta;
import com.sousa.sigpro.repository.Metas;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class MetaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private Metas metas;

	private TipoMeta tipo;
	private int ano;
	private Meta meta;
	private List<Meta> lista;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			ano = GregorianCalendar.getInstance().get(Calendar.YEAR);
			consultar();
		}
	}

	public void salvar() {
		try {
			for (Meta meta : lista) {
				if (meta.getValor() > 0) {
					meta.setTipo(tipo);
					meta = metas.guardar(meta);
				}
			}
			consultar();
			FacesUtil.addInfoMessage("Gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void consultar() {
		lista = new ArrayList<Meta>();
		for (int mes = 0; mes < 12; mes++) {
			meta = metas.pesquisa(seguranca.getPessoaLogado(), tipo, ano, mes);
			lista.add(meta);
		}
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public List<Meta> getLista() {
		return lista;
	}

	public void setLista(List<Meta> listaFaturamento) {
		this.lista = listaFaturamento;
	}

	public TipoMeta getTipo() {
		return tipo;
	}

	public void setTipo(TipoMeta tipo) {
		this.tipo = tipo;
	}

	public String getPessoa() {
		return seguranca.getPessoaLogadoOrigem().getNome();
	}
}