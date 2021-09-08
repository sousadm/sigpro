package com.sousa.sigpro.model.clinica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class ClinicaConsulta implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private ClinicaAgenda agenda;
	private ClinicaAcolhimento acolhimento;
	private Date inicio;
	private Date termino;
	private String diagnostico;

	private List<Anamnese> items = new ArrayList<>();
//	private List<Procedimento> exames = new ArrayList<>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Anamnese> getItems() {
		return items;
	}

	public void setItems(List<Anamnese> items) {
		this.items = items;
	}

	@ManyToOne
	public ClinicaAgenda getAgenda() {
		return agenda;
	}

	public void setAgenda(ClinicaAgenda agenda) {
		this.agenda = agenda;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getTermino() {
		return termino;
	}

	public void setTermino(Date termino) {
		this.termino = termino;
	}

	@Column(columnDefinition = "text")
	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	@Transient
	public boolean isInicializavel() {
		return id == null && inicio == null;
	}

	@Transient
	public boolean isCancelavel() {
		return !isInicializavel() && termino != null;
	}

	@Embedded
	public ClinicaAcolhimento getAcolhimento() {
		return acolhimento;
	}

	public void setAcolhimento(ClinicaAcolhimento acolhimento) {
		this.acolhimento = acolhimento;
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
		ClinicaConsulta other = (ClinicaConsulta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}