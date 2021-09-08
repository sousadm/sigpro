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
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fincatto.documentofiscal.nfe400.classes.NFModalidadeFrete;

@Entity
public class Aquisicao implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date dataCadastro;
	private Date dataCancelamento;
	private Date dataEncerramento;
	private Date dataEmail;
	private Nota nota;
	private Pessoa empresa;
	private Fornecedor fornecedor;
	private NFModalidadeFrete modalidadeFrete;
	private double valorTotal;
	private double valorFrete;

	private List<AquisicaoItem> items;
	private List<Titulo> titulos;

	public Aquisicao() {
		dataCadastro = new Date();
		nota = new Nota();
		items = new ArrayList<AquisicaoItem>();
		titulos = new ArrayList<Titulo>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "aquisicao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Titulo> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<Titulo> titulos) {
		this.titulos = titulos;
	}

	@OneToMany(mappedBy = "aquisicao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<AquisicaoItem> getItems() {
		return items;
	}

	public void setItems(List<AquisicaoItem> items) {
		this.items = items;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEncerramento() {
		return dataEncerramento;
	}

	public void setDataEncerramento(Date dataEncerramento) {
		this.dataEncerramento = dataEncerramento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEmail() {
		return dataEmail;
	}

	public void setDataEmail(Date dataEmail) {
		this.dataEmail = dataEmail;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Embedded
	public Nota getNota() {
		return nota;
	}

	public void setNota(Nota nota) {
		this.nota = nota;
	}

	@ManyToOne
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	@ManyToOne
	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(double valorFrete) {
		this.valorFrete = valorFrete;
	}

	public NFModalidadeFrete getModalidadeFrete() {
		return modalidadeFrete;
	}

	public void setModalidadeFrete(NFModalidadeFrete modalidadeFrete) {
		this.modalidadeFrete = modalidadeFrete;
	}

	public void calcular() {
		double valor = 0;
		for (AquisicaoItem item : items) {
			valor = valor + item.getValorTotal();
		}
		valorTotal = valor;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isPodeEncerrar() {
		return isExiste() && dataEncerramento == null;
	}

	@Transient
	public boolean isPodeCancelar() {
		return isExiste() && dataEncerramento != null && dataCancelamento == null;
	}

	@Transient
	public boolean isPodeImprimir() {
		return id != null && dataEncerramento == null && dataCancelamento == null;
	}

	@Transient
	public String getStatus() {
		String retorno = "Novo";
		if (dataCancelamento != null)
			retorno = "Cancelado";
		else if (dataEncerramento != null)
			retorno = "Confirmado";
		return retorno;
	}

	@Transient
	public String getStatusCor() {
		if (dataCancelamento != null)
			return "#FF0000";
		else if (dataEncerramento != null)
			return "#0000ff";
		else
			return "#000000";
	}

	@Transient
	public boolean isPodeGravar() {
		return dataEncerramento == null && dataCancelamento == null;
	}

	@Transient
	public boolean isPodeModificarItem() {
		return !isTemNotaFiscal() && isPodeGravar();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Transient
	public boolean isTemNotaFiscal() {
		return (nota != null) && (nota.getChave() != null);
	}

	@Transient
	public boolean isPodeAdicionarItem() {
		return !this.isTemNotaFiscal() && this.isPodeGravar();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Aquisicao other = (Aquisicao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}