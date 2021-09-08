package com.sousa.sigpro.model.tipo;

public enum TipoNaturezaOperacaoNFSe {

	TRIBUTO_NO_MUNICIPIO("Tributação no município", 1), 
	TIBUTA_FORA("Tributação fora do município", 2), 
	ISENTO("Isenção", 3), 
	IMUNE("Imune", 4), 
	SUSPENSO_JUDICIAL("Suspensa por decisão judicial", 5), 
	SUSPENSO_ADM("Suspensa por procedimento administrativo", 6);

	private String descricao;
	private int codigo;

	TipoNaturezaOperacaoNFSe(String descricao, int codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getCodigo() {
		return codigo;
	}

    public static TipoNaturezaOperacaoNFSe valueOfCodigo(final int codigo) {
        for (final TipoNaturezaOperacaoNFSe natureza : TipoNaturezaOperacaoNFSe.values()) {
            if (natureza.getCodigo() == codigo) {
                return natureza;
            }
        }
        return null;
    }
    
}