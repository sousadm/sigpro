package com.sousa.sigpro.model.tipo;

public enum TipoRepeticao {
	
	NAO("desabilitado", 0), 
	DIA("diário", 1),
	SEM("semanal", 7),
	QUI("quinzenal", 15),
	MES("mensal", 30),
	BIM("bimestral", 60),
	TRI("trimestral", 90),
	SMT("semestral", 180),
	ANO("anual", 360);
	
	private String descricao;
	private int tempo;

	private TipoRepeticao(String valor, int tempo) {
		this.descricao = valor;
		this.tempo = tempo;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getTempo() {
		return tempo;
	}
}
