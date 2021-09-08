package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Remessa;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("REMESSA")
public class EventoRemessa extends Evento {

	private Remessa remessa;

	public EventoRemessa(Usuario usuario, Remessa remessa, String descritivo) {
		super(usuario, descritivo);
		this.remessa = remessa;
	}

	public EventoRemessa() {
		
	}

	@ManyToOne
	public Remessa getRemessa() {
		return remessa;
	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
	}

}