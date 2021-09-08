package com.sousa.sigpro.model.tipo;

public enum TipoAtividadePrincipal {

	CONTRATE("Principal"),
	COMERCIO("Comércio"), 
	CLINICA("Clínica Médica"),
	GRAFICA("Gráfica"),
	OFICINA("Oficina Mecânica"),
	PETSHOP("Pet Shop"),
	VEICULO("Aluguel veicular"),
	INDUSTRIA("Indústria");

	private String descricao;

	@Override
	public String toString() {
		return descricao;
	}
	
	private TipoAtividadePrincipal(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}