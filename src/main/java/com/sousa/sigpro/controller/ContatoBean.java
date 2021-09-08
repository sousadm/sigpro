package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;

import org.primefaces.event.TabChangeEvent;

import com.sousa.sigpro.model.Contato;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.repository.Contatos;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class ContatoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Contatos contatos;

	private Pessoa pessoa;
	private List<Contato> lista;
	private int activeTab;
	private Contato contato;

	public void addContato() {
		contato = new Contato(pessoa);
		if (lista != null && lista.size() == 0) {
			List<Contato> lst = contatos.porPessoa(pessoa);
			if (lst == null || lst.size() == 0) {
				contato.setPessoa(pessoa);
				contato.setNome(pessoa.getNome());
				contato.setDdd(pessoa.getDdd());
				contato.setCelular(pessoa.getCelular());
				contato.setFone(pessoa.getFone());
				contato.setEmail(pessoa.getEmail());
			}
		}
		lista.add(contato);
		activeTab = lista.size() - 1;
	}

	public Contato getContato() {
		return contato;
	}

	public void setContato(Contato contato) {
		this.contato = contato;
	}

	public List<Contato> getLista() {
		return lista;
	}

	public void setLista(List<Contato> lista) {
		this.lista = lista;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public int getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(int activeTab) {
		this.activeTab = activeTab;
	}

	public void removerContato() {
		try {
			if (activeTab >= 0) {
				contato = lista.get(activeTab);
				if (contato.isExiste()) {
					contatos.remover(contato);
					FacesUtil.addInfoMessage("removido com sucesso");
				}
				lista.remove(activeTab);
				activeTab = activeTab - 1;
				if (activeTab >= 0)
					contato = lista.get(activeTab);
			}
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onTabChange(TabChangeEvent event) {
		contato = lista.get(activeTab);
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
		lista = contatos.porPessoa(pessoa);
		if (lista != null && lista.size() > 0)
			activeTab = 0;
	}

	public void gravarContato() {
		try {
			for (Contato contato : lista) {
				if (!Suporte.stringComValor(contato.getNome()))
					FacesUtil.addErrorMessage("nome incoreto para contato");
			}
			lista = contatos.guardar(lista);
			if (lista.size() > 0)
				FacesUtil.addInfoMessage("gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

}