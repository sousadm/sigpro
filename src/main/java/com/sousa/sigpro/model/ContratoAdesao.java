package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
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

import com.sousa.sigpro.model.tipo.TipoModulo;

@Entity
public class ContratoAdesao implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private ContratoAdesao origem;
	private Cliente cliente;
	private Date dataCadastro;
	private Date dataLiberado;
	private Date dataAtualizacao;
	private Date dataParcelamento;
	private Date dataCancelamento;
	private Date dataValidade;
	private String nomeSchema;
	private String tokenHomologacao;
	private String tokenProducao;
	private List<TipoModulo> modulos;

	public ContratoAdesao() {
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

	@ElementCollection
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public List<TipoModulo> getModulos() {
		return modulos;
	}

	public void setModulos(List<TipoModulo> modulos) {
		this.modulos = modulos;
	}

	@ManyToOne(optional = true)
	public ContratoAdesao getOrigem() {
		return origem;
	}

	public void setOrigem(ContratoAdesao origem) {
		this.origem = origem;
	}

	@ManyToOne
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataLiberado() {
		return dataLiberado;
	}

	public void setDataLiberado(Date dataLiberado) {
		this.dataLiberado = dataLiberado;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataParcelamento() {
		return dataParcelamento;
	}

	public void setDataParcelamento(Date dataParcelamento) {
		this.dataParcelamento = dataParcelamento;
	}

	@Column(length = 30)
	public String getNomeSchema() {
		return nomeSchema;
	}

	public void setNomeSchema(String nomeSchema) {
		this.nomeSchema = nomeSchema;
	}

	@Column(length = 32)
	public String getTokenHomologacao() {
		return tokenHomologacao;
	}

	public void setTokenHomologacao(String tokenHomologacao) {
		this.tokenHomologacao = tokenHomologacao;
	}

	@Column(length = 32)
	public String getTokenProducao() {
		return tokenProducao;
	}

	public void setTokenProducao(String tokenProducao) {
		this.tokenProducao = tokenProducao;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isCancelado() {
		return this.dataCancelamento != null;
	}

	@Transient
	public boolean isPodeLiberar() {
		return id != null && dataCancelamento == null && dataLiberado == null;
	}

	@Transient
	public boolean isPodeSincronizar() {
		return id != null && dataCancelamento == null;
	}

	@Transient
	public boolean isPodeModificar() {
		return dataCancelamento == null && dataLiberado == null;
	}

	@Transient
	public String getStatusCor() {
		if (dataCancelamento != null)
			return "#FF0000";
		else if (dataValidade != null && dataValidade.before(new Date()))
			return "#FF8000";
		else if (dataLiberado != null && dataLiberado.before(new Date()))
			return "#0000FF";
		else
			return "#000000";
	}

	@Transient
	public Date getDataContrato() {
		Date valor = dataCadastro;
		if (dataCancelamento != null)
			valor = dataCancelamento;
		else if (dataLiberado != null)
			valor = dataLiberado;
		return valor;
	}

	@Transient
	public String getTituloData() {
		String valor = "Data de Cadastro";
		if (dataCancelamento != null)
			valor = "Data de Cancelamento";
		else if (dataLiberado != null)
			valor = "Data de Liberação";
		return valor;
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
		ContratoAdesao other = (ContratoAdesao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}