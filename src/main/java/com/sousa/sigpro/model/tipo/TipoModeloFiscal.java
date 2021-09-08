package com.sousa.sigpro.model.tipo;

public enum TipoModeloFiscal {
	
	NFE("55", "NF-e", "/v2/nfe"), 
	NFCE("65", "NFC-e", "/v2/nfce"),
	NFSE("", "NFS-e", "/v2/nfse"),
	MDFE("58", "MDF-e", ""),
	CFE("", "CFe", "");

	private final String codigo;
	private final String descricao;
	private final String url;

	TipoModeloFiscal(final String codigo, final String descricao, String url) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.url = url;
    }

	public String getCodigo() {
		return this.codigo;
	}

	public String getUrl() {
		return url;
	}
	
	public static TipoModeloFiscal valueOfCodigo(final String codigo) {
		for (final TipoModeloFiscal tipo : TipoModeloFiscal.values()) {
			if (tipo.getCodigo().equals(codigo)) {
				return tipo;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return codigo + " - " + descricao;
	}
}
