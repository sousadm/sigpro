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

import com.fincatto.documentofiscal.mdfe3.classes.def.MDFTipoProprietario;

@Entity
public class Transportador implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private Date dataCadastro;
	private String codigoRNTRC;
	private MDFTipoProprietario tipoProprietario;

	public Transportador() {
		dataCadastro = new Date();
		tipoProprietario = MDFTipoProprietario.OUTROS;
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

	@Column(length = 20)
	public String getCodigoRNTRC() {
		return codigoRNTRC;
	}

	public void setCodigoRNTRC(String codigoRNTRC) {
		this.codigoRNTRC = codigoRNTRC;
	}

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public MDFTipoProprietario getTipoProprietario() {
		return tipoProprietario;
	}

	public void setTipoProprietario(MDFTipoProprietario tipoProprietario) {
		this.tipoProprietario = tipoProprietario;
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
		Transportador other = (Transportador) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}