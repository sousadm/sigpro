package com.sousa.sigpro.model.clinica;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.tipo.TipoDiaSemana;

@Entity
@NamedQueries({
		@NamedQuery(name = "Horario.teste", query = "select h from ClinicaHorario h where h.consultor = :teste ") })
public class ClinicaHorario implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Colaborador consultor;
	private Date inicio;
	private Date intervalo;
	private Date reinicio;
	private Date encerramento;
	private List<TipoDiaSemana> dias;

	public void prepara() {
		if (intervalo == null)
			intervalo = inicio;
		if (reinicio == null)
			reinicio = encerramento;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ElementCollection
	public List<TipoDiaSemana> getDias() {
		return dias;
	}

	public void setDias(List<TipoDiaSemana> dias) {
		this.dias = dias;
	}

	@ManyToOne
	public Colaborador getConsultor() {
		return consultor;
	}

	public void setConsultor(Colaborador consultor) {
		this.consultor = consultor;
	}

	@Temporal(value = TemporalType.TIME)
	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	@Temporal(value = TemporalType.TIME)
	public Date getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(Date intervalo) {
		this.intervalo = intervalo;
	}

	@Temporal(value = TemporalType.TIME)
	public Date getReinicio() {
		return reinicio;
	}

	public void setReinicio(Date reinicio) {
		this.reinicio = reinicio;
	}

	@Temporal(value = TemporalType.TIME)
	public Date getEncerramento() {
		return encerramento;
	}

	public void setEncerramento(Date encerramento) {
		this.encerramento = encerramento;
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
		ClinicaHorario other = (ClinicaHorario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
