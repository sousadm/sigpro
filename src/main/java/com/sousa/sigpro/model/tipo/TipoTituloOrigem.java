package com.sousa.sigpro.model.tipo;

public enum TipoTituloOrigem {

	PAGAR("conta a pagar", "Contas Pagas", "Pagamento", "Pago", "D", 2, -1), 
	RECEBER("conta a receber", "Contas Recebidas", "Recebimento", "Recebido", "C", 1, 1), 
	COMPENSA("Compensação", "Compensação", "Compensação", "Compensação", "X", 0, 0);

	private String realizado;
	private String descricao;
	private String titulo;
	private String resumido;
	private String sigla;
	private int ordem;
	private int fator;

	private TipoTituloOrigem(String valor, String realizado, String titulo, String resumido, String sigla, int ordem,
			int fator) {
		this.realizado = realizado;
		this.descricao = valor;
		this.titulo = titulo;
		this.resumido = resumido;
		this.sigla = sigla;
		this.ordem = ordem;
		this.fator = fator;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getResumido() {
		return resumido;
	}

	public String getSigla() {
		return sigla;
	}

	public int getOrdem() {
		return ordem;
	}

	public int getFator() {
		return fator;
	}

	public String getRealizado() {
		return realizado;
	}
}