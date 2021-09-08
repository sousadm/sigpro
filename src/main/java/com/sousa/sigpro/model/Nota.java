package com.sousa.sigpro.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFIndicadorFormaPagamento;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.NFTipoImpressao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorPresencaComprador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;

@Embeddable
public class Nota {

	private String chave;
	private Integer digitoVerificador;
	private int numeroNota;
	private String naturezaOperacao;
	private String serie;
	private Date dataConfirmacao;
	private Date dataHoraEmissao;
	private Date dataHoraSaidaOuEntrada;
	private String codigoMunicipio;
	private String versaoEmissor;
	private DFUnidadeFederativa uf;
	private NFIndicadorFormaPagamento formaPagamento;
	private DFModelo modelo;
	private NFTipo tipo;
	private NFIdentificadorLocalDestinoOperacao identificadorLocalDestinoOperacao;
	private NFTipoImpressao tipoImpressao;
	private NFTipoEmissao tipoEmissao;
	private DFAmbiente ambiente;
	private NFFinalidade finalidade;
	private NFOperacaoConsumidorFinal operacaoConsumidorFinal;
	private NFIndicadorPresencaComprador indicadorPresencaComprador;
	private NFProcessoEmissor programaEmissor;
	private String xml;

	@Column(columnDefinition = "text")
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@Column(length = 44, nullable = true)
	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public Integer getDigitoVerificador() {
		return digitoVerificador;
	}

	public void setDigitoVerificador(Integer digitoVerificador) {
		this.digitoVerificador = digitoVerificador;
	}

	@Column(nullable = true)
	public int getNumeroNota() {
		return numeroNota;
	}

	public void setNumeroNota(int numeroNota) {
		this.numeroNota = numeroNota;
	}

	@Column(length = 100)
	public String getNaturezaOperacao() {
		return naturezaOperacao;
	}

	public void setNaturezaOperacao(String naturezaOperacao) {
		this.naturezaOperacao = naturezaOperacao;
	}

	@Column(length = 20)
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataConfirmacao() {
		return dataConfirmacao;
	}

	public void setDataConfirmacao(Date dataConfirmacao) {
		this.dataConfirmacao = dataConfirmacao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataHoraEmissao() {
		return dataHoraEmissao;
	}

	public void setDataHoraEmissao(Date dataHoraEmissao) {
		this.dataHoraEmissao = dataHoraEmissao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataHoraSaidaOuEntrada() {
		return dataHoraSaidaOuEntrada;
	}

	public void setDataHoraSaidaOuEntrada(Date dataHoraSaidaOuEntrada) {
		this.dataHoraSaidaOuEntrada = dataHoraSaidaOuEntrada;
	}

	@Column(length = 7)
	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	@Column(length = 20)
	public String getVersaoEmissor() {
		return versaoEmissor;
	}

	public void setVersaoEmissor(String versaoEmissor) {
		this.versaoEmissor = versaoEmissor;
	}

	public DFUnidadeFederativa getUf() {
		return uf;
	}

	public void setUf(DFUnidadeFederativa uf) {
		this.uf = uf;
	}

	public NFIndicadorFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(NFIndicadorFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public DFModelo getModelo() {
		return modelo;
	}

	public void setModelo(DFModelo modelo) {
		this.modelo = modelo;
	}

	public NFTipo getTipo() {
		return tipo;
	}

	public void setTipo(NFTipo tipo) {
		this.tipo = tipo;
	}

	public NFIdentificadorLocalDestinoOperacao getIdentificadorLocalDestinoOperacao() {
		return identificadorLocalDestinoOperacao;
	}

	public void setIdentificadorLocalDestinoOperacao(
			NFIdentificadorLocalDestinoOperacao identificadorLocalDestinoOperacao) {
		this.identificadorLocalDestinoOperacao = identificadorLocalDestinoOperacao;
	}

	public NFTipoImpressao getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(NFTipoImpressao tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public NFTipoEmissao getTipoEmissao() {
		return tipoEmissao;
	}

	public void setTipoEmissao(NFTipoEmissao tipoEmissao) {
		this.tipoEmissao = tipoEmissao;
	}

	public DFAmbiente getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(DFAmbiente ambiente) {
		this.ambiente = ambiente;
	}

	public NFFinalidade getFinalidade() {
		return finalidade;
	}

	public void setFinalidade(NFFinalidade finalidade) {
		this.finalidade = finalidade;
	}

	public NFOperacaoConsumidorFinal getOperacaoConsumidorFinal() {
		return operacaoConsumidorFinal;
	}

	public void setOperacaoConsumidorFinal(NFOperacaoConsumidorFinal operacaoConsumidorFinal) {
		this.operacaoConsumidorFinal = operacaoConsumidorFinal;
	}

	public NFIndicadorPresencaComprador getIndicadorPresencaComprador() {
		return indicadorPresencaComprador;
	}

	public void setIndicadorPresencaComprador(NFIndicadorPresencaComprador indicadorPresencaComprador) {
		this.indicadorPresencaComprador = indicadorPresencaComprador;
	}

	public NFProcessoEmissor getProgramaEmissor() {
		return programaEmissor;
	}

	public void setProgramaEmissor(NFProcessoEmissor programaEmissor) {
		this.programaEmissor = programaEmissor;
	}

}