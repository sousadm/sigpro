package com.sousa.sigpro.model.tipo;

public enum TipoDocumentoFiscal {
	
	d01("01", "Nota Fiscal"),
	d1B("1B", "Nota Fiscal Avulsa"),
	d02("02", "Nota Fiscal de Venda a Consumidor"),
	d2D("2D", "Cupom Fiscal"),
	d2E("2E", "Cupom Fiscal Bilhete de Passagem"),
	d04("04", "Nota Fiscal de Produtor"),
	d06("06", "Nota Fiscal/Conta de Energia Elétrica"),
	d07("07", "Nota Fiscal de Serviço de Transporte"),
	d08("08", "Conhecimento de Transporte Rodoviário de Cargas"),
	d8B("8B", "Conhecimento de Transporte de Cargas Avulso"),
	d09("09", "Conhecimento de Transporte Aquaviário de Cargas"),
	d10("10", "Conhecimento Aéreo"),
	d11("11", "Conhecimento de Transporte Ferroviário de Cargas"),
	d13("13", "Bilhete de Passagem Rodoviário"),
	d14("14", "Bilhete de Passagem Aquaviário"),
	d15("15", "Bilhete de Passagem e Nota de Bagagem"),
	d16("16", "Bilhete de Passagem Ferroviário"),
	d18("18", "Resumo de Movimento Diário"),
	d21("21", "Nota Fiscal de Serviço de Comunicação"),
	d22("22", "Nota Fiscal de Serviço de Telecomunicação"),
	d26("26", "Conhecimento de Transporte Multimodal de Cargas"),
	d27("27", "Nota Fiscal De Transporte Ferroviário De Carga"),
	d28("28", "Nota Fiscal/Conta de Fornecimento de Gás Canalizado"),
	d29("29", "Nota Fiscal/Conta de Fornecimento de Água Canalizada"),
	d55("55", "Nota Fiscal Eletrônica – NF-e"),
	d57("57", "Conhecimento de Transporte Eletrônico - CT-e"),
	d59("59", "Cupom Fiscal Eletrônico – CF-e-SAT"),
	d60("60", "Cupom Fiscal Eletrônico  CF-e-ECF"),
	d65("65", "Nota Fiscal Eletrônica  para  Consumidor Final – NFC-e");
	
	private String codigo;
	private String descricao;

	TipoDocumentoFiscal(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getCodigo() {
		return codigo;
	}
}