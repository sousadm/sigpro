package com.sousa.sigpro.model.tipo;

public enum TipoStakeHolder {

	RESPONSAVEL("Gerente do projeto"),
	COORDENARDOR("Coordenador"),
	PROPRIETARIO("Responsável financeiro"),
	MEMBRO("Membro de equipe"),
	FORNECEDOR("Fornecedor de insumos"),
	CLIENTE("Cliente"),
	CONCORRENTE("Concorrente"),
	EXTERNO("Agente externo"),
	ESTADO("Agente governamental"),
	ONG("Organização não governamental"),
	MIDIA("Mídia"),
	SINDICATO("Sindicato"),
	COMUNIDADE("Agente comunitário"),
	FUNCIONARIO("Funcionário");
	
	String descricao;
	
	private TipoStakeHolder(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public String toString() {
		return descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
}
