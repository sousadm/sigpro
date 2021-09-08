package com.sousa.sigpro.repository.filter;

import java.io.Serializable;
import java.util.Date;

import com.sousa.sigpro.model.Pessoa;

public class ConsultaFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Pessoa vendedor;
	private String nome;
	private Date dataInicial;
	private Date dataFinal;

	public Pessoa getVendedor() {
		return vendedor;
	}

	public void setVendedor(Pessoa vendedor) {
		this.vendedor = vendedor;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}
