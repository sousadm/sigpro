package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("NOTAFISCAL")
public class EventoNotaFiscal extends Evento {

	private NotaFiscal nota;

	public EventoNotaFiscal(Usuario usuario, NotaFiscal nota, String descritivo) {
		super(usuario, descritivo);
		this.nota = nota;
	}

	public EventoNotaFiscal() {

	}

	@ManyToOne
	public NotaFiscal getNota() {
		return nota;
	}

	public void setNota(NotaFiscal nota) {
		this.nota = nota;
	}

}