package com.sousa.sigpro.controller.clinica;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.model.clinica.ClinicaAgenda;
import com.sousa.sigpro.repository.ClinicaAgendas;
import com.sousa.sigpro.repository.ClinicaHorarios;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.FocusEndereco;
import com.sousa.sigpro.service.TabelaMunicipioService;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class ClinicaAgendamentoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pessoas pessoas;
	@Inject
	private ClinicaHorarios horarios;
	@Inject
	private ClinicaAgendas agendas;
	@Inject
	private TabelaMunicipioService tabelaMunicipios;
	@Inject
	private Seguranca seguranca;

	private Date inicio;
	private Date termino;
	private DFUnidadeFederativa xUf;
	private Pessoa paciente;
	private Colaborador consultor;
	private Profissao profissao;
	private boolean skip;
	private String subTitulo;
	private ClinicaAgenda agenda;
	private Endereco endereco;
	private List<Profissao> listaProfissao;
	private List<Colaborador> listaConsultor;
	private List<ClinicaAgenda> listaClinicaAgenda;
	private List<Municipio> listaMunicipio;

	public ClinicaAgendamentoBean() {

	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			inicio = new Date();
			endereco = new Endereco();
			consultor = new Colaborador();
			profissao = new Profissao();
			paciente = new Pessoa();
			listarProfissao();
		}
	}

	public void agendar() {
		try {
			agenda.setDataMarcada(new Date());
//			agenda = agendas.guardar(agenda);
			FacesUtil.addInfoMessage("Agendado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao agendar");
		}
	}

	public void confirmar() {
		try {
			agenda.setDataConfirma(new Date());
//			agenda = agendas.guardar(agenda);
			FacesUtil.addInfoMessage("Confirmado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao confirmar");
		}
	}

	public void cancelar() {
		try {
			agenda.setDataCancela(new Date());
//			agenda = agendas.guardar(agenda);
			FacesUtil.addInfoMessage("Cancelado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao cancelar");
		}
	}

	public void listarHorarios(Colaborador profissional, Date data) {

	}

	public void listarProfissao() {
		listaProfissao = horarios.listaProfissao();
	}

	public void listarProfisional(Profissao profissao) {
		listaConsultor = horarios.listaProfissional(profissao);
	}

	public List<Profissao> getListaProfissao() {
		return listaProfissao;
	}

	public List<Colaborador> getListaConsultor() {
		return listaConsultor;
	}

	public Pessoa getPaciente() {
		return paciente;
	}

	public void setPaciente(Pessoa paciente) {
		this.paciente = paciente;
	}

	public String getSubTitulo() {
		return subTitulo;
	}

	public void onTabChange(TabChangeEvent event) {
		subTitulo = event.getTab().getTitle();
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public String onFlowProcess(FlowEvent event) {
		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}

	public Profissao getProfissao() {
		return profissao;
	}

	public List<ClinicaAgenda> getListaClinicaAgenda() {
		return listaClinicaAgenda;
	}

	public void setProfissao(Profissao profissao) {
		if (profissao != null) {
			listaConsultor = horarios.listaProfissional(profissao);
		}
		this.profissao = profissao;
	}

	public Colaborador getConsultor() {
		return consultor;
	}

	public void setConsultor(Colaborador consultor) {
		this.consultor = consultor;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getTermino() {
		return termino;
	}

	public void setTermino(Date termino) {
		this.termino = termino;
	}

	public void pesquisar() throws Exception {
		listaClinicaAgenda = horarios.listaClinicaAgenda(consultor, new Date());
	}

	public void listarHorario() throws Exception {
		listaClinicaAgenda = horarios.listaClinicaAgenda(consultor, inicio);
	}

	public ClinicaAgenda getAgenda() {
		return agenda;
	}

	public DFUnidadeFederativa getxUf() {
		return xUf;
	}

	public void setxUf(DFUnidadeFederativa xUf) {
		this.xUf = xUf;
	}

	public List<Municipio> getListaMunicipio() {
		return listaMunicipio;
	}

	public void setAgenda(ClinicaAgenda agenda) {
		if (agenda != null)
			paciente = pessoas.cliente(agenda.getPaciente());
		if (paciente == null)
			paciente = new Pessoa();
		paciente.setDefineCliente(true);
		this.agenda = agenda;
	}

	public void carregarPesquisaEndereco(SelectEvent event) {
		EnderecoFocus ende = (EnderecoFocus) event.getObject();
		seguranca.defineEnderecoFocus(ende, endereco);
	}

	public void carregaMunicipios() {
		if (xUf != null)
			if (listaMunicipio == null || listaMunicipio.size() == 0 || !listaMunicipio.get(0).getUf().equals(xUf))
				listaMunicipio = tabelaMunicipios.lista(xUf.getCodigo(), 0);
	}

	public void carregarEndereco() {
		try {
			FocusEndereco focus = new FocusEndereco(seguranca.getPessoaLogadoOrigem().getPJ());
			EnderecoFocus ende = focus.pesquisa(endereco.getCep());
			seguranca.defineEnderecoFocus(ende, endereco);
			setxUf(endereco.getMunicipio().getUf());
		} catch (Exception e) {
			FacesUtil.addInfoMessage(e.getMessage());
		}
	}

	public void pesquisaPorCpf() {
		paciente = pessoas.porCpf(paciente.getPF().getCpf());
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}
}