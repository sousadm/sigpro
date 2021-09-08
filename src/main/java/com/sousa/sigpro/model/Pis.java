package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaPIS;

@Embeddable
public class Pis implements Serializable {

	private static final long serialVersionUID = 1L;

	private double basePIS;
	private double aliquotaPIS;
	private double valorPIS;
	private NFNotaInfoSituacaoTributariaPIS cstPIS;

	public Pis() {
		this.basePIS = 0;
		this.aliquotaPIS = 0;
		this.valorPIS = 0;
	}

	@Column(precision = 12, scale = 2)
	public double getBasePIS() {
		return basePIS;
	}

	public void setBasePIS(double basePIS) {
		this.basePIS = basePIS;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaPIS() {
		return aliquotaPIS;
	}

	public void setAliquotaPIS(double aliquotaPIS) {
		this.aliquotaPIS = aliquotaPIS;
	}

	@Column(precision = 12, scale = 2)
	public double getValorPIS() {
		return valorPIS;
	}

	public void setValorPIS(double valorPIS) {
		this.valorPIS = valorPIS;
	}

	public NFNotaInfoSituacaoTributariaPIS getCstPIS() {
		return cstPIS;
	}

	public void setCstPIS(NFNotaInfoSituacaoTributariaPIS cstPIS) {
		this.cstPIS = cstPIS;
	}

}