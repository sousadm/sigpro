package com.sousa.sigpro.model.tipo;

public enum TipoSituacaoTributaria_a {
	
	s0("0", "Nacional"), 
	s1("1", "Estrangeira - imp. direta"),
	s2("2", "Estrangeira - merc.interno"),
	s3("3", "Nacional de 40% a 70% imp"),
	s4("4", "Nacional produção lei/dec"),
	s5("5", "Nacional menor 40% imp."),
	s6("6", "Estrangeira direta s/similar"),
	s7("7", "Estrangeira interna s/similar"),
	s8("8", "Nacional superior 70% imp");
	
	private String codigo;
	private String descricao;

	TipoSituacaoTributaria_a(String codigo, String descricao) {
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