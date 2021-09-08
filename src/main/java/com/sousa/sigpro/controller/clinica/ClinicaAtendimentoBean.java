package com.sousa.sigpro.controller.clinica;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;

import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.clinica.ClinicaAgenda;
import com.sousa.sigpro.model.clinica.ClinicaAtendimento;
import com.sousa.sigpro.model.clinica.ClinicaExame;
import com.sousa.sigpro.model.clinica.ClinicaMedicamento;
import com.sousa.sigpro.model.clinica.Doenca;
import com.sousa.sigpro.model.clinica.Exame;
import com.sousa.sigpro.model.clinica.Medicamento;
import com.sousa.sigpro.model.evento.EventoClinica;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.repository.ClinicaAgendas;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Exames;
import com.sousa.sigpro.repository.Medicamentos;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class ClinicaAtendimentoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Medicamentos medicamentos;
	@Inject
	private Exames exames;
	@Inject
	private ClinicaAgendas agendas;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Eventos eventos;

	private ClinicaAgenda agenda;
	private CondicaoFilter filtro;
	private Pessoa paciente;
	private List<ClinicaAgenda> listaClinicaAgenda;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			agenda = null;
			filtro = new CondicaoFilter();
		}
	}

	public void pesquisar() {
		filtro.setSql(" a.acolhimento.dataAcolhimento is not null "
				+ "and a.dataCancela is null and a.acolhimento.dataLiberaClinica is not null ");
		listaClinicaAgenda = agendas.pesquisar(filtro);
	}

	public ClinicaAgenda getAgenda() {
		return agenda;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public Pessoa getPaciente() {
		return paciente;
	}

	public void setPaciente(Pessoa paciente) {
		this.paciente = paciente;
	}

	public void agendaSelecionada(SelectEvent event) {
		agenda = (ClinicaAgenda) event.getObject();
	}

	public List<ClinicaAgenda> getListaClinicaAgenda() {
		return listaClinicaAgenda;
	}

	public void setListaClinicaAgenda(List<ClinicaAgenda> listaClinicaAgenda) {
		this.listaClinicaAgenda = listaClinicaAgenda;
	}

	public String getTitulo() {
		return agendas.titulo(agenda);
	}

	public void removerDoenca(int linha) {
		agenda.getExames().remove(linha);
	}

	public void onPatologiaChosen(SelectEvent event) {
		Doenca cid = (Doenca) event.getObject();
		agenda.getAtendimento().setPatologia(cid);
	}

	public void onMedicamentoChosen(SelectEvent event) {
		Medicamento medicamento = (Medicamento) event.getObject();
		Medicamento procurado = medicamentos.porId(medicamento.getCodigo());
		if (procurado == null)
			agenda.getMedicamentos().add(new ClinicaMedicamento(agenda, medicamento));
		else
			agenda.getMedicamentos().add(new ClinicaMedicamento(agenda, procurado));
	}

	public void removerMedicamento(int index) {
		agenda.getMedicamentos().remove(index);
	}

	public void onExameChosen(SelectEvent event) {
		Exame exame = (Exame) event.getObject();
		Exame procurado = exames.porId(exame.getCodigo());
		if (procurado == null)
			agenda.getExames().add(new ClinicaExame(agenda, exame));
		else
			agenda.getExames().add(new ClinicaExame(agenda, procurado));
	}

	public void setAgenda(ClinicaAgenda agenda) {
		if (agenda != null) {
			agenda = agendas.porId(agenda.getId());

			if (agenda.getAtendimento() == null)
				agenda.setAtendimento(new ClinicaAtendimento());

			paciente = agenda == null ? null : pessoas.cliente(agenda.getPaciente());

		}
		this.agenda = agenda;
	}

	public void salvar() {
		try {

			if (agenda.getAtendimento().getPatologia() == null)
				throw new NegocioException("defina a patologia para este atendimento");

			agenda = agendas.guardar_atendimento(agenda);
			FacesUtil.addInfoMessage("salvo com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirReceitaSimples() throws JRException, SQLException {

		try {

			String path = seguranca.pathClass("/");
			String arquivo = path + "relatorio/receita.jasper";
			String imagem = path + "resources/sigpro/images/medicina.png";

			Pessoa profissional = pessoas.porColaborador(agenda.getAtendimento().getConsultor());

			Endereco endereco = pessoas.endereco(profissional, TipoEndereco.RESIDENCIAL);
			if (endereco == null || endereco.getMunicipio() == null)
				endereco = seguranca.enderecoEmpresa();

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			parametros.put("logo", imagem);
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("profissional", profissional);
			parametros.put("endereco", endereco);

			List<ClinicaMedicamento> lstMedicamentos = agenda.getMedicamentos();
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lstMedicamentos);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void iniciar() {
		try {

			agenda = agendas.iniciar(agenda, seguranca.getPessoaLogado().getColaborador());
			FacesUtil.addInfoMessage("iniciado com sucesso");

			Pessoa paciente = pessoas.cliente(agenda.getPaciente());
			eventos.guardar(new EventoClinica(seguranca.getPessoaLogadoOrigem().getUsuario(), agenda,
					"inicio atendimento clinico a " + paciente.getNome()));
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}