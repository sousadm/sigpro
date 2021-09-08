package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Planner;
import com.sousa.sigpro.model.Swot;
import com.sousa.sigpro.model.tipo.TipoSwot;
import com.sousa.sigpro.repository.Planners;
import com.sousa.sigpro.repository.Swots;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SwotPlannerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Swots swots;

	@Inject
	private Planners planners;

	private Planner planner;
	private List<Planner> lstPlanner;
	
	public void aplicar() {
		planner = new Planner();
	}

	public List<Planner> getLstPlanner() {
		return lstPlanner;
	}

	public Planner getPlanner() {
		return planner;
	}

	public void setPlanner(Planner planner) {
		this.planner = planner;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			planner = new Planner();
			analisar();
		}
	}

	public void excluirItem(int linha) {
		this.lstPlanner.remove(linha);
		planner = new Planner();
	}

	public boolean getNaoPodeGravar() {
		return this.lstPlanner.size() == 0;
	}

	public boolean getNaoPodeAplicar() {
		return this.planner.getPorque() == null;
	}

	public void gravar() {
		try {
			int x = 0;
			for (Planner planner : lstPlanner) {
				x++;
				if (planner.getQue() == null) {
					throw new IllegalArgumentException("Item [" + x
							+ "] sem definição do que fazer?");
				}
			}

			planners.guardar(lstPlanner);
			lstPlanner = new ArrayList<Planner>();
			FacesUtil.addInfoMessage("Lista exportada para plano de ação.");

		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro: " + e.getMessage());
		}
	}

	public void analisar() {

		lstPlanner = new ArrayList<Planner>();

		for (Swot forca : swots.lista(TipoSwot.FORCA)) {

			for (Swot oport : swots.lista(TipoSwot.OPORTUNIDADE)) {
				Planner plano = new Planner();
				plano.setPorque("com potencializar " + forca.getDescricao()
						+ " com base em " + oport.getDescricao());
				lstPlanner.add(plano);
			}

			for (Swot ameaca : swots.lista(TipoSwot.AMEACA)) {
				Planner plano = new Planner();
				plano.setPorque("como minimizar " + ameaca.getDescricao()
						+ " com base em " + forca.getDescricao());
				lstPlanner.add(plano);
			}

		}

		for (Swot fraqueza : swots.lista(TipoSwot.FRAQUEZA)) {

			for (Swot oport : swots.lista(TipoSwot.OPORTUNIDADE)) {
				Planner plano = new Planner();
				plano.setPorque("como reduzir " + fraqueza.getDescricao()
						+ " aproveitando " + oport.getDescricao());
				lstPlanner.add(plano);
			}

			for (Swot ameaca : swots.lista(TipoSwot.AMEACA)) {
				Planner plano = new Planner();
				plano.setPorque("considando " + fraqueza.getDescricao() + " e "
						+ ameaca.getDescricao() + ", como reduzir perdas");
				lstPlanner.add(plano);
			}

		}

	}
}