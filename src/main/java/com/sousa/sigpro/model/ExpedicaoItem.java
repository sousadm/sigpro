package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoStatusProducao;
import com.sousa.sigpro.model.tipo.TipoUnidade;

@Entity
@Table(name = "expedicao_item")
@NamedQueries({
//		@NamedQuery(name = "ExpedicaoItem.listaComissionar", query = "select distinct e from Producao p join p.ordemItemProducao e where p.operador = :operador and p.titulo is null "),
		@NamedQuery(name = "ExpedicaoItem.servicoPendente", query = "select s from ExpedicaoItem s where s.servico = :ordem and s.cronologia.termino is null "),
		@NamedQuery(name = "ExpedicaoItem.servicoLivre", query = "select s from ExpedicaoItem s where s.expedicao = :expedicao and s.servico is null and s.produto.categoria.categoriaPai.tipoProduto = :tipo "),
		@NamedQuery(name = "ExpedicaoItem.listaPorServico", query = "select s from ExpedicaoItem s where s.servico = :ordem order by s.produto.categoria.categoriaPai.tipoProduto "),
		@NamedQuery(name = "ExpedicaoItem.listaExpedicao", query = "select s from ExpedicaoItem s where s.expedicao = :expedicao order by s.produto.categoria.categoriaPai.tipoProduto "),
		@NamedQuery(name = "ExpedicaoItem.listaExpedicaoNaoCancelada", query = "select s from ExpedicaoItem s where s.expedicao = :expedicao and s.dataCancelamento is null order by s.id "),
		@NamedQuery(name = "ExpedicaoItem.producao", query = "select s from ExpedicaoItem s where s.servico.responsavel.origem = :origem and s.servico.dataImpressao is not null and s.servico.cronologia.termino is null order by s.id desc") })
public class ExpedicaoItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private double quantidade;
	private double unitario;
	private double desconto;
	private double descontoExtra;
	private ExpedicaoItem origem;
	private Custo custo;
	private Produto produto;
	private Expedicao expedicao;
	private OrdemServico servico;
	private boolean selecionado;
	private Cronologia cronologia;
	private TipoStatusProducao status;
	private TipoUnidade unidade;
	private Date dataCancelamento;
	private Date dataComissao;
	private Date dataAgendamento;
	private String tempoToStr;
	private double valorComissao;

	public ExpedicaoItem() {
		custo = new Custo();
		produto = new Produto();
		cronologia = new Cronologia();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Embedded
	public Custo getCusto() {
		return custo;
	}

	public void setCusto(Custo custo) {
		this.custo = custo;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public TipoUnidade getUnidade() {
		return unidade;
	}

	public void setUnidade(TipoUnidade unidade) {
		this.unidade = unidade;
	}

	@Enumerated(EnumType.STRING)
	public TipoStatusProducao getStatus() {
		return status;
	}

	public void setStatus(TipoStatusProducao status) {
		this.status = status;
	}

	@Embedded
	public Cronologia getCronologia() {
		return cronologia;
	}

	public void setCronologia(Cronologia cronologia) {
		this.cronologia = cronologia;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataComissao() {
		return dataComissao;
	}

	public void setDataComissao(Date dataComissao) {
		this.dataComissao = dataComissao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Column(precision = 12, scale = 4)
	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	@Column(precision = 12, scale = 2)
	public double getDescontoExtra() {
		return descontoExtra;
	}

	public void setDescontoExtra(double descontoExtra) {
		this.descontoExtra = descontoExtra;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getUnitario() {
		return unitario;
	}

	public void setUnitario(double unitario) {
		this.unitario = unitario;
	}

	@ManyToOne
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.unidade = produto.getUnidade();
		this.produto = produto;
	}

	@ManyToOne
	public Expedicao getExpedicao() {
		return expedicao;
	}

	public void setExpedicao(Expedicao expedicao) {
		this.expedicao = expedicao;
	}

	@ManyToOne
	public OrdemServico getServico() {
		return servico;
	}

	public void setServico(OrdemServico servico) {
		this.servico = servico;
	}

	@ManyToOne
	public ExpedicaoItem getOrigem() {
		return origem;
	}

	public void setOrigem(ExpedicaoItem origem) {
		this.origem = origem;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getDataAgendamento() {
		return dataAgendamento;
	}

	public void setDataAgendamento(Date dataAgendamento) {
		this.dataAgendamento = dataAgendamento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpedicaoItem other = (ExpedicaoItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Transient
	public double getDescontoResidual() {
		return produto.getValorUnitario() * custo.getResidual() / 100;
	}

	@Transient
	public double getValorDesconto() {
		return quantidade * desconto;
	}

	@Transient
	public double getValorBruto() {
		return unitario * quantidade;
	}

	@Transient
	public double getValorUnitarioTributavel() {
		return unitario * desconto;
	}

	public double getValorComissao() {
		return valorComissao;
	}

	public void setValorComissao(double valorComissao) {
		this.valorComissao = valorComissao;
	}

	@Transient
	public boolean isProdutoAssociado() {
		return this.getProduto() != null && this.getProduto().getId() != null;
	}

	@Transient
	public boolean isNaoPodeGerarOS() {
		return this.produto != null && this.produto.isTipoServico();
	}

	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public String getTempoToStr() {
		return tempoToStr;
	}

	public void setTempoToStr(String tempoToStr) {
		this.tempoToStr = tempoToStr;
	}

	@Transient
	public boolean isPodeExcluir() {
		return servico == null;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Transient
	public double getValorTotal() {
		return getValorBruto() - desconto;
	}

	@Transient
	public double getValorCOFINS() {
		return getValorBruto() * getProduto().getFiscal().getCofins() / 100;
	}

	@Transient
	public double getValorPIS() {
		return getValorBruto() * getProduto().getFiscal().getPis() / 100;
	}

	@Transient
	public double getDescontoMargemLucro() {
		return unitario * custo.getLucro() / 100;
	}

	@Transient
	public boolean isPodeReabrir() {
		return status == TipoStatusProducao.CONCLUIDO;
	}

	@Transient
	public boolean isPodeReiniciar() {
		return status == TipoStatusProducao.PAUSA;
	}

	@Transient
	public boolean isPodeIniciar() {
		return status == TipoStatusProducao.DISPONIVEL;
	}

	@Transient
	public boolean isPodePausar() {
		return status == TipoStatusProducao.PRODUCAO;
	}

	@Transient
	public boolean isPodeEncerrar() {
		return status == TipoStatusProducao.PRODUCAO;
	}

}