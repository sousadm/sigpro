package com.sousa.sigpro.model.tipo;

public enum TipoComando {
	
	INICIAR("iniciar"),  
	PAUSAR("pausar"),
	REINICIAR("reiniciar"),
	ENCERRAR("encerrar");
	
	private String descricao;

	private TipoComando(String valor) {
		this.descricao = valor;
	}

	public String getDescricao() {
		return descricao;
	}

}
