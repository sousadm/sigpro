package com.sousa.sigpro.model.tipo;

public enum TipoPagamento {

	DINHEIRO("Dinheiro", false, false), 
	BOLETO("Boleto", true, true),
	CHEQUE("Cheque", true, true), 
	DEBITO("Débito", false, true),
	CREDITO("Crédito", true, true),
	BANCO("Banco", false, false),
	CAIXA("Caixa", false, false),
	DEPOSITO("Depósito", false, false),
	SAQUE("Saque", false, false);
	
	private String descricao;
	private boolean permiteNovaRegra;
	private boolean permiteEditaRegra;
	
	private TipoPagamento(String valor, boolean permiteNovaRegra, boolean permiteEditaRegra) {
		this.descricao = valor;
		this.permiteNovaRegra = permiteNovaRegra;
		this.permiteEditaRegra = permiteEditaRegra;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public boolean isPermiteNovaRegra() {
		return permiteNovaRegra;
	}

	public boolean isPermiteEditaRegra() {
		return permiteEditaRegra;
	}
	
}