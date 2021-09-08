package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.Questionario;
import com.sousa.sigpro.repository.Questionarios;

@Named
@ViewScoped
public class SelecaoQuestionarioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Questionarios questionarios;
	private String descricao;
	private List<Questionario> lista;

	public SelecaoQuestionarioBean() {
		descricao = "";
	}

	public void pesquisar() {
		lista = questionarios.listaComPergunta(descricao);
	}

	public void selecionar(Questionario questionario) {
		Questionario choosen = questionarios.porId(questionario.getId());
		PrimeFaces.current().dialog().closeDynamic(choosen);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Questionario> getLista() {
		return lista;
	}

	public void setLista(List<Questionario> lista) {
		this.lista = lista;
	}

}