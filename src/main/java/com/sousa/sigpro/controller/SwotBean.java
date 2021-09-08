package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Swot;
import com.sousa.sigpro.model.tipo.TipoImportancia;
import com.sousa.sigpro.model.tipo.TipoProbabilidade;
import com.sousa.sigpro.model.tipo.TipoSwot;
import com.sousa.sigpro.repository.Swots;
import com.sousa.sigpro.util.jpa.Transactional;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SwotBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Swots swots;

	private Boolean naoPodeAnalisar;
	private Swot swot;
	private List<Swot> lista;

	public SwotBean() {
		limpar();
	}

	public Boolean getNaoPodeAnalisar() {
		return naoPodeAnalisar;
	}

	private void limpar() {
		this.naoPodeAnalisar = true;
		swot = new Swot();
		swot.setData(new Date());
	}

	public List<Swot> getLista() {
		return lista;
	}

	public void setLista(List<Swot> lista) {
		this.lista = lista;
	}

	public Swot getSwot() {
		return swot;
	}

	public void setSwot(Swot swot) {
		this.swot = swot;
	}

	@Transactional
	public void salvar() {
		swot = swots.guardar(swot);
		FacesUtil.addInfoMessage("Registro salvo com sucesso!");
	}

	@Transactional
	public void excluir(Swot swot) {
		TipoSwot tipo = swot.getTipo();
		swots.remover(swot);

		if (tipo == TipoSwot.FORCA) {
			forca();
		} else if (tipo == TipoSwot.OPORTUNIDADE) {
			oportunidade();
		} else if (tipo == TipoSwot.FRAQUEZA) {
			fraqueza();
		} else if (tipo == TipoSwot.AMEACA) {
			ameaca();
		} else {
			pesquisar();
		}

		FacesUtil.addInfoMessage("Registro excluído com sucesso.");
	}

	public void pesquisar() {
		lista = swots.lista();
		this.naoPodeAnalisar = lista.size() == 0;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			pesquisar();
		}
	}

	public boolean isEditando() {
		return this.swot.getId() != null;
	}

	public void forca() {
		this.naoPodeAnalisar = true;
		lista = swots.lista(TipoSwot.FORCA);
	}

	public void fraqueza() {
		this.naoPodeAnalisar = true;
		lista = swots.lista(TipoSwot.FRAQUEZA);
	}

	public void ameaca() {
		this.naoPodeAnalisar = true;
		lista = swots.lista(TipoSwot.AMEACA);
	}

	public void oportunidade() {
		this.naoPodeAnalisar = true;
		lista = swots.lista(TipoSwot.OPORTUNIDADE);
	}

	public TipoSwot[] getTipos() {
		return TipoSwot.values();
	}

	public TipoImportancia[] getImportancias() {
		return TipoImportancia.values();
	}

	public TipoProbabilidade[] getTipoProbabilidades() {
		return TipoProbabilidade.values();
	}

}