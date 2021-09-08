package com.sousa.sigpro.model.tipo;

public enum TipoDiaSemana {

	DOMINGO("Domingo", "dom"), 
	SEGUNDA("Segunda", "seg"), 
	TERCA("Terça", "ter"), 
	QUARTA("Quarta", "qua"), 
	QUINTA("Quinta", "qui"), 
	SEXTA("Sexta", "sex"), 
	SABADO("Sábado", "sab");

	private String descricao;
	private String reduzido;

	private TipoDiaSemana(String descricao, String reduzido) {
		this.descricao = descricao;
		this.reduzido = reduzido;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getReduzido() {
		return reduzido;
	}

	public static TipoDiaSemana valueOfOrdem(int valor) {
		TipoDiaSemana retorno = null;
		for (final TipoDiaSemana semana : TipoDiaSemana.values()) {
			if (semana.ordinal() == valor) {
				retorno = semana;
			}
		}
		return retorno;
	}

}
