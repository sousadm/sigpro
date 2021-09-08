package com.sousa.sigpro.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoStatusProducao;

@Entity
@DiscriminatorValue("EXPEDICAO")
@NamedQueries({
		@NamedQuery(name = "Producao.tempoPorOperador", 
				query = "select p from ProducaoExpedicao p where p.ordemItemProducao = :item and p.comando.tipo in ('INICIAR','REINICIAR') and p.operador = :operador and p.titulo is null ") })
public class ProducaoExpedicao extends Producao {

	private ExpedicaoItem ordemItemProducao;

	public ProducaoExpedicao() {
		ordemItemProducao = new ExpedicaoItem();
	}

	@ManyToOne
	public ExpedicaoItem getOrdemItemProducao() {
		return ordemItemProducao;
	}

	public void setOrdemItemProducao(ExpedicaoItem ordemItemProducao) {
		this.ordemItemProducao = ordemItemProducao;
	}

	@Transient
	public boolean isPodeEncerrar() {
		return isPodePausar();
	}

	@Transient
	public boolean isPodePausar() {
		if (ordemItemProducao != null && ordemItemProducao.getProduto() != null)
			return ordemItemProducao.getStatus() == TipoStatusProducao.PRODUCAO;
		return false;
	}

	@Transient
	public boolean isPodeReiniciar() {
		if (ordemItemProducao != null && ordemItemProducao.getProduto() != null)
			return ordemItemProducao.getStatus() == TipoStatusProducao.PAUSA;
		return false;
	}

	@Transient
	public boolean isPodeReabrir() {
		if (ordemItemProducao != null && ordemItemProducao.getProduto() != null)
			return ordemItemProducao.getStatus() == TipoStatusProducao.CONCLUIDO;
		return false;
	}

}