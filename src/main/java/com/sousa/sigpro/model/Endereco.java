package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoEndereco;

@Entity
@NamedQueries({
		@NamedQuery(name = "Endereco.porPessoa", query = "select e from Endereco e where e.pessoa = :pessoa order by e.tipoEndereco"),
		@NamedQuery(name = "Endereco.porCepNumero", query = "select e from Endereco e where e.cep = :cep and e.numero = :numero") })
public class Endereco implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String logradouro;
	private int numero;
	private String complemento;
	private Municipio municipio;
	private String bairro;
	private String cep;
	private TipoEndereco tipoEndereco;
	private Pessoa pessoa;

	public Endereco() {
		municipio = new Municipio();
		tipoEndereco = TipoEndereco.RESIDENCIAL;
	}

	public Endereco(Municipio municipio) {
		this.municipio = municipio;
	}

	@Override
	public String toString() {
		return (logradouro == null ? "" : logradouro) + ", " + numero + " " + (bairro == null ? "" : bairro);
	}

	@Transient
	public String getCepCidade() {
		return "CEP - " + cep + "  ";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@ManyToOne
	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	@Column(length = 150)
	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	@Column(length = 35)
	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(length = 8)
	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	@Column(length = 50)
	public String getBairro() {
		return bairro;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoEndereco getTipoEndereco() {
		return tipoEndereco;
	}

	public void setTipoEndereco(TipoEndereco tipoEndereco) {
		this.tipoEndereco = tipoEndereco;
	}

	@Transient
	public boolean isExiste() {
		return id != null && id > 0;
	}

}