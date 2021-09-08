package com.sousa.sigpro.model.tipo;

public enum TipoCarroceria {

	NA("não aplicável", 0),
	AB("Aberta", 1),
	FB("Fechada/Baú", 2),
	GR("Granelera", 3),
	PC("Porta Container", 4),
	SD("Sider", 5);
	
	private String descricao;
	private int codigo;

	private TipoCarroceria(String valor, int codigo) {
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