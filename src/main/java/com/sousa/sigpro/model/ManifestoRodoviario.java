package com.sousa.sigpro.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Embeddable
public class ManifestoRodoviario {

	private String RNTRC;
	private Veiculo veiculo;
	private List<ManifestoCiot> listaCiot;

	@Column(length = 8)
	public String getRNTRC() {
		return RNTRC;
	}

	public void setRNTRC(String rNTRC) {
		RNTRC = rNTRC;
	}

	@ManyToOne
	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	@OneToMany(mappedBy = "manifesto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<ManifestoCiot> getListaCiot() {
		return listaCiot;
	}

	public void setListaCiot(List<ManifestoCiot> listaCiot) {
		this.listaCiot = listaCiot;
	}

}