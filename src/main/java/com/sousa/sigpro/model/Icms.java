package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaSituacaoOperacionalSimplesNacional;

@Embeddable
public class Icms implements Serializable {

	private static final long serialVersionUID = 1L;

	private double baseICMS;
	private double aliquotaICMS;
	private double valorICMS;
	private double aliquotaReducao;
	private double aliquotaMVA;
	private double baseIcmsST;
	private double valorIcmsST;
	private double aliquotaICMSIntra;
	private double margemValorICMSST;
	private NFNotaInfoImpostoTributacaoICMS cst;
	private NFNotaSituacaoOperacionalSimplesNacional cstSN;
	private NFNotaInfoItemModalidadeBCICMS modalidade;

	public Icms() {
		modalidade = NFNotaInfoItemModalidadeBCICMS.VALOR_OPERACAO;
		aliquotaICMSIntra = 0;
		aliquotaMVA = 0;
		baseIcmsST = 0;
		valorIcmsST = 0;
		valorICMS = 0;
		aliquotaICMS = 0;
		valorICMS = 0;
		margemValorICMSST = 0;
	}

	@Transient
	public boolean isProdutoSubstituicaoTributaria() {
		return cst != null && (cst.equals(
				NFNotaInfoImpostoTributacaoICMS.COM_REDUCAO_BASE_CALCULO_COBRANCA_ICMS_POR_SUBSTITUICAO_TRIBUTARIA_ICMS_SUBSTITUICAO_TRIBUTARIA)
				|| cst.equals(
						NFNotaInfoImpostoTributacaoICMS.ISENTA_OU_NAO_TRIBUTADA_COM_COBRANCA_ICMS_POR_SUBSTITUICAO_TRIBUTARIA)
				|| cst.equals(NFNotaInfoImpostoTributacaoICMS.TRIBUTADA_COM_COBRANCA_ICMS_POR_SUBSTITUICAO_TRIBUTARIA));
	}

	@Transient
	public boolean isProdutoIsentoNaoTributado() {
		return cst != null && (cst.equals(
				NFNotaInfoImpostoTributacaoICMS.ISENTA_OU_NAO_TRIBUTADA_COM_COBRANCA_ICMS_POR_SUBSTITUICAO_TRIBUTARIA)
				|| cst.equals(NFNotaInfoImpostoTributacaoICMS.ICMS_COBRADO_ANTERIORMENTE_POR_SUBSTITUICAO_TRIBUTARIA)
				|| cst.equals(NFNotaInfoImpostoTributacaoICMS.NAO_TRIBUTADO)
				|| cst.equals(NFNotaInfoImpostoTributacaoICMS.ISENTA)
				|| cst.equals(NFNotaInfoImpostoTributacaoICMS.SUSPENSAO)
				|| cst.equals(NFNotaInfoImpostoTributacaoICMS.DIFERIMENTO));
	}

	@Transient
	public boolean isProdutoReducaoBaseCalculo() {
		return cst != null && (cst.equals(NFNotaInfoImpostoTributacaoICMS.COM_REDUCAO_BASE_CALCULO) || cst.equals(
				NFNotaInfoImpostoTributacaoICMS.COM_REDUCAO_BASE_CALCULO_COBRANCA_ICMS_POR_SUBSTITUICAO_TRIBUTARIA_ICMS_SUBSTITUICAO_TRIBUTARIA));
	}

	@Column(precision = 12, scale = 2)
	public double getMargemValorICMSST() {
		return margemValorICMSST;
	}

	public void setMargemValorICMSST(double margemValorICMSST) {
		this.margemValorICMSST = margemValorICMSST;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaICMSIntra() {
		return aliquotaICMSIntra;
	}

	public void setAliquotaICMSIntra(double aliquotaICMSIntra) {
		this.aliquotaICMSIntra = aliquotaICMSIntra;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaMVA() {
		return aliquotaMVA;
	}

	public void setAliquotaMVA(double aliquotaMVA) {
		this.aliquotaMVA = aliquotaMVA;
	}

	@Column(precision = 12, scale = 2)
	public double getBaseIcmsST() {
		return baseIcmsST;
	}

	public void setBaseIcmsST(double baseIcmsST) {
		this.baseIcmsST = baseIcmsST;
	}

	@Column(precision = 12, scale = 2)
	public double getValorIcmsST() {
		return valorIcmsST;
	}

	public void setValorIcmsST(double valorIcmsST) {
		this.valorIcmsST = valorIcmsST;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaReducao() {
		return aliquotaReducao;
	}

	public void setAliquotaReducao(double aliquotaReducao) {
		this.aliquotaReducao = aliquotaReducao;
	}

	@Column(precision = 12, scale = 2)
	public double getBaseICMS() {
		return baseICMS;
	}

	public void setBaseICMS(double baseICMS) {
		this.baseICMS = baseICMS;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaICMS() {
		return aliquotaICMS;
	}

	public void setAliquotaICMS(double aliquotaICMS) {
		this.aliquotaICMS = aliquotaICMS;
	}

	@Column(precision = 12, scale = 2)
	public double getValorICMS() {
		return valorICMS;
	}

	public void setValorICMS(double valorICMS) {
		this.valorICMS = valorICMS;
	}

	public NFNotaSituacaoOperacionalSimplesNacional getCstSN() {
		return cstSN;
	}

	public void setCstSN(NFNotaSituacaoOperacionalSimplesNacional cstSN) {
		this.cstSN = cstSN;
	}

	public NFNotaInfoImpostoTributacaoICMS getCst() {
		return cst;
	}

	public void setCst(NFNotaInfoImpostoTributacaoICMS cst) {
		this.cst = cst;
	}

	public NFNotaInfoItemModalidadeBCICMS getModalidade() {
		return modalidade;
	}

	public void setModalidade(NFNotaInfoItemModalidadeBCICMS modalidade) {
		this.modalidade = modalidade;
	}
}