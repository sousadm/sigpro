package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.sousa.sigpro.model.tipo.TipoCheckListLocVeiculo;

@Embeddable
public class ServicoLocacao implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<TipoCheckListLocVeiculo> locacaoCheckList;
	private int locacaoNivelSaida;
	private int locacaoNivelRetorno;
	private Long locacaoOdometroFinal;
	private double locacaoFranquiaSinistro;

	public ServicoLocacao() {
		locacaoNivelSaida = 0;
		locacaoNivelRetorno = 0;
		locacaoOdometroFinal = 0L;
		locacaoFranquiaSinistro = 0;
	}

	@ElementCollection
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public List<TipoCheckListLocVeiculo> getLocacaoCheckList() {
		return locacaoCheckList;
	}

	public void setLocacaoCheckList(List<TipoCheckListLocVeiculo> locacaoCheckList) {
		this.locacaoCheckList = locacaoCheckList;
	}

	public int getLocacaoNivelSaida() {
		return locacaoNivelSaida;
	}

	public void setLocacaoNivelSaida(int locacaoNivelSaida) {
		this.locacaoNivelSaida = locacaoNivelSaida;
	}

	public int getLocacaoNivelRetorno() {
		return locacaoNivelRetorno;
	}

	public void setLocacaoNivelRetorno(int locacaoNivelRetorno) {
		this.locacaoNivelRetorno = locacaoNivelRetorno;
	}

	public Long getLocacaoOdometroFinal() {
		return locacaoOdometroFinal;
	}

	public void setLocacaoOdometroFinal(Long locacaoOdometroFinal) {
		this.locacaoOdometroFinal = locacaoOdometroFinal;
	}

	@Column(precision = 12, scale = 2)
	public double getLocacaoFranquiaSinistro() {
		return locacaoFranquiaSinistro;
	}

	public void setLocacaoFranquiaSinistro(double locacaoFranquiaSinistro) {
		this.locacaoFranquiaSinistro = locacaoFranquiaSinistro;
	}
}