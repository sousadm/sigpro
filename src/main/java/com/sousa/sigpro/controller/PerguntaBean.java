package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Pergunta;
import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.model.tipo.TipoPergunta;
import com.sousa.sigpro.repository.Perguntas;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PerguntaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Perguntas perguntas;

	private Pergunta pergunta;
	private Profissao profissao;
	private List<Pergunta> lista;

	public PerguntaBean() {
		pergunta = new Pergunta();
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
//			profissoes.lista();
		}
	}

	public List<Pergunta> getLista() {
		return lista;
	}

	public List<Profissao> getLstProfissao() {
		return null;
//		return profissoes.lista();
	}

	public TipoPergunta[] getTipos() {
		return TipoPergunta.values();
	}

	public Profissao getProfissao() {
		return profissao;
	}

	public void setProfissao(Profissao profissao) {
		this.profissao = profissao;
	}

	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public void salvar() {
		try {
			perguntas.guardar(pergunta);
			pesquisar();
			FacesUtil.addInfoMessage("Registro gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(Pergunta pergunta) {
		try {
			perguntas.excluir(pergunta);
			lista.remove(pergunta);
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void novo() {
		pergunta = new Pergunta();
	}

	public void pesquisar() {
		lista = perguntas.lista(profissao);
	}
}