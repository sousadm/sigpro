package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;

import com.sousa.sigpro.model.Comando;
import com.sousa.sigpro.repository.Comandos;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class ComandoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private Comandos comandos;

	private int indice;
	private String descricao;
	private Comando comando;
	private List<Comando> lista;

	public ComandoBean() {
		novo();
	}

	public void pesquisar() {
		lista = comandos.lista(descricao);
	}

	public void novo() {
		indice = -1;
		comando = new Comando();
	}

	public void editar(int numero) {
		this.indice = numero;
		this.comando = comandos.porId(lista.get(numero).getId());
	}

	public void salvar() {
		try {
			comando.setOrigem(seguranca.getPessoaLogadoOrigem());
			comando = comandos.guardar(comando);

			if (indice >= 0) {
				lista.set(indice, comando);
			} else {
				pesquisar();
			}

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(Comando comando) {
		try {
			comandos.remover(comando);
			pesquisar();
			FacesUtil.addInfoMessage("Registro excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}


	public Comando getComando() {
		return comando;
	}

	public void setComando(Comando comando) {
		this.comando = comando;
	}

	public List<Comando> getLista() {
		return lista;
	}

	public void setLista(List<Comando> lista) {
		this.lista = lista;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}