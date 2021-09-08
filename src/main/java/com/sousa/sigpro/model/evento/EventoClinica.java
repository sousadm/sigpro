package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Usuario;
import com.sousa.sigpro.model.clinica.ClinicaAgenda;

@Entity
@DiscriminatorValue("CLINICA")
public class EventoClinica extends Evento {

	private ClinicaAgenda agenda;

	public EventoClinica(Usuario usuario, ClinicaAgenda agenda, String descritivo) {
		super(usuario, descritivo);
		this.agenda  = agenda;
	}

	public EventoClinica() {

	}

	@ManyToOne
	public ClinicaAgenda getAgenda() {
		return agenda;
	}

	public void setAgenda(ClinicaAgenda agenda) {
		this.agenda = agenda;
	}

}