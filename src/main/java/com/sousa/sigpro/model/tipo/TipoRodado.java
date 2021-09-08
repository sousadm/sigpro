package com.sousa.sigpro.model.tipo;

public enum TipoRodado {

	TR("Truck", 1),
	TC("Toco", 2),
	CM("Cavalo Mecânico", 3),
	VN("VAN", 4),
	UT("Utilitário", 5),
	OU("Outros", 6);

	private String descricao;
	private int codigo;

	private TipoRodado(String valor, int codigo) {
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