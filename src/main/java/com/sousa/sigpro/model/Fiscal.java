package com.sousa.sigpro.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFIndicadorFormaPagamento;
import com.fincatto.documentofiscal.nfe400.classes.NFModalidadeFrete;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorPresencaComprador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;
import com.sousa.sigpro.model.tipo.TipoFocusStatus;
import com.sousa.sigpro.model.tipo.TipoModeloFiscal;

@Embeddable
public class Fiscal {

	private String chave;
	private String chaveRef;
	private Long numero;
	private Long codigoUnico;
	private int serie;
	private Date dataEmissao;
	private Date dataCancelamento;
	private Date impressao;
	private String observacao;
	private String justificativa;
	private String mensagemSefaz;
	private String pdfFile;
	private String xmlFile;
	private String caminhoXmlCancelamento;
	private String caminhoXmlCarta;
	private String caminhoPdfCarta;
	private Long statusSefaz;
	private Long codigoVerificacao;

	private OperacaoFiscal operacao;
	private Cliente cliente;
	private Endereco enderecoEntrega;
	private TipoModeloFiscal modelo;
	private NFTipo tipo;
	private NFIndicadorFormaPagamento formaPagamento;
	private NFTipoEmissao tipoEmissao;
	private DFAmbiente ambiente;
	private NFFinalidade finalidade;
	private NFProcessoEmissor programaEmissor;
	private NFIdentificadorLocalDestinoOperacao localDestinoOperacao;
	private NFOperacaoConsumidorFinal consumidorFinal;
	private NFIndicadorPresencaComprador presencaComprador;
	private NFModalidadeFrete modalidadeFrete;
	private TipoFocusStatus status;

	public Fiscal() {
		status = TipoFocusStatus.PROCESSANDO;
		presencaComprador = NFIndicadorPresencaComprador.NAO_APLICA;
		cliente = new Cliente();
		enderecoEntrega = new Endereco();
		tipo = NFTipo.SAIDA;
		formaPagamento = NFIndicadorFormaPagamento.A_VISTA;
		tipoEmissao = NFTipoEmissao.EMISSAO_NORMAL;
		ambiente = DFAmbiente.HOMOLOGACAO;
		finalidade = NFFinalidade.NORMAL;
		programaEmissor = NFProcessoEmissor.CONTRIBUINTE;
		localDestinoOperacao = NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA;
		consumidorFinal = NFOperacaoConsumidorFinal.SIM;
		presencaComprador = NFIndicadorPresencaComprador.OPERACAO_PRESENCIAL;
		modalidadeFrete = NFModalidadeFrete.SEM_OCORRENCIA_TRANSPORTE;
	}

	public Long getCodigoVerificacao() {
		return codigoVerificacao;
	}

	public void setCodigoVerificacao(Long codigoVerificacao) {
		this.codigoVerificacao = codigoVerificacao;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Endereco getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public void setEnderecoEntrega(Endereco enderecoEntrega) {
		this.enderecoEntrega = enderecoEntrega;
	}

	@Enumerated(EnumType.ORDINAL)
	public NFProcessoEmissor getProgramaEmissor() {
		return programaEmissor;
	}

	public void setProgramaEmissor(NFProcessoEmissor programaEmissor) {
		this.programaEmissor = programaEmissor;
	}

	public NFIndicadorPresencaComprador getPresencaComprador() {
		return presencaComprador;
	}

	public void setPresencaComprador(NFIndicadorPresencaComprador presencaComprador) {
		this.presencaComprador = presencaComprador;
	}

	public NFModalidadeFrete getModalidadeFrete() {
		return modalidadeFrete;
	}

	public void setModalidadeFrete(NFModalidadeFrete modalidadeFrete) {
		this.modalidadeFrete = modalidadeFrete;
	}

	@Enumerated(EnumType.ORDINAL)
	public TipoModeloFiscal getModelo() {
		return modelo;
	}

	public void setModelo(TipoModeloFiscal modelo) {
		this.modelo = modelo;
	}

	@Enumerated(EnumType.ORDINAL)
	public NFFinalidade getFinalidade() {
		return finalidade;
	}

	public void setFinalidade(NFFinalidade finalidade) {
		this.finalidade = finalidade;
	}

	@Enumerated(EnumType.ORDINAL)
	public DFAmbiente getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(DFAmbiente ambiente) {
		this.ambiente = ambiente;
	}

	@Enumerated(EnumType.ORDINAL)
	public NFTipoEmissao getTipoEmissao() {
		return tipoEmissao;
	}

	public void setTipoEmissao(NFTipoEmissao tipoEmissao) {
		this.tipoEmissao = tipoEmissao;
	}

	@Column(columnDefinition = "text")
	public String getCaminhoXmlCancelamento() {
		return caminhoXmlCancelamento;
	}

	public void setCaminhoXmlCancelamento(String caminhoXmlCancelamento) {
		this.caminhoXmlCancelamento = caminhoXmlCancelamento;
	}

	@Column(columnDefinition = "text")
	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	@Column(columnDefinition = "text")
	public String getPdfFile() {
		return pdfFile;
	}

	public void setPdfFile(String pdfFile) {
		this.pdfFile = pdfFile;
	}

	@Column(columnDefinition = "text")
	public String getMensagemSefaz() {
		return mensagemSefaz;
	}

	public void setMensagemSefaz(String mensagemSefaz) {
		this.mensagemSefaz = mensagemSefaz;
	}

	@Column(columnDefinition = "text")
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getImpressao() {
		return impressao;
	}

	public void setImpressao(Date impressao) {
		this.impressao = impressao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	@Column(length = 44)
	public String getChaveRef() {
		return chaveRef;
	}

	public void setChaveRef(String chaveRef) {
		this.chaveRef = chaveRef;
	}

	@Column(length = 44)
	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	// @Column(length = 1, nullable = false)
	@Enumerated(EnumType.ORDINAL)
	public NFIndicadorFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(NFIndicadorFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	@Enumerated(EnumType.ORDINAL)
	public NFTipo getTipo() {
		return tipo;
	}

	public void setTipo(NFTipo tipo) {
		this.tipo = tipo;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	@Enumerated(EnumType.ORDINAL)
	public NFIdentificadorLocalDestinoOperacao getLocalDestinoOperacao() {
		return localDestinoOperacao;
	}

	public void setLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao localDestinoOperacao) {
		this.localDestinoOperacao = localDestinoOperacao;
	}

	public NFOperacaoConsumidorFinal getConsumidorFinal() {
		return consumidorFinal;
	}

	public void setConsumidorFinal(NFOperacaoConsumidorFinal consumidorFinal) {
		this.consumidorFinal = consumidorFinal;
	}

	@ManyToOne
	public OperacaoFiscal getOperacao() {
		return operacao;
	}

	public void setOperacao(OperacaoFiscal operacao) {
		this.operacao = operacao;
	}

	public int getSerie() {
		return serie;
	}

	public void setSerie(int serie) {
		this.serie = serie;
	}

	public TipoFocusStatus getStatus() {
		return status;
	}

	public void setStatus(TipoFocusStatus status) {
		this.status = status;
	}

	public Long getStatusSefaz() {
		return statusSefaz;
	}

	public void setStatusSefaz(Long statusSefaz) {
		this.statusSefaz = statusSefaz;
	}

	public Long getCodigoUnico() {
		return codigoUnico;
	}

	public void setCodigoUnico(Long codigoUnico) {
		this.codigoUnico = codigoUnico;
	}

	@Column(columnDefinition = "text")
	public String getCaminhoPdfCarta() {
		return caminhoPdfCarta;
	}

	public void setCaminhoPdfCarta(String caminhoPdfCarta) {
		this.caminhoPdfCarta = caminhoPdfCarta;
	}

	@Column(columnDefinition = "text")
	public String getCaminhoXmlCarta() {
		return caminhoXmlCarta;
	}

	public void setCaminhoXmlCarta(String caminhoXmlCarta) {
		this.caminhoXmlCarta = caminhoXmlCarta;
	}

	@Transient
	public boolean isDocumentoCancelado() {
		return this.dataCancelamento != null;
	}

	@Transient
	public boolean isDocumentoEnviado() {
		return this.dataEmissao != null;
	}

}