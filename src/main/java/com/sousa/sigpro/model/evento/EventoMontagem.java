package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Montagem;
import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("MONTAGEM")
public class EventoMontagem extends Evento {

	private Montagem montagem;

	public EventoMontagem(Usuario usuario, Montagem montagem, String descritivo) {
		super(usuario, descritivo);
		this.montagem = montagem;
	}

	public EventoMontagem() {

	}

	@ManyToOne
	public Montagem getMontagem() {
		return montagem;
	}

	public void setMontagem(Montagem montagem) {
		this.montagem = montagem;
	}

}