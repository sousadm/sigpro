package com.sousa.sigpro.model.tipo;

public enum TipoRegimeEspecial {

	ME_MUNICIPAL("Microempresa municipal", 1),
	ESTIMATIVA("Estimativa", 2),
	PROFISSIONAIS("Sociedade de profissionais", 3),
	COOPERATIVA("Cooperativa", 4),
	MEI("MEI - Simples Nacional", 5),
	ME_EPP("ME EPP- Simples Nacional", 6);
	
	private String descricao;
	private int codigo;

	private TipoRegimeEspecial(String descricao, int codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public int getCodigo() {
		return codigo;
	}

}