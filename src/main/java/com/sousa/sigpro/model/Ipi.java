package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaIPI;

@Embeddable
public class Ipi implements Serializable {

	private static final long serialVersionUID = 1L;

	private double aliquotaIPI;
	private double valorIPI;
	private String codigoEnquadramento;
	private NFNotaInfoSituacaoTributariaIPI cstIPI;

	public Ipi() {
		this.aliquotaIPI = 0;
		this.valorIPI = 0;
	}

	@Column(precision = 12, scale = 2)
	public double getValorIPI() {
		return valorIPI;
	}

	public void setValorIPI(double baseIPI) {
		this.valorIPI = baseIPI;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaIPI() {
		return aliquotaIPI;
	}

	public void setAliquotaIPI(double aliquotaIPI) {
		this.aliquotaIPI = aliquotaIPI;
	}

	@Column(length = 20)
	public String getCodigoEnquadramento() {
		return codigoEnquadramento;
	}

	public void setCodigoEnquadramento(String codigoEnquadramento) {
		this.codigoEnquadramento = codigoEnquadramento;
	}

	public NFNotaInfoSituacaoTributariaIPI getCstIPI() {
		return cstIPI;
	}

	public void setCstIPI(NFNotaInfoSituacaoTributariaIPI cstIPI) {
		this.cstIPI = cstIPI;
	}

}