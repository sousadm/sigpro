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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fincatto.documentofiscal.DFAmbiente;

@Entity
@NamedQueries({
		@NamedQuery(name = "ClienteDigital.porIdentificadorAgenteAmbiente", query = "select c from ClienteDigital c where c.identificador = :identificador and c.agente = :agente and c.agente.ambiente = :ambiente "),
		@NamedQuery(name = "ClienteDigital.pesquisa", query = "select c from ClienteDigital c join c.agente a where c.pessoa = :pessoa and c.agente = :agente and a.ambiente = :ambiente") })
public class ClienteDigital implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa pessoa;
	private Agente agente;
	private String identificador;
	private Date dataCadastro;
	private DFAmbiente ambiente;

	public ClienteDigital() {
		this.dataCadastro = new Date();
	}

	public ClienteDigital(Pessoa pessoa, Agente agente, String identificador) {
		this.pessoa = pessoa;
		this.agente = agente;
		this.identificador = identificador;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Enumerated(EnumType.ORDINAL)
	public DFAmbiente getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(DFAmbiente ambiente) {
		this.ambiente = ambiente;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@ManyToOne
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@ManyToOne
	public Agente getAgente() {
		return agente;
	}

	public void setAgente(Agente agente) {
		if (agente != null) {
			ambiente = agente.getAmbiente();
		}
		this.agente = agente;
	}

	@Column(length = 30, nullable = false)
	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
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
		ClienteDigital other = (ClienteDigital) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}