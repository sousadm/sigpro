package com.sousa.sigpro.sigproapi.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Demanda {

	private Long id;

	private String resumo;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date dataEncerramento;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date data;

	@JsonProperty(value = "texto")
	private String descritivo;

	private String empresa;

	private String nome;

	private String email;

	private String telefone;

	private List<Retorno> retornos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDescritivo() {
		return descritivo;
	}

	public void setDescritivo(String descritivo) {
		this.descritivo = descritivo;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public List<Retorno> getRetornos() {
		return retornos;
	}

	public void setRetornos(List<Retorno> retornos) {
		this.retornos = retornos;
	}

	public Date getDataEncerramento() {
		return dataEncerramento;
	}

	public void setDataEncerramento(Date dataEncerramento) {
		this.dataEncerramento = dataEncerramento;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	@Transient
	public boolean isPodeSalvar() {
		return id == null;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}
}