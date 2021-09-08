package com.sousa.sigpro.model;

import java.util.Date;

import com.fincatto.documentofiscal.nfe400.classes.NFTipo;

public class Manifestacao {

	private String nome_emitente;
	private String documento_emitente;
	private String chave_nfe;
	private String situacao;
	private String manifestacao_destinatario;
	private String digest_value;
	private String numero_carta_correcao;
	private String carta_correcao;
	private String justificativa_cancelamento;
	private boolean nfe_completa;
	private int versao;
	private double valor_total;
	private Date data_emissao;
	private Date data_carta_correcao;
	private Date data_cancelamento;
	private NFTipo tipo_nfe;

	public String getNome_emitente() {
		return nome_emitente;
	}

	public void setNome_emitente(String nome_emitente) {
		this.nome_emitente = nome_emitente;
	}

	public String getDocumento_emitente() {
		return documento_emitente;
	}

	public void setDocumento_emitente(String documento_emitente) {
		this.documento_emitente = documento_emitente;
	}

	public String getChave_nfe() {
		return chave_nfe;
	}

	public void setChave_nfe(String chave_nfe) {
		this.chave_nfe = chave_nfe;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getManifestacao_destinatario() {
		return manifestacao_destinatario;
	}

	public void setManifestacao_destinatario(String manifestacao_destinatario) {
		this.manifestacao_destinatario = manifestacao_destinatario;
	}

	public NFTipo getTipo_nfe() {
		return tipo_nfe;
	}

	public void setTipo_nfe(NFTipo tipo_nfe) {
		this.tipo_nfe = tipo_nfe;
	}

	public String getDigest_value() {
		return digest_value;
	}

	public void setDigest_value(String digest_value) {
		this.digest_value = digest_value;
	}

	public String getNumero_carta_correcao() {
		return numero_carta_correcao;
	}

	public void setNumero_carta_correcao(String numero_carta_correcao) {
		this.numero_carta_correcao = numero_carta_correcao;
	}

	public String getCarta_correcao() {
		return carta_correcao;
	}

	public void setCarta_correcao(String carta_correcao) {
		this.carta_correcao = carta_correcao;
	}

	public String getJustificativa_cancelamento() {
		return justificativa_cancelamento;
	}

	public void setJustificativa_cancelamento(String justificativa_cancelamento) {
		this.justificativa_cancelamento = justificativa_cancelamento;
	}

	public boolean isNfe_completa() {
		return nfe_completa;
	}

	public void setNfe_completa(boolean nfe_completa) {
		this.nfe_completa = nfe_completa;
	}

	public double getValor_total() {
		return valor_total;
	}

	public void setValor_total(double valor_total) {
		this.valor_total = valor_total;
	}

	public Date getData_emissao() {
		return data_emissao;
	}

	public void setData_emissao(Date data_emissao) {
		this.data_emissao = data_emissao;
	}

	public int getVersao() {
		return versao;
	}

	public void setVersao(int versao) {
		this.versao = versao;
	}

	public Date getData_carta_correcao() {
		return data_carta_correcao;
	}

	public void setData_carta_correcao(Date data_carta_correcao) {
		this.data_carta_correcao = data_carta_correcao;
	}

	public Date getData_cancelamento() {
		return data_cancelamento;
	}

	public void setData_cancelamento(Date data_cancelamento) {
		this.data_cancelamento = data_cancelamento;
	}

}