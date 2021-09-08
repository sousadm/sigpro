package com.sousa.sigpro.controller.clinica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.clinica.Convenio;
import com.sousa.sigpro.repository.Convenios;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class ClinicaConvenioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Convenios convenios;
	@Inject
	private Seguranca seguranca;

	private boolean listando;
	private Convenio convenio;
	private List<Convenio> lista;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			convenio = new Convenio();
			lista = new ArrayList<>();
			pesquisar();
		}
	}

	public void pesquisar() {
		listando = true;
		lista = convenios.lista();
	}

	public void salvar() {
		try {
			convenio = convenios.guardar(convenio);
			pesquisar();
			FacesUtil.addInfoMessage("Registro salvo com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void novo() {
		listando = false;
		convenio = new Convenio();
		convenio.setEmpresa(seguranca.getPessoaLogadoOrigem());
	}

	public void excluir(Convenio convenio) {
		try {
			convenios.Excluir(convenio);
			pesquisar();
			FacesUtil.addInfoMessage("Registro excluído com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void cancela() {
		listando = true;
	}

	public Convenio getConvenio() {
		return convenio;
	}

	public void setConvenio(Convenio convenio) {
		listando = false;
		this.convenio = convenio;
	}

	public List<Convenio> getLista() {
		return lista;
	}

	public void setLista(List<Convenio> lista) {
		this.lista = lista;
	}

	public boolean isListando() {
		return listando;
	}

	public String getTituloCadastro() {
		return convenio.isExiste() ? "Editando" : "Incluindo";
	}

}