package com.sousa.sigpro.model;

public class EnderecoFocus {

	private String cep;
	private String tipo;
	private String nome;
	private String uf;
	private String nome_localidade;
	private String codigo_ibge;
	private String tipo_logradouro;
	private String nome_logradouro;
	private String bairro;
	private String descricao;

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getNome_localidade() {
		return nome_localidade;
	}

	public void setNome_localidade(String nome_localidade) {
		this.nome_localidade = nome_localidade;
	}

	public String getCodigo_ibge() {
		return codigo_ibge;
	}

	public void setCodigo_ibge(String codigo_ibge) {
		this.codigo_ibge = codigo_ibge;
	}

	public String getTipo_logradouro() {
		return tipo_logradouro;
	}

	public void setTipo_logradouro(String tipo_logradouro) {
		this.tipo_logradouro = tipo_logradouro;
	}

	public String getNome_logradouro() {
		return nome_logradouro;
	}

	public void setNome_logradouro(String nome_logradouro) {
		this.nome_logradouro = nome_logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}