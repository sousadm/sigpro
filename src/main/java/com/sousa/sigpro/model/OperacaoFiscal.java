package com.sousa.sigpro.model;

import java.io.Serializable;

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
import javax.persistence.Transient;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFIndicadorFormaPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorPresencaComprador;
import com.sousa.sigpro.model.tipo.TipoMovimentacaoFiscal;
import com.sousa.sigpro.model.tipo.TipoNaturezaOperacaoNFSe;
import com.sousa.sigpro.model.tipo.TipoRegimeEspecial;
import com.sousa.sigpro.model.tipo.TipoTributacaoRps;

@Entity
@Table(name = "operacao")
@NamedQueries({ @NamedQuery(name = "OperacaoFiscal.lista", query = "select c from OperacaoFiscal c order by c.id"),
		@NamedQuery(name = "OperacaoFiscal.listaNFe", query = "select c from OperacaoFiscal c where c.usaNFe = true and c.ativo = true order by c.cfop.id"),
		@NamedQuery(name = "OperacaoFiscal.listaNFSe", query = "select c from OperacaoFiscal c where c.usaNFSe = true and c.ativo = true order by c.cfop.id"),
		@NamedQuery(name = "OperacaoFiscal.listaCFe", query = "select c from OperacaoFiscal c where c.usaCFe = true and c.ativo = true order by c.cfop.id") })
public class OperacaoFiscal implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Cfop cfop;
	private boolean ativo;
	private Pessoa empresa;
	private boolean usaNFe;
	private boolean usaCFe;
	private boolean usaCTe;
	private boolean usaNFSe;
	private boolean remessa;
	private String descricao;
	private String codigoCnae;
	private boolean substTrib;
	private boolean devolucao;
	private boolean consignacao;
	private ProdutoFiscal fiscal;
	private boolean transferencia;
	private String itemListaServicos;
	private String codigoTributarioMunicipio;
	private NFIndicadorPresencaComprador presencaComprador;
	private TipoNaturezaOperacaoNFSe naturezaOperacaoNFSe;
	private NFIndicadorFormaPagamento formaPagamento;
	private TipoRegimeEspecial regimeEspecial;
	private TipoTributacaoRps tributacaoRps;
	private TipoMovimentacaoFiscal tipo;
	private NFTipoEmissao tipoEmissao;
	private NFFinalidade finalidade;
	private DFAmbiente ambiente;

	public OperacaoFiscal() {
		ativo = true;
		fiscal = new ProdutoFiscal();
		usaNFe = true;
		usaNFSe = false;
		usaCFe = false;
		usaCTe = false;
		substTrib = false;
		devolucao = false;
		remessa = false;
		transferencia = false;
		consignacao = false;
	}

	@Override
	public String toString() {
		return cfop == null ? "" : cfop.getId() + " - " + descricao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 20)
	public String getCodigoCnae() {
		return codigoCnae;
	}

	public void setCodigoCnae(String codigoCnae) {
		this.codigoCnae = codigoCnae;
	}

	@Column(length = 20)
	public String getItemListaServicos() {
		return itemListaServicos;
	}

	public void setItemListaServicos(String itemListaServicos) {
		this.itemListaServicos = itemListaServicos;
	}

	public TipoTributacaoRps getTributacaoRps() {
		return tributacaoRps;
	}

	public void setTributacaoRps(TipoTributacaoRps tributaaoRps) {
		this.tributacaoRps = tributaaoRps;
	}

	public TipoNaturezaOperacaoNFSe getNaturezaOperacaoNFSe() {
		return naturezaOperacaoNFSe;
	}

	public void setNaturezaOperacaoNFSe(TipoNaturezaOperacaoNFSe naturezaOperacaoNFSe) {
		this.naturezaOperacaoNFSe = naturezaOperacaoNFSe;
	}

	public TipoRegimeEspecial getRegimeEspecial() {
		return regimeEspecial;
	}

	public void setRegimeEspecial(TipoRegimeEspecial regimeEspecial) {
		this.regimeEspecial = regimeEspecial;
	}

	@Embedded
	public ProdutoFiscal getFiscal() {
		return fiscal;
	}

	public void setFiscal(ProdutoFiscal fiscal) {
		this.fiscal = fiscal;
	}

	public boolean isConsignacao() {
		return consignacao;
	}

	public void setConsignacao(boolean consignacao) {
		this.consignacao = consignacao;
	}

	public boolean isSubstTrib() {
		return substTrib;
	}

	public void setSubstTrib(boolean substTrib) {
		this.substTrib = substTrib;
	}

	public boolean isDevolucao() {
		return devolucao;
	}

	public void setDevolucao(boolean devolucao) {
		this.devolucao = devolucao;
	}

	public boolean isRemessa() {
		return remessa;
	}

	public void setRemessa(boolean remessa) {
		this.remessa = remessa;
	}

	public boolean isTransferencia() {
		return transferencia;
	}

	public void setTransferencia(boolean transferencia) {
		this.transferencia = transferencia;
	}

	@Enumerated(EnumType.ORDINAL)
	public TipoMovimentacaoFiscal getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimentacaoFiscal tipo) {
		this.tipo = tipo;
	}

	public boolean isUsaNFe() {
		return usaNFe;
	}

	public void setUsaNFe(boolean usaNFe) {
		this.usaNFe = usaNFe;
	}

	public boolean isUsaNFSe() {
		return usaNFSe;
	}

	public void setUsaNFSe(boolean usaNFSe) {
		this.usaNFSe = usaNFSe;
	}

	public boolean isUsaCFe() {
		return usaCFe;
	}

	public void setUsaCFe(boolean usaCFe) {
		this.usaCFe = usaCFe;
	}

	public boolean isUsaCTe() {
		return usaCTe;
	}

	public void setUsaCTe(boolean usaCTe) {
		this.usaCTe = usaCTe;
	}

	@Column(nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne
	public Cfop getCfop() {
		return cfop;
	}

	public void setCfop(Cfop cfop) {
		this.cfop = cfop;
	}

	@ManyToOne
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	public DFAmbiente getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(DFAmbiente ambiente) {
		this.ambiente = ambiente;
	}

	public NFFinalidade getFinalidade() {
		return finalidade;
	}

	public void setFinalidade(NFFinalidade finalidade) {
		this.finalidade = finalidade;
	}

	public NFIndicadorFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(NFIndicadorFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public NFIndicadorPresencaComprador getPresencaComprador() {
		return presencaComprador;
	}

	public void setPresencaComprador(NFIndicadorPresencaComprador presencaComprador) {
		this.presencaComprador = presencaComprador;
	}

	public NFTipoEmissao getTipoEmissao() {
		return tipoEmissao;
	}

	public void setTipoEmissao(NFTipoEmissao tipoEmissao) {
		this.tipoEmissao = tipoEmissao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Column(length = 20)
	public String getCodigoTributarioMunicipio() {
		return codigoTributarioMunicipio;
	}

	public void setCodigoTributarioMunicipio(String codigoTributarioMunicipio) {
		this.codigoTributarioMunicipio = codigoTributarioMunicipio;
	}

	@Transient
	public boolean isExiste() {
		return this.id != null;
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
		OperacaoFiscal other = (OperacaoFiscal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}