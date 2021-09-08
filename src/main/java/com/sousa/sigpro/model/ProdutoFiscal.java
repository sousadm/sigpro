package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMSST;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaIPI;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaPIS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaSituacaoOperacionalSimplesNacional;
import com.fincatto.documentofiscal.nfe400.classes.NFOrigem;

@Embeddable
public class ProdutoFiscal implements Serializable {

	private static final long serialVersionUID = 1L;

	private double ipi;
	private double iss;
	private double icms;
	private double icmsFora;
	private double pis;
	private double cofins;
	private NFOrigem origem;
	private NFNotaInfoImpostoTributacaoICMS cst;
	private NFNotaSituacaoOperacionalSimplesNacional cstSN;
	private NFNotaInfoItemModalidadeBCICMS modalidade;
	private NFNotaInfoItemModalidadeBCICMSST modalidadeST;
	private NFNotaInfoSituacaoTributariaPIS cstPIS;
	private NFNotaInfoSituacaoTributariaCOFINS cstCofins;
	private NFNotaInfoSituacaoTributariaIPI cstIPI;

	public ProdutoFiscal() {
		origem = NFOrigem.NACIONAL;
		icms = 0;
		icmsFora = 0;
		pis = 0;
		cofins = 0;
		ipi = 0;
		iss = 0;
	}

	@Column(precision = 12, scale = 2)
	public double getIpi() {
		return ipi;
	}

	public void setIpi(double ipi) {
		this.ipi = ipi;
	}

	@Column(precision = 12, scale = 2)
	public double getIcmsFora() {
		return icmsFora;
	}

	public void setIcmsFora(double icmsFora) {
		this.icmsFora = icmsFora;
	}

	@Column(precision = 12, scale = 2)
	public double getIss() {
		return iss;
	}

	public void setIss(double iss) {
		this.iss = iss;
	}

	@Column(precision = 12, scale = 2)
	public double getCofins() {
		return cofins;
	}

	public void setCofins(double cofins) {
		this.cofins = cofins;
	}

	public NFNotaInfoSituacaoTributariaIPI getCstIPI() {
		return cstIPI;
	}

	public void setCstIPI(NFNotaInfoSituacaoTributariaIPI cstIPI) {
		this.cstIPI = cstIPI;
	}

	public NFNotaInfoSituacaoTributariaCOFINS getCstCofins() {
		return cstCofins;
	}

	public void setCstCofins(NFNotaInfoSituacaoTributariaCOFINS cstCofins) {
		this.cstCofins = cstCofins;
	}

	public NFNotaSituacaoOperacionalSimplesNacional getCstSN() {
		return cstSN;
	}

	public void setCstSN(NFNotaSituacaoOperacionalSimplesNacional cstSN) {
		this.cstSN = cstSN;
	}

	@Column(precision = 12, scale = 2)
	public double getPis() {
		return pis;
	}

	public void setPis(double pis) {
		this.pis = pis;
	}

	public NFNotaInfoSituacaoTributariaPIS getCstPIS() {
		return cstPIS;
	}

	public void setCstPIS(NFNotaInfoSituacaoTributariaPIS cstPIS) {
		this.cstPIS = cstPIS;
	}

	@Column(precision = 12, scale = 2)
	public double getIcms() {
		return icms;
	}

	public void setIcms(double icms) {
		this.icms = icms;
	}

	public NFOrigem getOrigem() {
		return origem;
	}

	public void setOrigem(NFOrigem origem) {
		this.origem = origem;
	}

	public NFNotaInfoImpostoTributacaoICMS getCst() {
		return cst;
	}

	public void setCst(NFNotaInfoImpostoTributacaoICMS cst) {
		this.cst = cst;
	}

	public NFNotaInfoItemModalidadeBCICMSST getModalidadeST() {
		return modalidadeST;
	}

	public void setModalidadeST(NFNotaInfoItemModalidadeBCICMSST modalidadeST) {
		this.modalidadeST = modalidadeST;
	}

	public NFNotaInfoItemModalidadeBCICMS getModalidade() {
		return modalidade;
	}

	public void setModalidade(NFNotaInfoItemModalidadeBCICMS modalidade) {
		this.modalidade = modalidade;
	}
}