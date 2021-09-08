package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sousa.sigpro.model.tipo.TipoEscolaridade;
import com.sousa.sigpro.model.tipo.TipoSexo;
import com.sousa.sigpro.model.tipo.TipoTratamento;

@Embeddable
public class PessoaFisica implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cpf;
	private String identidade;
	private String pai;
	private String mae;
	private Date nascimento;
	private Date emissao;
	private String orgao;
	private String documento;
	private String idEstrangeiro;
	private String nacionalidade;
	private String naturalidade;
	private TipoSexo sexo;
	private TipoTratamento tratamento;
	private TipoEscolaridade escolaridade;
	private Profissao profissao;
	private double altura = 0;
	private double peso = 0;

	public PessoaFisica() {
		tratamento = TipoTratamento.SRA;
		escolaridade = TipoEscolaridade.GRADUADO;
	}

	@Column(precision = 12, scale = 2)
	public double getAltura() {
		return altura;
	}

	public void setAltura(double altura) {
		this.altura = altura;
	}

	@Column(precision = 12, scale = 2)
	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	@ManyToOne
	public Profissao getProfissao() {
		return profissao;
	}

	public void setProfissao(Profissao profissao) {
		this.profissao = profissao;
	}

	@Column(length = 3)
	@Enumerated(EnumType.STRING)
	public TipoSexo getSexo() {
		return sexo;
	}

	public void setSexo(TipoSexo sexo) {
		this.sexo = sexo;
	}

	@Column(length = 3)
	@Enumerated(EnumType.STRING)
	public TipoTratamento getTratamento() {
		return tratamento;
	}

	@Enumerated(EnumType.ORDINAL)
	public TipoEscolaridade getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(TipoEscolaridade escolaridade) {
		this.escolaridade = escolaridade;
	}

	public void setTratamento(TipoTratamento tratamento) {
		this.tratamento = tratamento;
	}

	@Column(length = 20)
	public String getIdEstrangeiro() {
		return idEstrangeiro;
	}

	public void setIdEstrangeiro(String idEstrangeiro) {
		this.idEstrangeiro = idEstrangeiro;
	}

	@Column(length = 20)
	public String getOrgao() {
		return orgao;
	}

	public void setOrgao(String orgao) {
		this.orgao = orgao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	@Column(length = 100)
	public String getNacionalidade() {
		return nacionalidade;
	}

	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	@Column(length = 100)
	public String getNaturalidade() {
		return naturalidade;
	}

	public void setNaturalidade(String naturalidade) {
		this.naturalidade = naturalidade;
	}

	// @Future
	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getNascimento() {
		return nascimento;
	}

	public void setNascimento(Date nascimento) {
		this.nascimento = nascimento;
	}

	@Column(length = 100)
	public String getMae() {
		return mae;
	}

	public void setMae(String mae) {
		this.mae = mae;
	}

	@Column(length = 100)
	public String getPai() {
		return pai;
	}

	public void setPai(String pai) {
		this.pai = pai;
	}

	// @CPF(message = "inválido")
	@Column(length = 11)
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	@Column(length = 20)
	public String getIdentidade() {
		return identidade;
	}

	public void setIdentidade(String identidade) {
		this.identidade = identidade;
	}

	@Column(length = 20)
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

}