package com.sousa.sigpro.model.clinica;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({
		@NamedQuery(name = "Medicamento.porCodigo", query = "select m from Medicamento m where m.codigo = :codigo") })
public class Medicamento implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date dataCadastro;
	private String codigo;
	private String descricao;
	private String principio;
	private String classe;
	private String forma;
	private String grupo;
	private String detentor;
	private String anvisa;
	private String tipo;

	public Medicamento() {
		// TODO Auto-generated constructor stub
	}

	public Medicamento(String codigo, String descricao, String principio, String classe, String forma, String grupo,
			String detentor, String anvisa, String tipo) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.principio = principio;
		this.classe = classe;
		this.forma = forma;
		this.grupo = grupo;
		this.detentor = detentor;
		this.anvisa = anvisa;
		this.tipo = tipo;
	}

	@Id
	@Column(length = 20, nullable = false)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(length = 300, nullable = false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(length = 500, nullable = false)
	public String getPrincipio() {
		return principio;
	}

	public void setPrincipio(String principio) {
		this.principio = principio;
	}

	@Column(length = 150, nullable = false)
	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	@Column(length = 100, nullable = false)
	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	@Column(length = 100, nullable = false)
	public String getForma() {
		return forma;
	}

	public void setForma(String forma) {
		this.forma = forma;
	}

	@Column(length = 100, nullable = false)
	public String getDetentor() {
		return detentor;
	}

	public void setDetentor(String detentor) {
		this.detentor = detentor;
	}

	@Column(length = 20, nullable = false)
	public String getAnvisa() {
		return anvisa;
	}

	public void setAnvisa(String anvisa) {
		this.anvisa = anvisa;
	}

	@Column(length = 20, nullable = false)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		Medicamento other = (Medicamento) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}