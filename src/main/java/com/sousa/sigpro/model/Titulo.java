package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;

import com.sousa.sigpro.model.tipo.TipoRepeticao;
import com.sousa.sigpro.model.tipo.TipoTituloCaracteristica;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;

@Entity
@NamedQueries({
		@NamedQuery(name = "Titulo.TESTE", query = "select sum(t.saldo) from Titulo t where t.responsavel = :pessoa"),
		@NamedQuery(name = "Titulo.listaParcelaOrigem", query = "select t from Titulo t where t.cartao = :cartao and t.dataBaixa is null and t.saldo > 0.1 order by t.vencimento"),
		@NamedQuery(name = "Titulo.listaPorOrigem", query = "select t from Titulo t where t.origem = :origem order by t.dataBaixa, t.documento"),
		@NamedQuery(name = "Titulo.listaPorContratoAdesao", query = "select t from Titulo t where t.contratoAdesao = :contrato order by t.id"),
		@NamedQuery(name = "Titulo.temExpedicaoBaixa", query = "select t from Titulo t where ((t.dataBaixa is not null) or ((t.saldo + t.desconto - t.acrescimo) < t.valor)) and t.expedicao = :expedicao"),
		@NamedQuery(name = "Titulo.listaPorExpedicao", query = "select t from Titulo t where t.expedicao = :expedicao"),
		@NamedQuery(name = "Titulo.listaPorAquisicao", query = "select t from Titulo t where t.aquisicao = :aquisicao"),
		@NamedQuery(name = "Titulo.listaPorProjeto", query = "select t from Titulo t where t.planner.projeto = :projeto order by t.id"),
		@NamedQuery(name = "Titulo.listaPorExpedicaoAberto", query = "select t from Titulo t where t.expedicao.id = :expedicao and t.agente = :agente and t.saldo > 0"),
		@NamedQuery(name = "Titulo.listaParaBordero", query = "select t from Titulo t where t.tipoDC = :tipo and t.valor > 0 and t.saldo > 0 and t.agente = :agente order by t.documento"),
		@NamedQuery(name = "Titulo.patrimonio", query = "select t from Titulo t where t.saldo > 0 order by t.tipoDC, t.vencimento") })
public class Titulo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Titulo origem;
	private TipoTituloOrigem tipoDC;
	private TipoDeTitulo tipoDocto;
	private TipoRepeticao repete;
	private TipoTituloCaracteristica caracteristica;
	private Date emissao;
	private Date vencimento;
	private Date dataBoleto;
	private Date dataRemessa;
	private double valor;
	private double valorInicial;
	private double desconto;
	private double acrescimo;
	private double saldo;
	private double abatimento;
	private Date dataCancelamento;
	private Date dataBaixa;
	private Date previsao;
	private Date dataMaxima;

	private String nominal;
	private String descricao;
	private CentroDeCusto centroDeCusto;

	private ContaCorrente contaBancaria;
	private String documento;
	private Banco banco;
	private String conta;
	private String agencia;
	private int franquia;

	private Pessoa responsavel;
	private Cartao cartao;
	private ContratoAdesao contratoAdesao;
	private Agente agente;

	private Aquisicao aquisicao;
	private Expedicao expedicao;
	private Planner planner;

	private boolean selecionado;

	public Titulo() {
		valor = 0;
		desconto = 0;
		acrescimo = 0;
		saldo = 0;
		emissao = new Date();
		repete = TipoRepeticao.NAO;
		tipoDocto = TipoDeTitulo.OUTROS;
		responsavel = new Pessoa();
	}

	public Titulo(Titulo titulo) {
		this.setOrigem(titulo);
		this.setEmissao(new Date());
		this.tipoDC = titulo.getTipoDC();
		this.tipoDocto = titulo.getTipoDocto();
		this.repete = titulo.getRepete();
		this.caracteristica = titulo.getCaracteristica();
		this.conta = titulo.getConta();
		this.contaBancaria = titulo.getContaBancaria();
		this.agencia = titulo.getAgencia();
		this.banco = titulo.getBanco();
		this.cartao = titulo.getCartao();
		this.centroDeCusto = titulo.getCentroDeCusto();
		this.descricao = titulo.getDescricao();
		this.documento = titulo.getDocumento();
		this.nominal = titulo.getNominal();
		this.responsavel = titulo.getResponsavel();
		this.valor = titulo.getValor();
		this.acrescimo = titulo.getAcrescimo();
		this.agente = titulo.getAgente();
		this.aquisicao = titulo.getAquisicao();
		this.desconto = titulo.getDesconto();
		this.expedicao = titulo.getExpedicao();
	}

	public void addValor(double valor) {
		this.valor = this.valor + valor;
		this.saldo = this.saldo + valor;
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
	@Enumerated(EnumType.STRING)
	public TipoTituloCaracteristica getCaracteristica() {
		return caracteristica;
	}

	public void setCaracteristica(TipoTituloCaracteristica caracteristica) {
		this.caracteristica = caracteristica;
	}

	@NotNull
	@Column(length = 10, nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	public TipoTituloOrigem getTipoDC() {
		return tipoDC;
	}

	public void setTipoDC(TipoTituloOrigem tipoDC) {
		this.tipoDC = tipoDC;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataRemessa() {
		return dataRemessa;
	}

	public void setDataRemessa(Date dataRemessa) {
		this.dataRemessa = dataRemessa;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataBoleto() {
		return dataBoleto;
	}

	public void setDataBoleto(Date dataBoleto) {
		this.dataBoleto = dataBoleto;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getVencimento() {
		return vencimento;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
		this.previsao = vencimento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataBaixa() {
		return dataBaixa;
	}

	@Column(precision = 12, scale = 2)
	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	@Column(precision = 12, scale = 2)
	public double getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(double acrescimo) {
		this.acrescimo = acrescimo;
	}

	@Column(precision = 12, scale = 2)
	public double getValorInicial() {
		return valorInicial;
	}

	public void setValorInicial(double valorInicial) {
		this.valorInicial = valorInicial;
	}

	@Column(precision = 12, scale = 2)
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
		this.saldo = valor;
	}

	@Column(precision = 12, scale = 2)
	public double getAbatimento() {
		return abatimento;
	}

	public void setAbatimento(double abatimento) {
		this.abatimento = abatimento;
	}

	@Column(precision = 12, scale = 2)
	public double getSaldo() {
		return saldo;
	}

	@Column(precision = 12, scale = 2)
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getDataMaxima() {
		return dataMaxima;
	}

	public void setDataMaxima(Date dataMaxima) {
		this.dataMaxima = dataMaxima;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getPrevisao() {
		return previsao;
	}

	public void setPrevisao(Date previsao) {
		this.previsao = previsao;
	}

	@Column(length = 20)
	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	@Column(length = 20)
	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	@Column(length = 100, nullable = false)
	@NotEmpty(message = "preencha o nome da pessoa")
	public String getNominal() {
		return nominal;
	}

	public void setNominal(String nominal) {
		this.nominal = nominal;
	}

	@ManyToOne(optional = true)
	public Titulo getOrigem() {
		return origem;
	}

	public void setOrigem(Titulo origem) {
		this.origem = origem;
	}

	@ManyToOne(optional = true)
	public ContratoAdesao getContratoAdesao() {
		return contratoAdesao;
	}

	public void setContratoAdesao(ContratoAdesao contratoAdesao) {
		this.contratoAdesao = contratoAdesao;
	}

	@ManyToOne(optional = true)
	public Aquisicao getAquisicao() {
		return aquisicao;
	}

	public void setAquisicao(Aquisicao aquisicao) {
		this.aquisicao = aquisicao;
	}

	@ManyToOne(optional = true)
	public Planner getPlanner() {
		return planner;
	}

	public void setPlanner(Planner planner) {
		this.planner = planner;
	}

	@ManyToOne(optional = true)
	public Expedicao getExpedicao() {
		return expedicao;
	}

	public void setExpedicao(Expedicao expedicao) {
		this.expedicao = expedicao;
	}

	@ManyToOne(optional = true)
	public Pessoa getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Pessoa responsavel) {
		this.responsavel = responsavel;
		if (responsavel != null) {
			if (nominal == null)
				nominal = responsavel.getNome();
			if (centroDeCusto == null) {
				if (tipoDC == TipoTituloOrigem.PAGAR && responsavel.getFornecedor() != null) {
					centroDeCusto = responsavel.getFornecedor().getCentroDeCusto();
				} else if (tipoDC == TipoTituloOrigem.RECEBER && responsavel.getCliente() != null) {
					centroDeCusto = responsavel.getCliente().getCentroDeCustoCliente();
				}
			}
		}
	}

	@ManyToOne(optional = true)
	public ContaCorrente getContaBancaria() {
		return contaBancaria;
	}

	public void setContaBancaria(ContaCorrente contaBancaria) {
		this.contaBancaria = contaBancaria;
	}

	@ManyToOne(optional = true)
	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	@ManyToOne(optional = true)
	public CentroDeCusto getCentroDeCusto() {
		return centroDeCusto;
	}

	public void setCentroDeCusto(CentroDeCusto centroDeCusto) {
		this.centroDeCusto = centroDeCusto;
	}

	@ManyToOne
	public Cartao getCartao() {
		return cartao;
	}

	public void setCartao(Cartao cartao) {
		this.cartao = cartao;
	}

	@ManyToOne
	public Agente getAgente() {
		return agente;
	}

	public void setAgente(Agente agente) {
		this.agente = agente;
	}

	@Column(length = 500, nullable = false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(length = 20)
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	@Column(length = 3, nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoRepeticao getRepete() {
		return repete;
	}

	@Column(length = 40, nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	public TipoDeTitulo getTipoDocto() {
		return tipoDocto;
	}

	public void setTipoDocto(TipoDeTitulo tipoDocto) {
		this.tipoDocto = tipoDocto;
	}

	public void setRepete(TipoRepeticao repete) {
		this.repete = repete;
	}

	public int getFranquia() {
		return franquia;
	}

	public void setFranquia(int franquia) {
		this.franquia = franquia;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isExistePagamento() {
		return this.saldo < this.valor;
	}

	@Transient
	public boolean isMostraSaldo() {
		return this.saldo != this.valor && this.saldo > 0;
	}

	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Transient
	public boolean isPago() {
		return this.getDataBaixa() != null; // || this.saldo <= 0;
	}

	@Transient
	public boolean isVencido() {
		return !isPago() && this.vencimento.before(new Date());
	}

	@Transient
	public String getParticipanteCaption() {
		String msg = "Participante";
		if (this.isExiste()) {
			switch (tipoDC) {
			case RECEBER:
				msg = "Credor";
				break;
			case PAGAR:
				msg = "Destino";
				break;
			default:
				break;
			}
		}
		return msg;
	}

	@Transient
	public double getValorExtrato() {
		if (tipoDC == TipoTituloOrigem.PAGAR) {
			return -valor;
		} else {
			return valor;
		}
	}

	public void liberarSaldo() {
		saldo = valor;
		previsao = vencimento;
	}

	@Transient
	public boolean isPodeLiberar() {
		return id != null && saldo == 0 && dataCancelamento == null;
	}

	@Transient
	public boolean isLiberado() {
		return id != null && saldo > 0;
	}

	@Transient
	public int getOrdemVencimento() {
		Date hoje = DateUtils.truncate(new Date(), Calendar.DATE);
		return vencimento.compareTo(hoje);
	}

	@Transient
	public boolean isNaoPodeAlterar() {
		return isExiste() && (isExistePagamento() || expedicao != null);
	}

	@Transient
	public boolean isPodeEditar() {
		return !isExiste() || (dataBaixa == null && saldo > 0);
	}

	@Transient
	public String getStatusCor() {
		if (dataCancelamento != null)
			return "#FF0000";
		else if (dataBaixa != null)
			return "#0000ff";
		else if (saldo == valor && vencimento.before(new Date()))
			return "#ff4000	";
		else if (saldo < valor)
			return "#00ff00	";
		else
			return "#000000";
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
		Titulo other = (Titulo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}