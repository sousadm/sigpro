package com.sousa.sigpro.controller;

import com.sousa.sigpro.model.Expedicao;

public class ExpedicaoModificadoEvent {

	private Expedicao expedicao;
	
	public ExpedicaoModificadoEvent(Expedicao expedicao) {
		this.expedicao = expedicao;
	}
	
	public Expedicao getExpedicao() {
		return expedicao;
	}
}
