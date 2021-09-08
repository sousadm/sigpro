package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFMeioPagamento;
import com.sousa.sigpro.model.tipo.TipoModeloFiscal;
import com.sousa.sigpro.util.Suporte;

@Entity
@NamedQueries({
		@NamedQuery(name = "NotaFiscal.items", query = "select i from NotaFiscalItem i where i.nota = :nota order by i.id"),
		@NamedQuery(name = "NotaFiscal.pagamentos", query = "select p from NotaFiscalPgto p where p.nota = :nota order by p.id"),
		@NamedQuery(name = "NotaFiscal.ultimaPorPessoa", query = "select n from NotaFiscal n WHERE n.fiscal.modelo like :modelo and n.responsavel = :responsavel") })
public class NotaFiscal implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String chaveCanc;
	private String qrcode;
	private Long caixa;
	private String satSerie;
	private Date dataCadastro;

	private Fiscal fiscal;
	private Issqn issqn;
	private Pessoa empresa;
	private Usuario responsavel;
	private Transportador transportador;

	private double valorFrete;
	private double valorSeguro;
	private double valorDesconto;
	private double valorOutros;

	private List<NotaFiscalItem> items;
	private List<NotaFiscalPgto> pgtos;
	private List<NotaFiscalVolume> volumes;

	public NotaFiscal() {
		fiscal = new Fiscal();
		issqn = new Issqn();
		responsavel = new Usuario();
		transportador = new Transportador();
		pgtos = new ArrayList<NotaFiscalPgto>();
		items = new ArrayList<NotaFiscalItem>();
		volumes = new ArrayList<>();
	}

	public void distribuirValorOutros() {

		double valorProduto = this.getValorProduto();
		double diferenca = valorOutros;

		if (items.size() > 1) {
			for (NotaFiscalItem item : items) {
				item.setValorOutros(Suporte.arredondaValor(valorOutros * (item.getValorProduto() / valorProduto), 2));
				diferenca = diferenca - item.getValorOutros();
			}
			diferenca = diferenca + items.get(0).getValorOutros();
			items.get(0).setValorOutros(diferenca);
		} else {
			items.get(0).setValorOutros(valorOutros);
		}
	}

	public void distribuirValorSeguro() {

		double valorProduto = this.getValorProduto();
		double diferenca = valorSeguro;

		if (items.size() > 1) {
			for (NotaFiscalItem item : items) {
				item.setValorSeguro(Suporte.arredondaValor(valorSeguro * (item.getValorProduto() / valorProduto), 2));
				diferenca = diferenca - item.getValorSeguro();
			}
			diferenca = diferenca + items.get(0).getValorSeguro();
			items.get(0).setValorSeguro(diferenca);
		} else {
			items.get(0).setValorSeguro(valorSeguro);
		}
	}

	public void distribuirValorFrete() {

		double valorProduto = this.getValorProduto();
		double diferenca = valorFrete;

		if (items.size() > 1) {
			for (NotaFiscalItem item : items) {
				item.setValorFrete(Suporte.arredondaValor(valorFrete * (item.getValorProduto() / valorProduto), 2));
				diferenca = diferenca - item.getValorFrete();
			}
			diferenca = diferenca + items.get(0).getValorFrete();
			items.get(0).setValorFrete(diferenca);
		} else {
			items.get(0).setValorFrete(valorFrete);
		}
	}

	public void distribuirValorDesconto() {

		double valorProduto = this.getValorProduto();
		double diferenca = valorDesconto;

		if (items.size() > 1) {
			for (NotaFiscalItem item : items) {
				item.setValorDescontoRateio(
						Suporte.arredondaValor(valorDesconto * (item.getValorProduto() / valorProduto), 2));
				diferenca = diferenca - item.getValorDescontoRateio();
			}
			diferenca = diferenca + items.get(0).getValorDescontoRateio();
			items.get(0).setValorDescontoRateio(diferenca);
		} else {
			items.get(0).setValorDescontoRateio(valorDesconto);
		}
	}

	@Embedded
	public Issqn getIssqn() {
		return issqn;
	}

	public void setIssqn(Issqn issqn) {
		this.issqn = issqn;
	}

	@Transient
	public double getValorServico() {
		return issqn.getValorServicos();
	}

	@Transient
	public double getValorProduto() {
		double valor = 0;
		for (NotaFiscalItem item : items) {
			valor = valor + item.getValorProduto();
		}
		return valor;
	}

	@Transient
	public double getValorTotal() {
		double valor = issqn.getValorServicos();
		for (NotaFiscalItem item : items) {
			valor = valor + item.getValorItem();
		}
		return valor;
	}

	@OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<NotaFiscalVolume> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<NotaFiscalVolume> volumes) {
		this.volumes = volumes;
	}

	@OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<NotaFiscalPgto> getPgtos() {
		return pgtos;
	}

	public void setPgtos(List<NotaFiscalPgto> pgtos) {
		this.pgtos = pgtos;
	}

	@OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<NotaFiscalItem> getItems() {
		return items;
	}

	public void setItems(List<NotaFiscalItem> items) {
		this.items = items;
	}

	@Embedded
	public Fiscal getFiscal() {
		return fiscal;
	}

	public void setFiscal(Fiscal fiscal) {
		this.fiscal = fiscal;
	}

	@Column(length = 44)
	public String getChaveCanc() {
		return chaveCanc;
	}

	public void setChaveCanc(String chaveCanc) {
		this.chaveCanc = chaveCanc;
	}

	@ManyToOne
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	@ManyToOne
	public Usuario getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Usuario responsavel) {
		this.responsavel = responsavel;
	}

	@Column(length = 20)
	public String getSatSerie() {
		return satSerie;
	}

	public void setSatSerie(String satSerie) {
		this.satSerie = satSerie;
	}

	public Long getCaixa() {
		return caixa;
	}

	public void setCaixa(Long caixa) {
		this.caixa = caixa;
	}

	@Column(length = 345)
	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(precision = 12, scale = 2)
	public double getValorOutros() {
		return valorOutros;
	}

	public void setValorOutros(double valorOutros) {
		this.valorOutros = valorOutros;
	}

	@Column(precision = 12, scale = 2)
	public double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	@Column(precision = 12, scale = 2)
	public double getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(double valorFrete) {
		this.valorFrete = valorFrete;
	}

	@Column(precision = 12, scale = 2)
	public double getValorSeguro() {
		return valorSeguro;
	}

	public void setValorSeguro(double valorSeguro) {
		this.valorSeguro = valorSeguro;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@ManyToOne
	public Transportador getTransportador() {
		return transportador;
	}

	public void setTransportador(Transportador transportador) {
		this.transportador = transportador;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isPodeConsultarStatus() {
		return isExiste() && fiscal.getModelo() != null && fiscal.getDataEmissao() != null;
	}

	@Transient
	public boolean isPodeEmitirDocumento() {
		return isExiste() && fiscal.getModelo() != null && fiscal.getDataEmissao() == null
				&& fiscal.getNumero() == null;
	}

	@Transient
	public boolean isPodeEmitirCorrecao() {
		return fiscal.getNumero() != null && fiscal.getDataCancelamento() == null;
	}

	@Transient
	public boolean isPodeEnviarEmail() {
		return fiscal.getNumero() != null;
	}

	@Transient
	public boolean isNotaTipoNFe() {
		return fiscal.getModelo() != null && fiscal.getModelo().equals(TipoModeloFiscal.NFE);
	}

	@Transient
	public boolean isNotaTipoNFSe() {
		return fiscal.getModelo() != null && fiscal.getModelo().equals(TipoModeloFiscal.NFSE);
	}

	@Transient
	public boolean isPodeImprimir() {
		return fiscal.getPdfFile() != null && fiscal.getDataCancelamento() == null;
	}

	@Transient
	public boolean isPodeImprimirCancelamento() {
		return fiscal.getCaminhoXmlCancelamento() != null && fiscal.getDataCancelamento() != null;
	}

	@Transient
	public double getValorPgto() {
		double valor = 0;
		if (pgtos != null)
			for (NotaFiscalPgto pgto : pgtos) {
				valor = valor + pgto.getValor();
			}
		return valor;
	}

	@Transient
	public double getValorPgtoDinheiro() {
		double valor = 0;
		if (pgtos != null)
			for (NotaFiscalPgto pgto : pgtos) {
				if (pgto.getMeioPgto().equals(NFMeioPagamento.DINHEIRO))
					valor = valor + pgto.getValor();
			}
		return valor;
	}

	@Transient
	public double getValorTroco() {
		double troco = 0;
		if ((getValorPgto() - getValorPgtoDinheiro()) < getValorTotal() && (getValorPgto() > getValorTotal())) {
			troco = getValorPgto() - getValorTotal();
		}
		return troco;
	}

	@Transient
	public double getValorFalta() {
		double valor = this.getValorTotal() - this.getValorPgto();
		if (valor < 0)
			valor = 0;
		return valor;
	}

	@Transient
	public boolean isValoresEmConformidade() {
		double vtt = Math.ceil(this.getValorTotal());
		double ptt = Math.ceil(this.getValorPgto());

		return vtt == ptt;
	}

	@Transient
	public boolean isPodeImprimirCorrecao() {
		return fiscal.getCaminhoPdfCarta() != null;
	}

	@Transient
	public boolean isPodeImportarPedido() {
		return fiscal.getModelo() != null && fiscal.getDataEmissao() == null && !fiscal.getOperacao().isDevolucao();
	}

	@Transient
	public boolean isPodeImportarCompra() {
		return fiscal.getModelo() != null && fiscal.getDataEmissao() == null && fiscal.getOperacao().isDevolucao();
	}

	@Transient
	public boolean isNotaDevolucao() {
		return fiscal.getModelo() != null && fiscal.getOperacao().isDevolucao();
	}

	@Transient
	public boolean isPodeCancelarNota() {
		return fiscal.getDataCancelamento() == null && fiscal.getNumero() != null && fiscal.getNumero().intValue() > 0;
	}

	@Transient
	public boolean isPodeEditar() {
		return fiscal != null && fiscal.getOperacao() != null && fiscal.getOperacao().getCfop() != null
				&& fiscal.getCodigoVerificacao() == null && fiscal.getDataEmissao() == null;
	}

	@Transient
	public boolean isPodeAdicionarProduto() {
		return isPodeEditar() && fiscal.getCliente().isExiste();
	}

	@Transient
	public boolean isProducao() {
		return fiscal != null && fiscal.getAmbiente() == DFAmbiente.PRODUCAO;
	}

	@Transient
	public double getBaseICMS() {
		double valor = 0;
		for (NotaFiscalItem item : items)
			if (item.getIcms() != null)
				valor = valor + item.getIcms().getBaseICMS();
		return valor;
	}

	@Transient
	public double getValorICMS() {
		double valor = 0;
		for (NotaFiscalItem item : items)
			if (item.getIcms() != null)
				valor = valor + item.getIcms().getValorICMS();
		return valor;
	}

	@Transient
	public boolean isPodeModificarAmbiente() {
		return fiscal != null && fiscal.getDataEmissao() == null && fiscal.getDataCancelamento() == null
				&& fiscal.getAmbiente().equals(DFAmbiente.HOMOLOGACAO);
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
		NotaFiscal other = (NotaFiscal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}