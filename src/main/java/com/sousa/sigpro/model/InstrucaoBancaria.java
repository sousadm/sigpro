package com.sousa.sigpro.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class InstrucaoBancaria {

	private Integer carteiraCodigo;
	private String localPagamento;
	private String instrucaoSacado;
	private String instrucao1;
	private String instrucao2;
	private String carteiraNome;
	private String convenio;
	private String modalidade;
	private boolean aceite;

	private int diaParaProtesto;
	private int diaParaNegativar;
	private int diaParaMulta;
	private int diaParaJuro;

	private int tempoLimiteRecebimento;
	private int tempoLimiteAbatimento;
	private int tempoLimiteDesconto;

	private double aliquotaJuro;
	private double aliquotaMulta;
	private double aliquotaDesconto;
	private String contaCedente;
	private String codigoTransmissao;

	@Column(length = 15)
	public String getCodigoTransmissao() {
		return codigoTransmissao;
	}

	public void setCodigoTransmissao(String codigoTransmissao) {
		this.codigoTransmissao = codigoTransmissao;
	}

	@Column(length = 3)
	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	@Column(length = 20)
	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	@Column(length = 20)
	public String getContaCedente() {
		return contaCedente;
	}

	public void setContaCedente(String contaCedente) {
		this.contaCedente = contaCedente;
	}

	@Column(length = 100)
	public String getInstrucao1() {
		return instrucao1;
	}

	public void setInstrucao1(String instrucao1) {
		this.instrucao1 = instrucao1;
	}

	@Column(length = 100)
	public String getInstrucao2() {
		return instrucao2;
	}

	public void setInstrucao2(String instrucao2) {
		this.instrucao2 = instrucao2;
	}

	@Column(length = 100)
	public String getCarteiraNome() {
		return carteiraNome;
	}

	public void setCarteiraNome(String carteiraNome) {
		this.carteiraNome = carteiraNome;
	}

	public boolean isAceite() {
		return aceite;
	}

	public void setAceite(boolean aceite) {
		this.aceite = aceite;
	}

	public Integer getCarteiraCodigo() {
		return carteiraCodigo;
	}

	public void setCarteiraCodigo(Integer carteiraCodigo) {
		this.carteiraCodigo = carteiraCodigo;
	}

	@Column(columnDefinition = "text")
	public String getLocalPagamento() {
		return localPagamento;
	}

	public void setLocalPagamento(String localPagamento) {
		this.localPagamento = localPagamento;
	}

	@Column(columnDefinition = "text")
	public String getInstrucaoSacado() {
		return instrucaoSacado;
	}

	public void setInstrucaoSacado(String instrucaoSacado) {
		this.instrucaoSacado = instrucaoSacado;
	}

	public int getDiaParaProtesto() {
		return diaParaProtesto;
	}

	public void setDiaParaProtesto(int diaParaProtesto) {
		this.diaParaProtesto = diaParaProtesto;
	}

	public int getDiaParaNegativar() {
		return diaParaNegativar;
	}

	public void setDiaParaNegativar(int diaParaNegativar) {
		this.diaParaNegativar = diaParaNegativar;
	}

	public int getTempoLimiteRecebimento() {
		return tempoLimiteRecebimento;
	}

	public void setTempoLimiteRecebimento(int tempoLimiteRecebimento) {
		this.tempoLimiteRecebimento = tempoLimiteRecebimento;
	}

	public int getTempoLimiteAbatimento() {
		return tempoLimiteAbatimento;
	}

	public void setTempoLimiteAbatimento(int tempoLimiteAbatimento) {
		this.tempoLimiteAbatimento = tempoLimiteAbatimento;
	}

	public int getTempoLimiteDesconto() {
		return tempoLimiteDesconto;
	}

	public void setTempoLimiteDesconto(int tempoLimiteDesconto) {
		this.tempoLimiteDesconto = tempoLimiteDesconto;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaJuro() {
		return aliquotaJuro;
	}

	public void setAliquotaJuro(double aliquotaJuro) {
		this.aliquotaJuro = aliquotaJuro;
	}

	@Column(precision = 12, scale = 2)
	public double getAliquotaMulta() {
		return aliquotaMulta;
	}

	public void setAliquotaMulta(double aliquotaMulta) {
		this.aliquotaMulta = aliquotaMulta;
	}

	public int getDiaParaMulta() {
		return diaParaMulta;
	}

	public void setDiaParaMulta(int diaParaMulta) {
		this.diaParaMulta = diaParaMulta;
	}

	public int getDiaParaJuro() {
		return diaParaJuro;
	}

	public void setDiaParaJuro(int diaParaJuro) {
		this.diaParaJuro = diaParaJuro;
	}

	public double getAliquotaDesconto() {
		return aliquotaDesconto;
	}

	public void setAliquotaDesconto(double aliquotaDesconto) {
		this.aliquotaDesconto = aliquotaDesconto;
	}

}
