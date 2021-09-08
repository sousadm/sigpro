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
		@NamedQuery(name = "BoletoDigital.porIdAmbiente", query = "select b from BoletoDigital b join b.titulo t where b.agente = :agente and b.ambiente = :ambiente and b.identificador = :identificador"),
		@NamedQuery(name = "BoletoDigital.listaPorExpedicao", query = "select b from BoletoDigital b join b.titulo t where t.expedicao = :expedicao order by b.titulo.vencimento"),
		@NamedQuery(name = "BoletoDigital.listaPorTitulo", query = "select b from BoletoDigital b where b.ambiente = :ambiente and b.titulo = :titulo order by b.titulo.vencimento") })
public class BoletoDigital implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Titulo titulo;
	private Agente agente;
	private String urlDoArquivo;
	private String urlDaFatura;
	private String status;
	private String numeroDaFatura;
	private Date dataCadastro;
	private Date dataAtualizacao;
	private String identificador;
	private DFAmbiente ambiente;

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

	@ManyToOne
	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
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

	@Column(length = 100)
	public String getUrlDaFatura() {
		return urlDaFatura;
	}

	public void setUrlDaFatura(String urlDaFatura) {
		this.urlDaFatura = urlDaFatura;
	}

	@Column(length = 100)
	public String getUrlDoArquivo() {
		return urlDoArquivo;
	}

	public void setUrlDoArquivo(String urlDoArquivo) {
		this.urlDoArquivo = urlDoArquivo;
	}

	@Column(length = 100)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(length = 10)
	public String getNumeroDaFatura() {
		return numeroDaFatura;
	}

	public void setNumeroDaFatura(String numeroDaFatura) {
		this.numeroDaFatura = numeroDaFatura;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
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
		BoletoDigital other = (BoletoDigital) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}