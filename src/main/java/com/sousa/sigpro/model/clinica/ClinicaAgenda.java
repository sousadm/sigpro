package com.sousa.sigpro.model.clinica;

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

import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Profissao;

@Entity
@NamedQueries({
		@NamedQuery(name = "ClinicaAgenda.pesquisa", query = "select a from ClinicaAgenda a, Pessoa p where p.cliente = a.paciente ") })
public class ClinicaAgenda implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Cliente paciente;
	private Date dataCadastro;
	private Date dataMarcada;
	private Date dataConfirma;
	private Date dataCancela;
	private String documento;
	private int idade;
	private Convenio convenio;
	private ClinicaAcolhimento acolhimento;
	private ClinicaAtendimento atendimento;
	private Profissao profissao;

	private List<ClinicaExame> exames;
	private List<ClinicaMedicamento> medicamentos;

	public ClinicaAgenda() {
		atendimento = new ClinicaAtendimento();
		acolhimento = new ClinicaAcolhimento();
		dataCadastro = new Date();
		exames = new ArrayList<>();
		medicamentos = new ArrayList<>();
		profissao = new Profissao();
		convenio = new Convenio();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<ClinicaExame> getExames() {
		return exames;
	}

	public void setExames(List<ClinicaExame> exames) {
		this.exames = exames;
	}

	@OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<ClinicaMedicamento> getMedicamentos() {
		return medicamentos;
	}

	public void setMedicamentos(List<ClinicaMedicamento> medicamentos) {
		this.medicamentos = medicamentos;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataMarcada() {
		return dataMarcada;
	}

	public void setDataMarcada(Date dataMarcada) {
		this.dataMarcada = dataMarcada;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataConfirma() {
		return dataConfirma;
	}

	public void setDataConfirma(Date dataConfirma) {
		this.dataConfirma = dataConfirma;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancela() {
		return dataCancela;
	}

	public void setDataCancela(Date dataCancela) {
		this.dataCancela = dataCancela;
	}

	@Embedded
	public ClinicaAtendimento getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(ClinicaAtendimento atendimento) {
		this.atendimento = atendimento;
	}

	@Embedded
	public ClinicaAcolhimento getAcolhimento() {
		return acolhimento;
	}

	public void setAcolhimento(ClinicaAcolhimento acolhimento) {
		this.acolhimento = acolhimento;
	}

	@ManyToOne
	public Convenio getConvenio() {
		return convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	@ManyToOne
	public Profissao getProfissao() {
		return profissao;
	}

	public void setProfissao(Profissao profissao) {
		this.profissao = profissao;
	}

	@ManyToOne
	public Cliente getPaciente() {
		return paciente;
	}

	public void setPaciente(Cliente paciente) {
		this.paciente = paciente;
	}

	@Transient
	public boolean isExiste() {
		return id != null && id > 0;
	}

	@Column(length = 30)
	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	@Transient
	public boolean isPodeSalvar() {
		return dataCancela == null && acolhimento.getDataLiberaClinica() == null;
	}

	@Transient
	public boolean isPodeSalvarAtendimento() {
		return isExiste() && dataCancela == null && atendimento != null
				&& atendimento.getDataInicioAtendimento() != null && atendimento.getDataTerminoAtendimento() == null;
	}

	@Transient
	public boolean isPodeLiberar() {
		return isExiste() && isPodeSalvar();
	}

	@Transient
	public boolean isPodeIniciar() {
		return isExiste() && dataCancela == null && acolhimento != null && acolhimento.getDataLiberaClinica() != null
				&& (atendimento == null || (atendimento.getDataInicioAtendimento() == null
						&& atendimento.getDataTerminoAtendimento() == null));
	}

	@Transient
	public boolean isPodeRetornar() {
		return isExiste() && dataCancela == null && (acolhimento == null || acolhimento.getDataLiberaClinica() != null)
				&& (atendimento == null || atendimento.getDataInicioAtendimento() == null);
	}

	@Transient
	public boolean isPodeCancelar() {
		return isExiste() && dataCancela == null
				&& (atendimento == null || atendimento.getDataInicioAtendimento() == null);
	}

	@Transient
	public boolean isPodeImprimirReceita() {
		return isExiste() && dataCancela == null && medicamentos != null && medicamentos.size() > 0;
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
		ClinicaAgenda other = (ClinicaAgenda) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
