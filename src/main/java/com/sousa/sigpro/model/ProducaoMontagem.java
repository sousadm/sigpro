package com.sousa.sigpro.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoComando;

@Entity
@DiscriminatorValue("MONTAGEM")
public class ProducaoMontagem extends Producao {

	private MontagemItem montagemItem;
	private double quantidade;

	public ProducaoMontagem() {
		this.getCronologia().setInicio(new Date());
		montagemItem = new MontagemItem();
		quantidade = 0;
	}

	@ManyToOne
	public MontagemItem getMontagemItem() {
		return montagemItem;
	}

	public void setMontagemItem(MontagemItem montagemItem) {
		this.montagemItem = montagemItem;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	@Transient
	public boolean isPodeEncerrar() {
		return isPodePausar();
	}

	@Transient
	public boolean isPodePausar() {
		if (montagemItem != null && montagemItem.getProduto() != null)
			return this.getComando().getTipo() == TipoComando.INICIAR
					|| this.getComando().getTipo() == TipoComando.REINICIAR;
		return false;
	}

	@Transient
	public boolean isPodeReiniciar() {
		if (montagemItem != null && montagemItem.getProduto() != null)
			return this.getComando().getTipo() == TipoComando.PAUSAR;
		return false;
	}

	@Transient
	public boolean isPodeReabrir() {
		if (montagemItem != null && montagemItem.getProduto() != null)
			return this.getComando().getTipo() == TipoComando.ENCERRAR;
		return false;
	}

}
