package com.sousa.sigpro.model.tipo;

public enum TipoEspecialidade {

	OFICINA("Oficina Mecânica"),
	ALUGUEL_VEICULO("Aluguel de Veículos"),
	PET_SHOP("Petshop");

	private String descricao;

	private TipoEspecialidade(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return descricao;
	}
}