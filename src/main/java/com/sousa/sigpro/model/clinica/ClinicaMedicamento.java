package com.sousa.sigpro.model.clinica;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ClinicaMedicamento implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private ClinicaAgenda clinica;
	private Medicamento medicamento;
	private String posologia;
	private double quantidade = 0;

	public ClinicaMedicamento() {

	}

	public ClinicaMedicamento(ClinicaAgenda clinica, Medicamento medicamento) {
		this.clinica = clinica;
		this.medicamento = medicamento;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	@ManyToOne
	public ClinicaAgenda getClinica() {
		return clinica;
	}

	public void setClinica(ClinicaAgenda clinica) {
		this.clinica = clinica;
	}

	@ManyToOne
	public Medicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(Medicamento medicamento) {
		this.medicamento = medicamento;
	}

	@Column(length = 100)
	public String getPosologia() {
		return posologia;
	}

	public void setPosologia(String posologia) {
		this.posologia = posologia;
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
		ClinicaMedicamento other = (ClinicaMedicamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}