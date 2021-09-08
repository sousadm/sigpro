package com.sousa.sigpro.model;

import java.io.Serializable;

public class Remuneracao implements Serializable {

	private static final long serialVersionUID = 1L;

	private ExpedicaoItem expedicaoItem;
	private Producao producao;

	public ExpedicaoItem getExpedicaoItem() {
		return expedicaoItem;
	}

	public void setExpedicaoItem(ExpedicaoItem expedicaoItem) {
		this.expedicaoItem = expedicaoItem;
	}

	public Producao getProducao() {
		return producao;
	}

	public void setProducao(Producao producao) {
		this.producao = producao;
	}

}