package com.sousa.sigpro.model.tipo;

public enum TipoProprietario {

	AG("Agregado", 0),
	IN("Independente", 1),
	OU("Outros", 2);

	private String descricao;
	private int codigo;

	private TipoProprietario(String valor, int codigo) {
		this.descricao = valor;
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getCodigo() {
		return codigo;
	}
}