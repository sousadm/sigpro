package com.sousa.sigpro.model.tipo;

public enum TipoFocusStatus {

	PROCESSANDO("processando_autorizacao", "Em processo"), AUTORIZADO("autorizado", "Autorizado"),
	CANCELADO("cancelado", "Cancelado"), ERRO_SEFAZ("erro_autorizacao", "Erro de autorização"),
	DENEGADO("denegado", "Denegado");

	private final String codigo;
	private final String descricao;

	private TipoFocusStatus(final String codigo, final String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public static TipoFocusStatus valueOfCodigo(final String codigo) {
		for (final TipoFocusStatus tipo : TipoFocusStatus.values()) {
			if (tipo.getCodigo().equals(codigo)) {
				return tipo;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.descricao;
	}
}