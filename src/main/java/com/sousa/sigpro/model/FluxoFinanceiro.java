package com.sousa.sigpro.model;

import java.io.Serializable;

public class FluxoFinanceiro implements Serializable {

	private static final long serialVersionUID = 1L;

	private String descricao;
	private String grupamento;
	private boolean fechaLinha;
	double[] valores;

	public FluxoFinanceiro(String descricao, String grupamento, int qt) {
		this.descricao = descricao;
		this.grupamento = grupamento;
		valores = new double[qt];
	}

	public double total() {
		double valor = 0;
		for (int i = 0; i <= valores.length; i++) {
			valor += valores[i];
		}
		return valor;
	}

	public double media() {

		double valor = 0;
		int qt = 0;

		for (int i = 0; i <= valores.length; i++) {
			if (valores[i] > 0) {
				qt += 1;
				valor += valores[i];
			}
		}

		return qt > 0 ? valor / qt : 0;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double[] getValores() {
		return valores;
	}

	public void setValores(double[] valores) {
		this.valores = valores;
	}

	public String getGrupamento() {
		return grupamento;
	}

	public void setGrupamento(String grupamento) {
		this.grupamento = grupamento;
	}

	public boolean isFechaLinha() {
		return fechaLinha;
	}

	public void setFechaLinha(boolean fechaLinha) {
		this.fechaLinha = fechaLinha;
	}

}