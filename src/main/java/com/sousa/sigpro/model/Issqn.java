package com.sousa.sigpro.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoNaturezaOperacaoNFSe;
import com.sousa.sigpro.model.tipo.TipoRegimeEspecial;
import com.sousa.sigpro.model.tipo.TipoTributacaoRps;

@Embeddable
public class Issqn implements Serializable {

	private static final long serialVersionUID = 1L;

	private double valorServicos;
	private double aliquotaISSQN;
	private Municipio codigoMunicipio;
	private String itemListaServicos;
	private String discriminacao;
	private String codigoTributarioMunicipio;
	private String codigoCnae;
	private boolean incentivadorCultural;
	private boolean retencaoIss;
	private TipoNaturezaOperacaoNFSe naturezaOperacaoNFSe;
	private TipoRegimeEspecial regimeEspecial;
	private TipoTributacaoRps tributacaoRPS;

	public Issqn() {
		valorServicos = 0;
		aliquotaISSQN = 0;
	}

	@Transient
	public double getValorISSQN() {
		return valorServicos * aliquotaISSQN / 100;
	}

	@Column(length = 20)
	public String getCodigoCnae() {
		return codigoCnae;
	}

	public void setCodigoCnae(String codigoCnae) {
		this.codigoCnae = codigoCnae;
	}

	public boolean isRetencaoIss() {
		return retencaoIss;
	}

	public void setRetencaoIss(boolean retencaoIss) {
		this.retencaoIss = retencaoIss;
	}

	public TipoTributacaoRps getTributacaoRPS() {
		return tributacaoRPS;
	}

	public void setTributacaoRPS(TipoTributacaoRps tributacaoRPS) {
		this.tributacaoRPS = tributacaoRPS;
	}

	public TipoRegimeEspecial getRegimeEspecial() {
		return regimeEspecial;
	}

	public void setRegimeEspecial(TipoRegimeEspecial regimeEspecial) {
		this.regimeEspecial = regimeEspecial;
	}

	public TipoNaturezaOperacaoNFSe getNaturezaOperacaoNFSe() {
		return naturezaOperacaoNFSe;
	}

	public void setNaturezaOperacaoNFSe(TipoNaturezaOperacaoNFSe naturezaOperacaoNFSe) {
		this.naturezaOperacaoNFSe = naturezaOperacaoNFSe;
	}

	@Column(columnDefinition = "text")
	public String getDiscriminacao() {
		return discriminacao;
	}

	public void setDiscriminacao(String discriminacao) {
		this.discriminacao = discriminacao;
	}

	@Column(length = 20)
	public String getCodigoTributarioMunicipio() {
		return codigoTributarioMunicipio;
	}

	public void setCodigoTributarioMunicipio(String codigoTributarioMunicipio) {
		this.codigoTributarioMunicipio = codigoTributarioMunicipio;
	}

	@Column(precision = 12, scale = 2)
	public double getValorServicos() {
		return valorServicos;
	}

	public void setValorServicos(double valorServicos) {
		this.valorServicos = valorServicos;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaISSQN() {
		return aliquotaISSQN;
	}

	public void setAliquotaISSQN(double aliquotaISSQN) {
		this.aliquotaISSQN = aliquotaISSQN;
	}

	@ManyToOne
	public Municipio getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public void setCodigoMunicipio(Municipio codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public String getItemListaServicos() {
		return itemListaServicos;
	}

	public void setItemListaServicos(String itemListaServicos) {
		this.itemListaServicos = itemListaServicos;
	}

	public boolean isIncentivadorCultural() {
		return incentivadorCultural;
	}

	public void setIncentivadorCultural(boolean incentivadorCultural) {
		this.incentivadorCultural = incentivadorCultural;
	}
}