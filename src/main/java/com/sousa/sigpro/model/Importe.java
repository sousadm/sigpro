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

import com.sousa.sigpro.model.tipo.TipoImporte;

@Entity
public class Importe implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long caixaLocal;
	private Date dataCadastro;
	private String macAdress;
	private String ipAdress;
	private String serie;
	private TipoImporte tipo;
	private String script;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoImporte getTipo() {
		return tipo;
	}

	public void setTipo(TipoImporte tipo) {
		this.tipo = tipo;
	}

	public Long getCaixaLocal() {
		return caixaLocal;
	}

	public void setCaixaLocal(Long caixaLocal) {
		this.caixaLocal = caixaLocal;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Column(length = 20)
	public String getIpAdress() {
		return ipAdress;
	}

	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}

	@Column(length = 20)
	public String getMacAdress() {
		return macAdress;
	}

	public void setMacAdress(String macAdress) {
		this.macAdress = macAdress;
	}

	@Column(length = 20)
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	@Column(columnDefinition = "text")
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
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
		Importe other = (Importe) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}