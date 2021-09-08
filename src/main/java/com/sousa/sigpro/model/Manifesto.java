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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFModalidadeTransporte;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFTipoEmissao;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFTipoEmitente;
import com.fincatto.documentofiscal.mdfe3.classes.def.MDFUnidadeMedidaPesoBrutoCarga;

@Entity
public class Manifesto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Transportador transportador;
	private MDFTipoEmitente tipoEmitente;
	private Date dataCadastro;
	private Date dataEmissao;
	private double valorTotalCarga;
	private double pesoBrutoCarga;
	private MDFTipoEmissao tipoEmissao;
	private MDFModalidadeTransporte modalidadeTransporte;
	private DFUnidadeFederativa ufunidadeFederativaCarregamento;
	private DFUnidadeFederativa unidadeFederativaDescarregamento;
	private MDFUnidadeMedidaPesoBrutoCarga unidadeMedidaPesoBrutoCarga;
	private String informacaoFisco;
	private String informacaoContribuinte;
	
	private ManifestoRodoviario rodoviario;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Transportador getTransportador() {
		return transportador;
	}

	public void setTransportador(Transportador transportador) {
		this.transportador = transportador;
	}

	@Column(columnDefinition = "text")
	public String getInformacaoContribuinte() {
		return informacaoContribuinte;
	}

	public void setInformacaoContribuinte(String informacaoContribuinte) {
		this.informacaoContribuinte = informacaoContribuinte;
	}
 
	@Column(columnDefinition = "text")
	public String getInformacaoFisco() {
		return informacaoFisco;
	}

	public void setInformacaoFisco(String informacaoFisco) {
		this.informacaoFisco = informacaoFisco;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getPesoBrutoCarga() {
		return pesoBrutoCarga;
	}

	public void setPesoBrutoCarga(double pesoBrutoCarga) {
		this.pesoBrutoCarga = pesoBrutoCarga;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getValorTotalCarga() {
		return valorTotalCarga;
	}

	public void setValorTotalCarga(double valorTotalCarga) {
		this.valorTotalCarga = valorTotalCarga;
	}

	@Embedded
	public ManifestoRodoviario getRodoviario() {
		return rodoviario;
	}

	public void setRodoviario(ManifestoRodoviario rodoviario) {
		this.rodoviario = rodoviario;
	}

	@Column(length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	public DFUnidadeFederativa getUfunidadeFederativaCarregamento() {
		return ufunidadeFederativaCarregamento;
	}

	public void setUnidadeFederativaDescarregamento(DFUnidadeFederativa unidadeFederativaDescarregamento) {
		this.unidadeFederativaDescarregamento = unidadeFederativaDescarregamento;
	}

	@Column(length = 5)
	@Enumerated(EnumType.STRING)
	public MDFUnidadeMedidaPesoBrutoCarga getUnidadeMedidaPesoBrutoCarga() {
		return unidadeMedidaPesoBrutoCarga;
	}

	public void setUnidadeMedidaPesoBrutoCarga(MDFUnidadeMedidaPesoBrutoCarga unidadeMedidaPesoBrutoCarga) {
		this.unidadeMedidaPesoBrutoCarga = unidadeMedidaPesoBrutoCarga;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public DFUnidadeFederativa getUnidadeFederativaDescarregamento() {
		return unidadeFederativaDescarregamento;
	}

	public void setUfunidadeFederativaCarregamento(DFUnidadeFederativa ufunidadeFederativaCarregamento) {
		this.ufunidadeFederativaCarregamento = ufunidadeFederativaCarregamento;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public MDFModalidadeTransporte getModalidadeTransporte() {
		return modalidadeTransporte;
	}

	public void setModalidadeTransporte(MDFModalidadeTransporte modalidadeTransporte) {
		this.modalidadeTransporte = modalidadeTransporte;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public MDFTipoEmitente getTipoEmitente() {
		return tipoEmitente;
	}

	public void setTipoEmitente(MDFTipoEmitente tipoEmitente) {
		this.tipoEmitente = tipoEmitente;
	}

	public MDFTipoEmissao getTipoEmissao() {
		return tipoEmissao;
	}

	public void setTipoEmissao(MDFTipoEmissao tipoEmissao) {
		this.tipoEmissao = tipoEmissao;
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
		Manifesto other = (Manifesto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}