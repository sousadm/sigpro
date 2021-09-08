package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.model.ContratoAdesao;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.model.Usuario;
import com.sousa.sigpro.model.evento.EventoHistorico;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.util.Suporte;

@Named
@ViewScoped
public class ListaEventoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Eventos eventos;
	@Inject
	private Pessoas pessoas;
	private CondicaoFilter filtro;
	private boolean mostra_documento;
	private String title;

	private List<EventoHistorico> lista;

	public ListaEventoBean() {
		filtro = new CondicaoFilter();
	}

	public String getTitle() {
		return title;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public boolean isMostra_documento() {
		return mostra_documento;
	}

	public List<EventoHistorico> getLista() {
		return lista;
	}

	public void preparar(Expedicao expedicao) {
		Pessoa cliente = pessoas.cliente(expedicao.getCliente());
		title = "Pedido: " + Suporte.zerosAEsquerda(expedicao.getId(), 6) + " - " + cliente.getNome();
		lista = eventos.lista(expedicao);
	}

	public void preparar(Pessoa pessoa) {
		title = pessoa.getNome();
		lista = eventos.lista(pessoa);
	}

	public void preparar(ContratoAdesao contrato) {
		title = contrato.getNomeSchema();
		lista = eventos.lista(contrato);
	}

	public void usuario(Usuario usuario) {
		title = "Usuário: " + usuario.getNome();
		mostra_documento = true;
		filtro.setUsuario(usuario);
		lista = eventos.lista(filtro);
	}

	public void projeto(Projeto projeto) {
		lista = eventos.lista(projeto);
	}

	public void aquisicao(Aquisicao aquisicao) {
		lista = eventos.lista(aquisicao);
	}

	public void produto(Produto produto) {
		lista = eventos.lista(produto);
	}

}