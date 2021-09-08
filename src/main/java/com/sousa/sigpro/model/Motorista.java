package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sousa.sigpro.model.tipo.TipoCategoriaHabilitacao;

@Entity
public class Motorista implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private Date dataCadastro;
	private String cnhNumero;
	private String cnhOrgao;
	private Date cnhPrimeira;
	private Date cnhEmissao;
	private Date cnhValidade;
	private TipoCategoriaHabilitacao cnhCategoria;

	public Motorista() {
		dataCadastro = new Date();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 100)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getCnhEmissao() {
		return cnhEmissao;
	}

	public void setCnhEmissao(Date cnhEmissao) {
		this.cnhEmissao = cnhEmissao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getCnhPrimeira() {
		return cnhPrimeira;
	}

	public void setCnhPrimeira(Date cnhPrimeira) {
		this.cnhPrimeira = cnhPrimeira;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getCnhValidade() {
		return cnhValidade;
	}

	public void setCnhValidade(Date cnhValidade) {
		this.cnhValidade = cnhValidade;
	}

	@Column(length = 20)
	public String getCnhOrgao() {
		return cnhOrgao;
	}

	public void setCnhOrgao(String cnhOrgao) {
		this.cnhOrgao = cnhOrgao;
	}

	@Column(length = 20)
	public String getCnhNumero() {
		return cnhNumero;
	}

	public void setCnhNumero(String cnhNumero) {
		this.cnhNumero = cnhNumero;
	}

	@Column(length = 2)
	@Enumerated(EnumType.STRING)
	public TipoCategoriaHabilitacao getCnhCategoria() {
		return cnhCategoria;
	}

	public void setCnhCategoria(TipoCategoriaHabilitacao cnhCategoria) {
		this.cnhCategoria = cnhCategoria;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Motorista other = (Motorista) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}