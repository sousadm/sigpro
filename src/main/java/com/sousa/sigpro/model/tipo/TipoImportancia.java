package com.sousa.sigpro.model.tipo;

public enum TipoImportancia {

	MUITO_BAIXO("Muito baixo", 1),
	BAIXO("Baixo", 2),
	MEDIO("Médio", 3),
	ALTO("Alto", 4),
	MUITO_ALTO("Muito alto", 5);

	private String descricao;
	private int valor;

	private TipoImportancia(String desc, int valor) {
		this.descricao = desc;
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public int getValor() {
		return valor;
	}

}