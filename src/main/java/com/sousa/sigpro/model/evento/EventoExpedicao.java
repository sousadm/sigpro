package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("EXPEDICAO")
public class EventoExpedicao extends Evento {

	private Expedicao expedicao;

	public EventoExpedicao(Usuario usuario, Expedicao expedicao, String descritivo) {
		super(usuario, descritivo);
		this.expedicao = expedicao;
	}

	public EventoExpedicao() {

	}

	@ManyToOne
	public Expedicao getExpedicao() {
		return expedicao;
	}
	public void setExpedicao(Expedicao expedicao) {
		this.expedicao = expedicao;
	}

}