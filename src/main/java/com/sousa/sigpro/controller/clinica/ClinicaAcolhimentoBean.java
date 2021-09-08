package com.sousa.sigpro.controller.clinica;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Profissao;
import com.sousa.sigpro.model.clinica.ClinicaAgenda;
import com.sousa.sigpro.model.clinica.Convenio;
import com.sousa.sigpro.model.evento.EventoClinica;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.repository.ClinicaAgendas;
import com.sousa.sigpro.repository.ClinicaHorarios;
import com.sousa.sigpro.repository.Convenios;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.FocusEndereco;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.service.TabelaMunicipioService;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class ClinicaAcolhimentoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pessoas pessoas;
	@Inject
	private TabelaMunicipioService tabelaMunicipios;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Convenios convenios;
	@Inject
	private ClinicaAgendas agendas;
	@Inject
	private Eventos eventos;
	@Inject
	private ClinicaHorarios horarios;

	private String editorControl = "bold italic underline | font size style color highlight | bullets numbering | alignleft center alignright justify | undo redo | copy cut paste pastetext | outdent indent | removeformat";
	private ClinicaAgenda agenda;
	private CondicaoFilter filtro;
	private Pessoa paciente;
	private Endereco endereco;
	private DFUnidadeFederativa xUf;
	private List<Municipio> listaMunicipio;
	private List<Convenio> listaConvenio;
	private List<Profissao> listaProfissao;
	private List<ClinicaAgenda> listaClinicaAgenda;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			novo();
			filtro = new CondicaoFilter();
			listaConvenio = convenios.lista();
			listaProfissao = horarios.listaProfissao();
		}
	}

	public ClinicaAgenda getAgenda() {
		return agenda;
	}

	public void agendaSelecionada(SelectEvent event) {
		agenda = (ClinicaAgenda) event.getObject();
	}

	public Pessoa getPaciente() {
		return paciente;
	}

	public void setPaciente(Pessoa paciente) {
		this.paciente = paciente;
	}

	public DFUnidadeFederativa getxUf() {
		return xUf;
	}

	public void setxUf(DFUnidadeFederativa xUf) {
		if (xUf != null)
			listaMunicipio = tabelaMunicipios.lista(xUf.getCodigo(), 0);
		this.xUf = xUf;
	}

	public List<Municipio> getListaMunicipio() {
		return listaMunicipio;
	}

	public List<Municipio> getListaMunicipios(DFUnidadeFederativa xUf) {
		return tabelaMunicipios.lista(xUf.getCodigo(), 0);
	}

	public List<Convenio> getListaConvenio() {
		return listaConvenio;
	}

	public void setListaConvenio(List<Convenio> listaConvenio) {
		this.listaConvenio = listaConvenio;
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

	public void novo() {
		Suporte.removerAtributoDaSessao("imagem");
		listaMunicipio = new ArrayList<>();
		agenda = new ClinicaAgenda();
		agenda.getAcolhimento().setDataAcolhimento(new Date());
		paciente = new Pessoa();
		paciente.setUsuario(null);
		paciente.setFornecedor(null);
		paciente.setColaborador(null);
		paciente.setDefineTransportador(null);
		paciente.setVendedor(null);
		endereco = new Endereco();
	}

	public List<Profissao> getListaProfissao() {
		return listaProfissao;
	}

	public List<ClinicaAgenda> getListaClinicaAgenda() {
		return listaClinicaAgenda;
	}

	public void setListaClinicaAgenda(List<ClinicaAgenda> listaClinicaAgenda) {
		this.listaClinicaAgenda = listaClinicaAgenda;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void carregarPesquisaEndereco(SelectEvent event) {
		EnderecoFocus ende = (EnderecoFocus) event.getObject();
		seguranca.defineEnderecoFocus(ende, endereco);
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public void carregaMunicipios() {
		if (xUf != null)
			if (listaMunicipio == null || listaMunicipio.size() == 0 || !listaMunicipio.get(0).getUf().equals(xUf))
				listaMunicipio = tabelaMunicipios.lista(xUf.getCodigo(), 0);
	}

	public String getTitulo() {
		return agendas.titulo(agenda);
	}

	public void retornar() {
		try {

			NegocioException.analisa(agenda == null || !agenda.isExiste(),
					"acolhimento não está pronto para atendimento!");

			NegocioException.analisa(agenda.getDataCancela() != null, "registro está cancelado");

			NegocioException.analisa(
					agenda.getAtendimento() != null && agenda.getAtendimento().getDataInicioAtendimento() != null,
					"registro já está em atendimento");

			agenda = agendas.retornar(agenda);
			this.setAgenda(agenda);

			FacesUtil.addInfoMessage("retorno para acolhimento com sucesso");

			Pessoa paciente = pessoas.cliente(agenda.getPaciente());
			eventos.guardar(new EventoClinica(seguranca.getPessoaLogado().getUsuario(), agenda,
					paciente.getNome() + ": retorno para acolhimento"));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public String getEditorControl() {
		return editorControl;
	}

	public void cancelar() {
		try {

			agenda = agendas.cancelar(agenda);
			FacesUtil.addInfoMessage("cancelado com sucesso");

			Pessoa paciente = pessoas.cliente(agenda.getPaciente());
			eventos.guardar(new EventoClinica(seguranca.getPessoaLogado().getUsuario(), agenda,
					paciente.getNome() + ": acolhimento cancelado"));

		} catch (Exception e) {
			agenda.setDataCancela(null);
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imagemSelecionada(SelectEvent event) {
		byte[] foto = (byte[]) event.getObject();
		Suporte.setAtributoNaSessao("imagem", foto);
	}

	public StreamedContent getImagem() throws IOException {
		StreamedContent img = new DefaultStreamedContent();
		FacesContext context = FacesContext.getCurrentInstance();
		if (context.getCurrentPhaseId() != PhaseId.RENDER_RESPONSE) {
			byte[] foto = (byte[]) Suporte.getAtributoDaSessao("imagem");
			if (foto != null)
				img = new DefaultStreamedContent(new ByteArrayInputStream(foto));
		}
		return img;
	}

	public void pesquisar() {
		filtro.setSql(" a.acolhimento.dataAcolhimento is not null ");
		listaClinicaAgenda = agendas.pesquisar(filtro);
	}

	public void setAgenda(ClinicaAgenda agenda) {

		if (agenda == null)
			paciente = null;
		else {
			agenda = agendas.porId(agenda.getId());
			paciente = pessoas.cliente(agenda.getPaciente());
		}

		this.agenda = agenda;
		preparaCadastroPaciente();

	}

	public void liberar() {
		try {

			NegocioException.analisa(agenda == null || !agenda.isExiste(),
					"acolhimento não está pronto para atendimento!");

			agenda = agendas.liberar(agenda);
			this.setAgenda(agenda);

			FacesUtil.addInfoMessage("liberado para clínica com sucesso");

			Pessoa paciente = pessoas.cliente(agenda.getPaciente());
			eventos.guardar(new EventoClinica(seguranca.getPessoaLogado().getUsuario(), agenda,
					paciente.getNome() + ": liberado para clínica"));

		} catch (Exception e) {
			agenda.getAcolhimento().setDataLiberaClinica(null);
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onClienteChosen(SelectEvent event) {
		paciente = (Pessoa) event.getObject();
		preparaCadastroPaciente();
	}

	public void pesquisaPorCpf() {
		paciente = pessoas.porCpf(paciente.getPF().getCpf());
		preparaCadastroPaciente();
	}

	public void preparaCadastroPaciente() {

		try {

			Suporte.removerAtributoDaSessao("imagem");

			if (paciente == null)
				throw new NegocioException("Não localizado");

			paciente = pessoas.porId(paciente.getId());

			int idade = 0;
			if (paciente.getPF() != null && paciente.getPF() != null) {
				agenda.getAcolhimento().setAltura(paciente.getPF().getAltura());
				agenda.getAcolhimento().setPeso(paciente.getPF().getPeso());
				if (paciente.getPF().getNascimento() != null)
					idade = SuporteData.idade(paciente.getPF().getNascimento(), agenda.getDataCadastro());
				agenda.setIdade(idade);
			}

			if (paciente.getImagem() != null)
				Suporte.setAtributoNaSessao("imagem", paciente.getImagem());

			endereco = paciente.getEndereco(TipoEndereco.RESIDENCIAL);

			if (!endereco.isExiste())
				paciente.getEnderecos().add(endereco);

			if (endereco != null && endereco.getMunicipio() != null)
				setxUf(endereco.getMunicipio().getUf());

		} catch (Exception e) {

			paciente = new Pessoa();
			endereco = new Endereco(seguranca.enderecoEmpresa().getMunicipio());

			FacesUtil.addErrorMessage("Erro:" + e.getMessage());
		}

	}

	public void salvar() {
		try {

			if (paciente.getPF() == null || paciente.getPF().getCpf() == null)
				throw new NegocioException("CPF indefinido para o paciente!");

			String cpf = Suporte.onlyNumbers(paciente.getPF().getCpf());
			if (cpf.length() < 11 || !Suporte.isCPF(cpf))
				throw new NegocioException("CPF inválido!");

			if (!paciente.getEnderecos().contains(endereco))
				paciente.getEnderecos().add(endereco);

			boolean novo = !agenda.isExiste();
			agenda.setPaciente(paciente.getCliente());

			if (Suporte.existeAtributoNaSessao("imagem")) {
				byte[] foto = (byte[]) Suporte.getAtributoDaSessao("imagem");
				if (foto != null) {
					paciente.setImagem(foto);
				}
			}

			paciente = pessoas.guardar(paciente);
			agenda.setPaciente(paciente.getCliente());

			if (agenda.getAtendimento().getConsultor() != null
					&& agenda.getAtendimento().getConsultor().getId() == null)
				agenda.getAtendimento().setConsultor(null);

			if (agenda.getAtendimento().getPatologia() != null
					&& agenda.getAtendimento().getPatologia().getDataCadastro() == null)
				agenda.getAtendimento().setPatologia(null);

			agenda = agendas.guardar_acolhimento(agenda);

			this.setAgenda(agenda);

			FacesUtil.addInfoMessage(
					"Acolhimento salvo com sucesso: [" + Suporte.zerosAEsquerda(agenda.getId(), 6) + "] ");

			if (novo) {
				Pessoa paciente = pessoas.cliente(agenda.getPaciente());
				eventos.guardar(new EventoClinica(seguranca.getPessoaLogado().getUsuario(), agenda,
						paciente.getNome() + ": acolhimento de paciente"));
			}

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}
