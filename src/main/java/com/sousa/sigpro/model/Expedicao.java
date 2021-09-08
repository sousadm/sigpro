package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.sousa.sigpro.model.tipo.TipoExpedicao;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.util.Suporte;

@Entity(name = "expedicao")
@NamedQueries({
		@NamedQuery(name = "Expedicao.listaPorServico", query = "select e from ExpedicaoItem i join i.expedicao e where e.empresa = :origem"),
		@NamedQuery(name = "Expedicao.lista", query = "select e from expedicao e where e.empresa = :origem"),
		@NamedQuery(name = "Expedicao.listaConvidado", query = "select e from expedicao e where e.cliente = :cliente") })
public class Expedicao implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date dataCadastro;
	private String observacao;
	private Date dataEntrega;
	private Date dataCancelamento;
	private Date dataEmail;
	private Date dataEmissao;
	private Date dataReabertura;
	private Expedicao origem;
	private TipoExpedicao tipo;
	private Vendedor vendedor;
	private Cliente cliente;
	private Pessoa empresa;
	private Endereco enderecoEntrega;
	private RegraPgto regraPgto;

	private double valorFrete;
	private double valorDesconto;
	private double valorSubTotal;
	private double valorTotal;
	private double valorTotalProduto;
	private double valorTotalServico;

	private List<ExpedicaoItem> items;
	private List<Pacote> pacotes;

	public Expedicao() {
		enderecoEntrega = new Endereco();
		tipo = TipoExpedicao.ORC;
		items = new ArrayList<>();
		pacotes = new ArrayList<>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public RegraPgto getRegraPgto() {
		return regraPgto;
	}

	public void setRegraPgto(RegraPgto regraPgto) {
		this.regraPgto = regraPgto;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "vendedor_id", nullable = false)
	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = false)
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@Column(length = 3, nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoExpedicao getTipo() {
		return tipo;
	}

	public void setTipo(TipoExpedicao tipo) {
		this.tipo = tipo;
	}

	@OneToMany(mappedBy = "expedicao", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Pacote> getPacotes() {
		return pacotes;
	}

	public void setPacotes(List<Pacote> pacotes) {
		this.pacotes = pacotes;
	}

	@OneToMany(mappedBy = "expedicao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<ExpedicaoItem> getItems() {
		return items;
	}

	public void setItems(List<ExpedicaoItem> items) {
		this.items = items;
	}

	@ManyToOne
	public Expedicao getOrigem() {
		return origem;
	}

	public void setOrigem(Expedicao origem) {
		this.origem = origem;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Endereco getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public void setEnderecoEntrega(Endereco enderecoEntrega) {
		this.enderecoEntrega = enderecoEntrega;
	}

	@Transient
	public boolean isNovo() {
		return getId() == null;
	}

	@Transient
	public boolean isExistente() {
		return !isNovo();
	}

	@Column(precision = 12, scale = 2)
	public double getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(double valorFrete) {
		this.valorFrete = valorFrete;
	}

	@Column(precision = 12, scale = 2)
	public double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "data_email")
	public Date getDataEmail() {
		return dataEmail;
	}

	public void setDataEmail(Date dataEmail) {
		this.dataEmail = dataEmail;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "data_pedido")
	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "data_reabertura")
	public Date getDataReabertura() {
		return dataReabertura;
	}

	public void setDataReabertura(Date dataReabertura) {
		this.dataReabertura = dataReabertura;
	}

	@ManyToOne
	@JoinColumn(name = "pessoa_id", nullable = false)
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
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
		Expedicao other = (Expedicao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Transient
	public boolean isValorTotalNegativo() {
		return this.getValorTotal() < 0;
	}

	@Transient
	public boolean isEmitido() {
		return dataEmissao != null && dataCancelamento == null;
	}

	@Transient
	public boolean isNaoEmissivel() {
		return !this.isEmissivel();
	}

	@Transient
	private boolean isEmissivel() {
		return this.isExistente() && this.isOrcamento() && !this.isCancelado();
	}

	@Transient
	private boolean isCancelado() {
		return TipoExpedicao.CAN.equals(this.getTipo());

	}

	@Transient
	private boolean isCancelavel() {
		return this.isExiste() && !this.isCancelado();
	}

	@Transient
	public boolean isNaoCancelavel() {
		return !this.isCancelavel();
	}

	@Transient
	public boolean isOrcamento() {
		return TipoExpedicao.ORC.equals(this.getTipo());
	}

	@Transient
	public boolean isNaoEnviavelPorEmail() {
		return this.isNovo() || this.isCancelado();
	}

	@Transient
	public boolean isPodeEditar() {
		return id == null || dataCancelamento == null && dataEmissao == null;
	}

	@Transient
	private boolean isPodeReabrir() {
		return this.isExistente() && !TipoExpedicao.ORC.equals(this.getTipo()) && !this.isCancelado() && !this.isNovo();
	}

	@Transient
	public boolean isNaoPodeReabrir() {
		return !this.isPodeReabrir();
	}

	@Transient
	public boolean isAlteravel() {
		return this.isOrcamento();
	}

	@Transient
	public boolean isNaoAlteravel() {
		return !this.isAlteravel();
	}

	@Transient
	public boolean isPodeGerarOS() {
		boolean pode = false;
		for (ExpedicaoItem item : items) {
			if (item.getServico() == null && item.getProduto().isTipoServico())
				pode = true;
			if (pode)
				break;
		}
		return pode && isPodeEditar();
	}

	@Transient
	public boolean isTemServico() {
		boolean tem = false;
		for (ExpedicaoItem item : items) {
			if (tem == false) {
				tem = item.getProduto().getCategoria().getCategoriaPai().getTipoProduto().equals(TipoProduto.SERVICO);
			}
		}
		return tem;
	}

	@Transient
	public double getDescontoMargemLucro() {
		double valor = 0;
		for (ExpedicaoItem item : items) {
			valor = valor + item.getQuantidade() * item.getDescontoMargemLucro();
		}
		return valor;
	}

	@Transient
	public double getTotalCargaTributaria() {
		double valor = 0;
		for (ExpedicaoItem item : items) {
			valor += item.getValorTotal() * item.getCusto().getTributo() / 100;
		}
		return valor;
	}

	@Transient
	public boolean isTemLucro() {
		return getTotalLucroLiquido() > 0;
	}

	@Column(precision = 12, scale = 2)
	public double getValorTotalProduto() {
		return valorTotalProduto;
	}

	public void setValorTotalProduto(double valorTotalProduto) {
		this.valorTotalProduto = valorTotalProduto;
	}

	@Column(precision = 12, scale = 2)
	public double getValorTotalServico() {
		return valorTotalServico;
	}

	public void setValorTotalServico(double valorTotalServico) {
		this.valorTotalServico = valorTotalServico;
	}

	@Column(precision = 12, scale = 2)
	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}

	@Column(precision = 12, scale = 2)
	public double getValorSubTotal() {
		return valorSubTotal;
	}

	public void setValorSubTotal(double valorSubTotal) {
		this.valorSubTotal = valorSubTotal;
	}

	@Transient
	public double getTotalMargemContribuicao() {
		double valor = 0;
		for (ExpedicaoItem item : items) {
			valor = valor + item.getValorBruto() * (item.getCusto().getResidual() + item.getCusto().getMargem()) / 100;
		}
		return valor;
	}

	@Transient
	public double getTotalCustoReposicao() {
		double valor = 0;
		for (ExpedicaoItem item : items)
			if (!(item.getProduto().isTipoServico() || item.getProduto().isTipoImobilizado())) {
				valor = valor + item.getQuantidade() * item.getProduto().getPrecoCusto();
			}
		return valor;
	}

	@Transient
	public double getValorComissaoVenda() {
		double valor = 0;
		for (ExpedicaoItem item : items)
			valor = valor + item.getValorComissao();
		return valor;
	}

	@Transient
	public double getTotalLucroLiquido() {
		return valorTotal - getTotalMargemContribuicao() - getTotalCargaTributaria() - getTotalCustoReposicao()
				- getValorComissaoVenda();
	}

	public void calcular() {

		double total = 0;
		double resto = 0;

		for (ExpedicaoItem item : items)
			item.setDescontoExtra(0);

		if (valorDesconto > 0) {
			resto = valorDesconto;
			for (ExpedicaoItem item : items)
				total = total + item.getValorBruto();
			if (total > 0) {
				for (ExpedicaoItem item : items) {
					item.setDescontoExtra(Suporte.arredondaValor(item.getValorBruto() / total * valorDesconto));
					resto = resto - item.getDescontoExtra();
				}
				if (!items.isEmpty() && resto > 0) {
					ExpedicaoItem item = items.get(0);
					resto = resto + item.getDescontoExtra();
					items.get(0).setDescontoExtra(resto);
				}
			}
		}

		valorTotalServico = 0;
		valorTotalProduto = 0;
		for (ExpedicaoItem item : items) {
			if (item.getProduto().isTipoServico())
				valorTotalServico = valorTotalServico + item.getValorTotal();
			else
				valorTotalProduto = valorTotalProduto + item.getValorTotal();
		}

		valorSubTotal = valorTotalProduto + valorTotalServico;
		valorTotal = valorSubTotal + valorFrete - valorDesconto;

	}

	@Transient
	public String getTitutoExpedicao() {
		String valor = "ORÇAMENTO DE VENDAS";
		if (dataCancelamento != null)
			valor = "**** CANCELADO ****";
		else if (dataEmissao != null)
			valor = "PEDIDO DE VENDAS";
		return valor;
	}

	@Transient
	public String getStatusColor() {
		String cor = "black";
		if (dataCancelamento != null)
			cor = "red";
		else if (dataEmissao != null)
			cor = "blue";
		return cor;
	}

	@Transient
	public boolean isTemMunicipioEntrega() {
		return enderecoEntrega != null && enderecoEntrega.getMunicipio() != null;
	}

}