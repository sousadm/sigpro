package com.sousa.sigpro.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("MODULOPAGAMENTO")
public class ModuloPagamento extends Equipamento {

	private String codigoValidacao;
	private String codigoRequisicao;

	@Column(length = 30)
	public String getCodigoRequisicao() {
		return codigoRequisicao;
	}

	public void setCodigoRequisicao(String codigoRequisicao) {
		this.codigoRequisicao = codigoRequisicao;
	}

	@Column(length = 30)
	public String getCodigoValidacao() {
		return codigoValidacao;
	}

	public void setCodigoValidacao(String codigoValidacao) {
		this.codigoValidacao = codigoValidacao;
	}

}