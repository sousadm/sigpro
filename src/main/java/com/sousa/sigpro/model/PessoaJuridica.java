package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Null;

import org.hibernate.validator.constraints.br.CNPJ;

import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorIEDestinatario;
import com.sousa.sigpro.model.tipo.TipoPorteEmpresa;

@Embeddable
public class PessoaJuridica implements Serializable {

	private static final long serialVersionUID = 1L;

	// dados pessoa juridica
	private String fantasia;
	private String cnpj;
	private String IE;
	private String IESub;
	private String IM;
	private Date fundacao;
	private String representante;
	private boolean incentivoCultural;
	private NFRegimeTributario regime;
	private TipoPorteEmpresa porte;
	private NFIndicadorIEDestinatario tipoIE;
	private String codigoAtividade;
	private String codigoSuframa;
	private String missao;
	private String visao;
	private String sobre;
	private String tokenHomologacao;
	private String tokenProducao;
	
	@Column(length = 100)
	public String getFantasia() {
		return fantasia;
	}

	public void setFantasia(String fantasia) {
		this.fantasia = fantasia;
	}

	@CNPJ(message = "inválido")
	@Column(length = 14)
	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	@Column(length = 20)
	public String getIE() {
		return IE;
	}

	public void setIE(String iE) {
		IE = iE;
	}

	@Column(length = 20)
	public String getCodigoSuframa() {
		return codigoSuframa;
	}

	public void setCodigoSuframa(String codigoSuframa) {
		this.codigoSuframa = codigoSuframa;
	}

	@Column(length = 20)
	public String getIESub() {
		return IESub;
	}

	public void setIESub(String iESub) {
		IESub = iESub;
	}

	@Column(length = 7)
	public String getCodigoAtividade() {
		return codigoAtividade;
	}

	public void setCodigoAtividade(String codigoAtividade) {
		this.codigoAtividade = codigoAtividade;
	}

	@Column(length = 20)
	public String getIM() {
		return IM;
	}

	public void setIM(String iM) {
		IM = iM;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getFundacao() {
		return fundacao;
	}

	public void setFundacao(Date fundacao) {
		this.fundacao = fundacao;
	}

	@Column(length = 100)
	public String getRepresentante() {
		return representante;
	}

	public void setRepresentante(String representante) {
		this.representante = representante;
	}

	@Enumerated(EnumType.ORDINAL)
	public NFRegimeTributario getRegime() {
		return regime;
	}

	public void setRegime(NFRegimeTributario regime) {
		this.regime = regime;
	}

	@Enumerated(EnumType.ORDINAL)
	public NFIndicadorIEDestinatario getTipoIE() {
		return tipoIE;
	}

	public void setTipoIE(NFIndicadorIEDestinatario tipoIE) {
		this.tipoIE = tipoIE;
	}

	@Column(length = 3)
	@Enumerated(EnumType.STRING)
	public TipoPorteEmpresa getPorte() {
		return porte;
	}

	public void setPorte(TipoPorteEmpresa porte) {
		this.porte = porte;
	}

	@Column(columnDefinition = "text")
	public String getMissao() {
		return missao;
	}

	public void setMissao(String missao) {
		this.missao = missao;
	}

	@Column(columnDefinition = "text")
	public String getVisao() {
		return visao;
	}

	public void setVisao(String visao) {
		this.visao = visao;
	}

	@Column(columnDefinition = "text")
	public String getSobre() {
		return sobre;
	}

	public void setSobre(String sobre) {
		this.sobre = sobre;
	}

	@Column(columnDefinition = "boolean default true")
	public boolean isIncentivoCultural() {
		return incentivoCultural;
	}

	public void setIncentivoCultural(boolean incentivoCultural) {
		this.incentivoCultural = incentivoCultural;
	}

	@Column(length = 32)
	public String getTokenHomologacao() {
		return tokenHomologacao;
	}

	public void setTokenHomologacao(String tokenHomologacao) {
		this.tokenHomologacao = tokenHomologacao;
	}

	@Column(length = 32)
	public String getTokenProducao() {
		return tokenProducao;
	}

	public void setTokenProducao(String tokenProducao) {
		this.tokenProducao = tokenProducao;
	}

}