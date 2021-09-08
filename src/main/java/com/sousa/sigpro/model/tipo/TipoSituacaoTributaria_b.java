package com.sousa.sigpro.model.tipo;

public enum TipoSituacaoTributaria_b {
	
	s00("00", "Tributada integralmente"),
	s10("10", "Tributada e com cobrança do ICMS por substituição tributária"),
	s20("20", "Com redução de base de cálculo"),
	s30("30", "Isenta ou não tributada e com cobrança do ICMS por substituição tributária"),
	s40("40", "Isenta"),
	s41("41", "Não tributada"),  
	s50("50", "Suspensão"),  
	s51("51", "Diferimento"),  
	s60("60", "ICMS cobrado anteriormente por substituição tributária"),  
	s70("70", "Com redução de base de cálculo e cobrança do ICMS por substituição tributária"),  
	s90("90", "Outros");  	
	
	private String codigo;
	private String descricao;

	TipoSituacaoTributaria_b(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getCodigo() {
		return codigo;
	}
}