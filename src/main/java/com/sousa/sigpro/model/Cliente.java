package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;

import com.sousa.sigpro.model.tipo.TipoSituacaoCredito;

@Entity
public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private Date dataCadastro = new Date();
	private double limiteCredito;
	private double limitePrazo;
	private int limiteParcela;
	private TipoSituacaoCredito situacao = TipoSituacaoCredito.RESTRITO;
	private boolean visitaSegunda = Boolean.TRUE;
	private boolean visitaTerca = Boolean.TRUE;
	private boolean visitaQuarta = Boolean.TRUE;
	private boolean visitaQuinta = Boolean.TRUE;
	private boolean visitaSexta = Boolean.TRUE;
	private boolean visitaSabado = Boolean.FALSE;
	private boolean visitaDomingo = Boolean.FALSE;
	private int visitaRepique = 7;
	private boolean retencaoIss = false;
	private String emailExtraPrimeiro;
	private String emailExtraSegundo;
	private String tagFiscalProduto;
	private CentroDeCusto centroDeCustoCliente;

	public Cliente() {
	}
	
	public Cliente(String email) {
		this.emailExtraPrimeiro = email;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional = true)
	public CentroDeCusto getCentroDeCustoCliente() {
		return centroDeCustoCliente;
	}

	public void setCentroDeCustoCliente(CentroDeCusto centroDeCustoCliente) {
		this.centroDeCustoCliente = centroDeCustoCliente;
	}

	public boolean isRetencaoIss() {
		return retencaoIss;
	}

	public void setRetencaoIss(boolean retencaoIss) {
		this.retencaoIss = retencaoIss;
	}

//	@OneToOne(cascade = CascadeType.ALL)
//	public Endereco getEnderecoDeEntrega() {
//		return enderecoDeEntrega;
//	}

//	public void setEnderecoDeEntrega(Endereco enderecoDeEntrega) {
//		this.enderecoDeEntrega = enderecoDeEntrega;
//	}

//	@OneToOne(cascade = CascadeType.ALL)
//	public Endereco getEnderecoDeFaturamento() {
//		return enderecoDeFaturamento;
//	}

//	public void setEnderecoDeFaturamento(Endereco enderecoDeFaturamento) {
//		this.enderecoDeFaturamento = enderecoDeFaturamento;
//	}

	@Column(length = 100)
	public String getTagFiscalProduto() {
		return tagFiscalProduto;
	}

	public void setTagFiscalProduto(String tagFiscalProduto) {
		this.tagFiscalProduto = tagFiscalProduto;
	}

	@Column(length = 100)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Column(precision = 12, scale = 2, columnDefinition = "double precision default 0")
	public double getLimiteCredito() {
		return limiteCredito;
	}

	public void setLimiteCredito(double limiteCredito) {
		this.limiteCredito = limiteCredito;
	}

	@Column(columnDefinition = "integer default 1")
	public int getLimiteParcela() {
		return limiteParcela;
	}

	public void setLimiteParcela(int limiteParcela) {
		this.limiteParcela = limiteParcela;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public TipoSituacaoCredito getSituacao() {
		return situacao;
	}

	@Column(precision = 12, scale = 2, columnDefinition = "double precision default 0")
	public double getLimitePrazo() {
		return limitePrazo;
	}

	public void setLimitePrazo(double limitePrazo) {
		this.limitePrazo = limitePrazo;
	}

	public void setSituacao(TipoSituacaoCredito situacao) {
		this.situacao = situacao;
	}

	public boolean isVisitaSegunda() {
		return visitaSegunda;
	}

	public void setVisitaSegunda(boolean visitaSegunda) {
		this.visitaSegunda = visitaSegunda;
	}

	public boolean isVisitaTerca() {
		return visitaTerca;
	}

	public void setVisitaTerca(boolean visitaTerca) {
		this.visitaTerca = visitaTerca;
	}

	public boolean isVisitaQuarta() {
		return visitaQuarta;
	}

	public void setVisitaQuarta(boolean visitaQuarta) {
		this.visitaQuarta = visitaQuarta;
	}

	public boolean isVisitaQuinta() {
		return visitaQuinta;
	}

	public void setVisitaQuinta(boolean visitaQuinta) {
		this.visitaQuinta = visitaQuinta;
	}

	public boolean isVisitaSexta() {
		return visitaSexta;
	}

	public void setVisitaSexta(boolean visitaSexta) {
		this.visitaSexta = visitaSexta;
	}

	public boolean isVisitaSabado() {
		return visitaSabado;
	}

	public void setVisitaSabado(boolean visitaSabado) {
		this.visitaSabado = visitaSabado;
	}

	public boolean isVisitaDomingo() {
		return visitaDomingo;
	}

	public void setVisitaDomingo(boolean visitaDomingo) {
		this.visitaDomingo = visitaDomingo;
	}

	@Column(columnDefinition = "integer default 7")
	public int getVisitaRepique() {
		return visitaRepique;
	}

	public void setVisitaRepique(int visitaRepique) {
		this.visitaRepique = visitaRepique;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Email(message = "e-mail inválido")
	@Column(length = 150)
	public String getEmailExtraPrimeiro() {
		return emailExtraPrimeiro;
	}

	public void setEmailExtraPrimeiro(String emailExtraPrimeiro) {
		this.emailExtraPrimeiro = emailExtraPrimeiro;
	}

	@Email(message = "e-mail inválido")
	@Column(length = 150)
	public String getEmailExtraSegundo() {
		return emailExtraSegundo;
	}

	public void setEmailExtraSegundo(String emailExtraSegundo) {
		this.emailExtraSegundo = emailExtraSegundo;
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
		Cliente other = (Cliente) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}