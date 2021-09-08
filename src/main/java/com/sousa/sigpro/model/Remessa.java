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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoRemessa;

@Entity
public class Remessa implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date dataCadastro;
	private Date dataEmissao;
	private Date dataCancelamento;
	private TipoRemessa tipo;
	private int sequencia;
	private String descricao;
	private Usuario usuario;
	private Agente destino;
	private double valorTotal;
	private int volume;

	private double prazoMedio;
	private double aliquotaAdValorem;
	private double aliquotaDesconto;
	private double taxaTac;
	private double taxaCobranca;
	private double taxaPostagem;
	private double aliquotaIss;
	private double aliquotaIof;

	private double valorAdValorem;
	private double valorDesconto;
	private double valorTac;
	private double valorCobranca;
	private double valorPostagem;
	private double valorIss;
	private double valorIof;

	private double outrasDespesas;
	private double valorFace;
	private double valorLiquido;

	private List<RemessaItem> items;

	@Transient
	public boolean isPodeEditar() {
		return dataEmissao == null;
	}

	@Transient
	public boolean isPodeCancelar() {
		return id != null && dataCancelamento == null && dataEmissao != null;
	}

	@Transient
	public boolean isPodeEmitir() {
		return id != null && dataCancelamento == null && dataEmissao == null;
	}

	public Remessa() {
		valorTotal = 0;
		volume = 0;
		tipo = TipoRemessa.INCLUSAO;
		items = new ArrayList<>();
		dataCadastro = new Date();
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
	public Agente getDestino() {
		return destino;
	}

	public void setDestino(Agente destino) {
		this.destino = destino;
	}

	@Column(columnDefinition = "text")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoRemessa getTipo() {
		return tipo;
	}

	public void setTipo(TipoRemessa tipo) {
		this.tipo = tipo;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@OneToMany(mappedBy = "remessa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<RemessaItem> getItems() {
		return items;
	}

	public void setItems(List<RemessaItem> items) {
		this.items = items;
	}

	@ManyToOne
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getSequencia() {
		return sequencia;
	}

	public void setSequencia(int sequencia) {
		this.sequencia = sequencia;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	@Column(precision = 12, scale = 2)
	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}

	@Column(precision = 12, scale = 2)
	public double getTaxaCobranca() {
		return taxaCobranca;
	}

	public void setTaxaCobranca(double taxaCobranca) {
		this.taxaCobranca = taxaCobranca;
	}

	@Column(precision = 12, scale = 2)
	public double getValorIof() {
		return valorIof;
	}

	public void setValorIof(double valorIof) {
		this.valorIof = valorIof;
	}

	@Column(precision = 12, scale = 2)
	public double getValorIss() {
		return valorIss;
	}

	public void setValorIss(double valorIss) {
		this.valorIss = valorIss;
	}

	@Column(precision = 12, scale = 2)
	public double getValorPostagem() {
		return valorPostagem;
	}

	public void setValorPostagem(double valorPostagem) {
		this.valorPostagem = valorPostagem;
	}

	@Column(precision = 12, scale = 2)
	public double getValorCobranca() {
		return valorCobranca;
	}

	public void setValorCobranca(double valorCobranca) {
		this.valorCobranca = valorCobranca;
	}

	@Column(precision = 12, scale = 2)
	public double getValorTac() {
		return valorTac;
	}

	public void setValorTac(double valorTac) {
		this.valorTac = valorTac;
	}

	@Column(precision = 12, scale = 2)
	public double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	@Column(precision = 12, scale = 2)
	public double getTaxaPostagem() {
		return taxaPostagem;
	}

	public void setTaxaPostagem(double taxaPostagem) {
		this.taxaPostagem = taxaPostagem;
	}

	@Column(precision = 12, scale = 2)
	public double getTaxaTac() {
		return taxaTac;
	}

	public void setTaxaTac(double taxaTac) {
		this.taxaTac = taxaTac;
	}

	@Column(precision = 12, scale = 4)
	public double getAliquotaDesconto() {
		return aliquotaDesconto;
	}

	public void setAliquotaDesconto(double aliquotaDesconto) {
		this.aliquotaDesconto = aliquotaDesconto;
	}

	@Column(precision = 12, scale = 2)
	public double getValorAdValorem() {
		return valorAdValorem;
	}

	public void setValorAdValorem(double valorAdValorem) {
		this.valorAdValorem = valorAdValorem;
	}

	@Column(precision = 12, scale = 2)
	public double getPrazoMedio() {
		return prazoMedio;
	}

	public void setPrazoMedio(double prazoMedio) {
		this.prazoMedio = prazoMedio;
	}

	@Column(precision = 12, scale = 4)
	public double getAliquotaAdValorem() {
		return aliquotaAdValorem;
	}

	public void setAliquotaAdValorem(double aliquotaAdValorem) {
		this.aliquotaAdValorem = aliquotaAdValorem;
	}

	@Column(precision = 12, scale = 4)
	public double getAliquotaIss() {
		return aliquotaIss;
	}

	public void setAliquotaIss(double aliquoataIss) {
		this.aliquotaIss = aliquoataIss;
	}

	@Column(precision = 12, scale = 4)
	public double getAliquotaIof() {
		return aliquotaIof;
	}

	public void setAliquotaIof(double aliquotaIof) {
		this.aliquotaIof = aliquotaIof;
	}

	@Column(precision = 12, scale = 2)
	public double getOutrasDespesas() {
		return outrasDespesas;
	}

	public void setOutrasDespesas(double outrasDespesas) {
		this.outrasDespesas = outrasDespesas;
	}

	public void setValorFace(double valorFace) {
		this.valorFace = valorFace;
	}

	public void setValorLiquido(double valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	@Column(precision = 12, scale = 2)
	public double getValorFace() {
		return valorFace;
	}

	@Column(precision = 12, scale = 2)
	public double getValorLiquido() {
		return valorLiquido;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	public void calcular() {

		int volume = items.size();
		double valorTotal = 0;
		valorFace = 0;

		for (RemessaItem item : items) {
			valorTotal = valorTotal + item.getValor();
			valorFace = valorFace + item.getLiquido();
		}

		prazoMedio = valorFace <= 0 ? 0 : valorTotal / valorFace;
		valorAdValorem = valorFace * aliquotaAdValorem / 100;
		valorIss = valorAdValorem * aliquotaIss / 100;
		valorCobranca = volume * taxaCobranca;
		valorPostagem = volume * taxaPostagem;
		valorIof = valorFace / 30 * prazoMedio * aliquotaIof / 100;
		valorDesconto = valorAdValorem + valorIss + valorCobranca + valorPostagem + valorIof;
		valorLiquido = valorLiquido - valorDesconto + outrasDespesas;

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
		Remessa other = (Remessa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}