package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("PRODUTO")
public class EventoProduto extends Evento {

	private Produto produto;

	public EventoProduto(Usuario usuario, Produto produto, String descritivo) {
		super(usuario, descritivo);
		this.produto = produto;
	}

	public EventoProduto() {
	
	}

	@ManyToOne
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

}