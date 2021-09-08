package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Agenda;
import com.sousa.sigpro.model.Contato;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoAgendaStatus;
import com.sousa.sigpro.repository.Agendas;
import com.sousa.sigpro.repository.Expedicoes;
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
public class AgendaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private EntityManager manager;
	@Inject
	private Agendas agendas;
	@Inject
	private Expedicoes expedicoes;

	private CondicaoFilter filtro;
	private LazyDataModel<Agenda> lista;
	private Agenda agenda;
	private String txtProgresso = "aguarde ...";
	private Integer progress;
	private Date minData;

	public AgendaBean() {

	}

	public void inicializar() throws Exception {
		if (FacesUtil.isNotPostback()) {
			minData = SuporteData.horaInicial(new Date());
			limpar();
			pesquisar();
		}
	}

	public void limpar() {
		filtro = new CondicaoFilter();
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public LazyDataModel<Agenda> getLista() {
		return lista;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void reprogramar() throws InterruptedException {

		txtProgresso = "Aguarde ...";

		StringBuilder condicao = new StringBuilder();
		condicao.append("select i from ExpedicaoItem i JOIN i.expedicao p ");
		condicao.append(" where p.empresa = " + seguranca.getPessoaLogadoOrigem().getId());
		condicao.append(" and i.produto.tipoReposicao = 'DIA' ");
		condicao.append(" and p.dataCancelamento is null and p.dataEmissao is not null ");
		condicao.append(" and i.dataAgendamento is null ");
		condicao.append(" ORDER BY i.id ");
		List<ExpedicaoItem> lst = manager.createQuery(condicao.toString(), ExpedicaoItem.class).getResultList();

		progress = 0;
		int posicao = 0;
		int qt = 0;
		for (ExpedicaoItem item : lst) {

			posicao = posicao + 1;
			progress = (posicao * 100) / lst.size();
			txtProgresso = "processando..." + posicao;
			Thread.sleep(200);

			if (item.getProduto().getTipoReposicao() != null) {
				qt = qt + 1;

				Expedicao venda = item.getExpedicao();

				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, item.getProduto().getFatorReposicao());
				Agenda agenda = new Agenda();
				agenda.setDataEmissao(venda.getDataEmissao());
				agenda.setDataPrevista(c.getTime());
				agenda.setObservacao(item.getProduto().getNome().toLowerCase());

				agenda = agendas.guardar(agenda);

				item.setDataAgendamento(new Date());
				item = expedicoes.guardarExpedicaoItem(item);
			}

		}
	}

	public String getTxtProgresso() {
		return txtProgresso;
	}

	public Integer getProgress() {
		return progress;
	}


	public void pesquisar() {
		lista = new LazyDataModel<Agenda>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Agenda> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(agendas.quantidadeFiltrados(filtro));
				return agendas.lista(filtro);
			}

		};
	}

	public void excluir(Agenda agenda) {
		try {
			agendas.remover(agenda);
			FacesUtil.addInfoMessage("excluído com sucesso.");
			agenda = null;
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void finalizar(String valor) {
		try {
			agenda.setStatus(TipoAgendaStatus.valueOf(valor));
			agenda = agendas.finalizar(agenda);
			FacesUtil.addInfoMessage("concluído com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {
		try {

			List<Agenda> lst = agendas.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "agenda.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Date getMinData() {
		return minData;
	}

	public void novo() {
		agenda = new Agenda();
		agenda.getContato().setDdd(seguranca.getPessoaLogadoOrigem().getDdd());
	}

	public void onContatoChosen(SelectEvent event) {
		Contato contato = (Contato) event.getObject();
		if (agenda == null)
			agenda = new Agenda();
		agenda.setContato(contato);
	}

	public void onPessoaChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		agenda.getContato().setPessoa(pessoa);
		agenda = agendas.guardar(agenda);
	}

	public void salvar() {
		try {

			if (agenda.getDataPrevista().before(agenda.getDataEmissao()))
				throw new IllegalArgumentException("Data de previsão deve ser superior à emissão!");

			if (agenda.getContato().getFone() == null && agenda.getContato().getCelular() == null)
				throw new NegocioException("defina um celular ou telefone");

			agenda.setUsuario(seguranca.getPessoaLogado().getUsuario());
			agenda = agendas.guardar(agenda);
			FacesUtil.addInfoMessage("atualizado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}