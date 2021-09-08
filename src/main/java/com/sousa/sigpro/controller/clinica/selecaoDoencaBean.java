package com.sousa.sigpro.controller.clinica;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.clinica.Doenca;
import com.sousa.sigpro.repository.Doencas;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class selecaoDoencaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Doencas doencas;
	private String codigo;
	private String descricao;
	private List<Doenca> lista;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {

		}
	}

	public void pesquisar() {
		lista = doencas.lista(codigo, descricao);
	}

	public void selecionar(Doenca cid) {
		PrimeFaces.current().dialog().closeDynamic(cid);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Doenca> getLista() {
		return lista;
	}

	public void setLista(List<Doenca> lista) {
		this.lista = lista;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}