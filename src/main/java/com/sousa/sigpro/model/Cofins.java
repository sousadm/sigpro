package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaCOFINS;

@Embeddable
public class Cofins implements Serializable {

	private static final long serialVersionUID = 1L;

	private double baseCofins;
	private double aliquotaCofins;
	private double valorCofins;
	private NFNotaInfoSituacaoTributariaCOFINS cstCofins;

	public Cofins() {
		this.aliquotaCofins = 0;
		this.baseCofins = 0;
		this.valorCofins = 0;
	}

	@Column(precision = 12, scale = 2)
	public double getBaseCofins() {
		return baseCofins;
	}

	public void setBaseCofins(double baseCofins) {
		this.baseCofins = baseCofins;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaCofins() {
		return aliquotaCofins;
	}

	public void setAliquotaCofins(double aliquotaCofins) {
		this.aliquotaCofins = aliquotaCofins;
	}

	@Column(precision = 12, scale = 2)
	public double getValorCofins() {
		return valorCofins;
	}

	public void setValorCofins(double valorCofins) {
		this.valorCofins = valorCofins;
	}

	public NFNotaInfoSituacaoTributariaCOFINS getCstCofins() {
		return cstCofins;
	}

	public void setCstCofins(NFNotaInfoSituacaoTributariaCOFINS cstCofins) {
		this.cstCofins = cstCofins;
	}

}