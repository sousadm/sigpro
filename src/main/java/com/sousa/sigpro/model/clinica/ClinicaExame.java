package com.sousa.sigpro.model.clinica;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ClinicaExame implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private ClinicaAgenda clinica;
	private Exame exame;

	public ClinicaExame() {
		
	}

	public ClinicaExame(ClinicaAgenda clinica, Exame exame) {
		this.clinica = clinica;
		this.exame = exame;
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
	public ClinicaAgenda getClinica() {
		return clinica;
	}

	public void setClinica(ClinicaAgenda clinica) {
		this.clinica = clinica;
	}

	@ManyToOne
	public Exame getExame() {
		return exame;
	}

	public void setExame(Exame exame) {
		this.exame = exame;
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
		ClinicaExame other = (ClinicaExame) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}