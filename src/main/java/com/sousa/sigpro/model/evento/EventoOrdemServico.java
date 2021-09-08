package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("ORDEMSERVICO")
public class EventoOrdemServico extends Evento {

	private OrdemServico ordem;

	public EventoOrdemServico(Usuario usuario, OrdemServico ordem, String descritivo) {
		super(usuario, descritivo);
		this.ordem = ordem;
	}

	public EventoOrdemServico() {

	}

	@ManyToOne
	public OrdemServico getOrdem() {
		return ordem;
	}

	public void setOrdem(OrdemServico ordem) {
		this.ordem = ordem;
	}

}