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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@NamedQueries({
		@NamedQuery(name = "Ordem.porExpedicao", query = "select s from ExpedicaoItem e join e.servico s where e.expedicao = :expedicao"),
		@NamedQuery(name = "Ordem.lista", query = "select s from OrdemServico s where s.responsavel.origem = :origem order by s.id desc") })
public class OrdemServico implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa responsavel;
	private Date dataEmissao;
	private Date dataLiberaProducao;
	private Date dataPrevisao;
	private Date dataImpressao;
	private Date dataReimpressao;
	private String observacao;
	private Veiculo veiculo;
	private Cronologia cronologia;
	private ServicoLocacao locacao;
	private Long odometro;

	private List<ExpedicaoItem> items = new ArrayList<ExpedicaoItem>();

	public OrdemServico() {
		locacao = new ServicoLocacao();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOdometro() {
		return odometro;
	}

	public void setOdometro(Long odometro) {
		this.odometro = odometro;
	}

	@Embedded
	public ServicoLocacao getLocacao() {
		return locacao;
	}

	public void setLocacao(ServicoLocacao locacao) {
		this.locacao = locacao;
	}

	@Embedded
	public Cronologia getCronologia() {
		return cronologia;
	}

	public void setCronologia(Cronologia cronologia) {
		this.cronologia = cronologia;
	}

	@NotNull
	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataImpressao() {
		return dataImpressao;
	}

	public void setDataImpressao(Date dataImpressao) {
		this.dataImpressao = dataImpressao;
	}

	@NotNull
	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataPrevisao() {
		return dataPrevisao;
	}

	public void setDataPrevisao(Date dataPrevisao) {
		this.dataPrevisao = dataPrevisao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataLiberaProducao() {
		return dataLiberaProducao;
	}

	public void setDataLiberaProducao(Date dataLiberaProducao) {
		this.dataLiberaProducao = dataLiberaProducao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataReimpressao() {
		return dataReimpressao;
	}

	public void setDataReimpressao(Date dataReimpressao) {
		this.dataReimpressao = dataReimpressao;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@OneToMany(mappedBy = "servico", targetEntity = ExpedicaoItem.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<ExpedicaoItem> getItems() {
		return items;
	}

	public void setItems(List<ExpedicaoItem> items) {
		this.items = items;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "responsavel_id", nullable = false)
	public Pessoa getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Pessoa responsavel) {
		this.responsavel = responsavel;
	}

	@NotNull(message = "Veiculo indefinido")
	@ManyToOne
	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isImpresso() {
		return dataImpressao != null;
	}

	@Transient
	public boolean isEncerrado() {
		return cronologia != null && cronologia.getTermino() != null;
	}

	@Transient
	public boolean isLiberado() {
		return dataLiberaProducao != null;
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
		OrdemServico other = (OrdemServico) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}