package com.sousa.sigpro.model.tipo;

public enum TipoUnidade {

	AMPOLA("ampola", "ampola", 0),
	BALDE("balde", "balde", 0),
	BANDEJ("bandeja", "bandeja", 0),
	BARRA("barra", "barra", 0),
	BISNAG("bisnaga", "bisnaga", 0),
	BLOCO("bloco", "bloco", 0),
	BOBINA("BOBINA", "bobina", 0),
	BOMB("bomb", "bombona", 0),
	CAPS("caps", "capsula", 0),
	CART("cart", "cartela", 0),
	CENTO("cento", "cento", 0),
	CJ("CJ", "conjunto", 0),
	CM("CM", "centimetro", 2),
	CM2("CM2", "centimetro quadrado", 2),
	CX("CX", "caixa", 0),
	CX2("CX2", "caixa com 2 unidades", 0),
	CX3("CX3", "caixa com 3 unidades", 0),
	CX5("CX5", "caixa com 5 unidades", 0),
	CX10("CX10", "caixa com 10 unidades", 0),
	CX15("CX15", "caixa com 15 unidades", 0),
	CX20("CX20", "caixa com 20 unidades", 0),
	CX25("CX25", "caixa com 25 unidades", 0),
	CX50("CX50", "caixa com 50 unidades", 0),
	CX100("CX100", "caixa com 100 unidades", 0),
	DISP("DISP", "display", 0),
	DUZIA("DUZIA", "duzia", 1),
	EMBAL("EMBAL", "embalagem", 0),
	FARDO("FARDO", "fardo", 0),
	FOLHA("FOLHA", "folha", 0),
	FRASCO("FRASCO", "frasco", 0),
	GALAO("GALAO", "galão", 0),
	GF("GF", "garrafa", 0),
	GRAMAS("GRAMAS", "gramas", 3),
	JOGO("JOGO", "jogo", 0),
	KIT("KIT", "kit", 0),
	LATA("LATA", "lata", 1),
	LITRO("LITRO", "litro", 1),
	M("M", "metro", 2),
	M2("m2", "metro quadrado", 2),
	M3("m3", "metro cúbico", 3),	
	MILHEI("mil", "milheiro", 2),
	ML("ML", "mililitro", 3),
	MWH("MWH", "megawatt hora", 3),
	PACOTE("PCT", "pacote", 0),
	PALETE("PLT", "palete", 0),
	PARES("PAR", "pares", 0),
	PC("PC", "peça", 0),
	POTE("POTE", "pote", 0),
	K("K", "quilate", 0),
	KG("kg", "quilograma", 3),
	RESMA("RESMA", "resma", 0),
	ROLO("ROLO", "rolo", 0),
	SACO("SACO", "saco", 0),
	SACOLA("SACOLA", "sacola", 0),
	TAMBOR("TAMBOR", "tambor", 0),
	TANQUE("TANQUE", "tanque", 0),
	TON("ton", "tonelada", 2),
	TUBO("TUBO", "tubo", 0),
	UND("unid", "unidade", 1),	
	VASIL("VASIL", "vasilhame", 0),
	VIDRO("VIDRO", "vidro", 0);
	
	private String codigo;
	private String descricao;
	private int precisao;

	private TipoUnidade(String codigo, String descricao, int precisao) {
		this.descricao = descricao;
		this.codigo = codigo;
		this.precisao = precisao;

	}

	public String getCodigo() {
		return codigo;
	}
 
	public String getDescricao() {
		return descricao;
	}

	public int getPrecisao() {
		return precisao;
	}
}