package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.CaixaTitulo;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.repository.Caixas;

@Named
@ViewScoped
public class TituloHistoricoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Caixas caixas;
	private double total;
	private List<CaixaTitulo> historico;

	public TituloHistoricoBean() {

	}

	public void listar(Titulo titulo) {
		total = 0;
		historico = caixas.movimentoPorTitulo(titulo);
		for (CaixaTitulo cx : historico) {
			total = total + cx.getValorPago();
		}
	}

	public List<CaixaTitulo> getHistorico() {
		return historico;
	}

	public double getTotal() {
		return total;
	}
}