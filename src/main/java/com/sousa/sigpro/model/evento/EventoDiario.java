package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.CaixaDiario;
import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("DIARIO")
public class EventoDiario extends Evento {

	private CaixaDiario diario;

	public EventoDiario(Usuario usuario, CaixaDiario diario, String descritivo) {
		super(usuario, descritivo);
		this.diario = diario;
	}

	public EventoDiario() {

	}

	@ManyToOne
	public CaixaDiario getDiario() {
		return diario;
	}

	public void setDiario(CaixaDiario diario) {
		this.diario = diario;
	}

}