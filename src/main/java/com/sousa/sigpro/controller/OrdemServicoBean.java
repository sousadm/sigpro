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
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.Comando;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Producao;
import com.sousa.sigpro.model.ProducaoExpedicao;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.evento.EventoOrdemServico;
import com.sousa.sigpro.model.tipo.TipoComando;
import com.sousa.sigpro.model.tipo.TipoStatusProducao;
import com.sousa.sigpro.repository.Comandos;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.OrdemProducoes;
import com.sousa.sigpro.repository.OrdemServicos;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Producoes;
import com.sousa.sigpro.repository.filter.OrdemServicoFilter;
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
public class OrdemServicoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private HttpServletResponse response;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Expedicoes expedicoes;
	@Inject
	private OrdemServicos ordemServicos;
	@Inject
	private OrdemProducoes ordemProducoes;
	@Inject
	private Comandos comandos;
	@Inject
	private Producoes producoes;
	@Inject
	private Eventos eventos;

	private Expedicao expedicao;
	private OrdemServico ordem;
	private LazyDataModel<OrdemServico> listaOrdemServico;
	private LazyDataModel<ExpedicaoItem> listaOrdemProducao;
	private OrdemServicoFilter filtro;
	private List<Colaborador> listaOperadores;
	private List<Comando> listaComandos;
	private List<OrdemServico> listaPorExpedicao;
	private ProducaoExpedicao producao;

	public OrdemServicoBean() {
		if (FacesUtil.isNotPostback()) {
			producao = new ProducaoExpedicao();
			filtro = new OrdemServicoFilter();
			filtro.setTipo(1);
		}
	}

	public Producao getProducao() {
		return producao;
	}

	public void setProducao(ProducaoExpedicao producao) {
		this.producao = producao;
	}

	public List<Comando> getListaComandos() {
		return listaComandos;
	}

	public void setListaComandos(List<Comando> listaComandos) {
		this.listaComandos = listaComandos;
	}

	public Expedicao getExpedicao() {
		return expedicao;
	}

	public OrdemServicoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(OrdemServicoFilter filtro) {
		this.filtro = filtro;
	}

	public void setExpedicao(Expedicao expedicao) {
		this.expedicao = expedicao;
	}

	public OrdemServico getOrdem() {
		return ordem;
	}

	public LazyDataModel<OrdemServico> getListaOrdemServico() {
		return listaOrdemServico;
	}

	public LazyDataModel<ExpedicaoItem> getListaOrdemProducao() {
		return listaOrdemProducao;
	}

	public void inicializarOrdemServico() {
		if (expedicao != null) {
			expedicao = expedicoes.porId(expedicao.getId());

			Pessoa responsavel = pessoas.cliente(expedicao.getCliente());

			ordem = new OrdemServico();

			ordem.setResponsavel(responsavel);

			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(new Date()); // sets calendar time/date
			cal.add(Calendar.HOUR_OF_DAY, 2); // adds one hour
			ordem.setDataPrevisao(cal.getTime()); // returns
			ordem.setItems(expedicoes.listaServicoLivre(expedicao));

			expedicao = null;
		}
	}

	public void limparResponsavel() {
		filtro.setResponsavel(new Pessoa());
	}

	public void onClienteChosen(SelectEvent event) {
		filtro.setResponsavel((Pessoa) event.getObject());
	}

	/* ORDEM DE PRODUCAO DE SERVIÇO */

	public List<Colaborador> getListaOperadores() {
		return listaOperadores;
	}

	public void setListaOperadores(List<Colaborador> listaOperadores) {
		this.listaOperadores = listaOperadores;
	}

	public void stop(int linha) {
		try {
			ExpedicaoItem item = listaOrdemProducao.getRowData();
			ordemProducoes.stop(item);
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onVeiculoChosen(SelectEvent event) {
		Veiculo veiculo = (Veiculo) event.getObject();
		ordem.setVeiculo(veiculo);
		ordem.setOdometro(veiculo.getOdometroFinal());
	}

	public void excluirItemOrdem(int linha) {
		try {
			if (linha >= 0) {

				if (ordem.getItems().size() <= 1)
					throw new NegocioException("precisa ter pelo meno um item");

				ExpedicaoItem item = expedicoes.itemPorId(ordem.getItems().get(linha).getId());
				if (item.getStatus() == TipoStatusProducao.CONCLUIDO
						|| item.getStatus() == TipoStatusProducao.ENCERRADO)
					throw new NegocioException("Item não pode ser excluído!");
				ordem.getItems().remove(linha);
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void salvarOrdemServico() {
		try {

			if (ordem.getDataPrevisao() == null)
				throw new NegocioException("Defina a previsão para conclusão!");
			if (ordem.getVeiculo() == null)
				throw new NegocioException("Veículo indefinido!");
			if (ordem.getItems().size() == 0)
				throw new NegocioException("Necessário pelo menos um item!");
			if (ordem.getOdometro() == null)
				throw new NegocioException("Defina o odômetro atual do veículo!");
			if (ordem.getItems() == null || ordem.getItems().size() == 0)
				throw new NegocioException("Necessário ter pelo menos um item de serviço.");

			ordem = ordemServicos.guardar(ordem);
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");

		} catch (

		Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<OrdemServico> getListaPorExpedicao() {
		return listaPorExpedicao;
	}

	public void preparaListaOrdemServico(Expedicao expedicao) {
		listaPorExpedicao = ordemServicos.lista(expedicao);
	}

	public void setOrdem(OrdemServico ordem) {
		this.ordem = ordem;
		if (ordem != null) {
			ordem.setItems(ordemServicos.listaItems(ordem));
		}
	}

	public void liberarParaProducao() {
		try {
			ordem = ordemServicos.liberar(ordem);
			eventos.guardar(new EventoOrdemServico(seguranca.getPessoaLogadoOrigem().getUsuario(), ordem,
					"ordem de serviço liberada para produção número " + Suporte.zerosAEsquerda(ordem.getId(), 6)));
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirOrdemServico() throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "ordemServico.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("ordem", ordem);

			List<ExpedicaoItem> items = expedicoes.itemsPorServico(ordem);
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(items);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			ordem = ordemServicos.definirImpressao(ordem);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void pesquisarOrdemServico() {
		listaOrdemServico = new LazyDataModel<OrdemServico>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<OrdemServico> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setPropriedadeOrdenacao(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(ordemServicos.quantidadeFiltrados(filtro));
				return ordemServicos.lista(filtro);
			}
		};
	}

	public void pesquisarOrdemProducao() {

		listaOrdemProducao = new LazyDataModel<ExpedicaoItem>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<ExpedicaoItem> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setPropriedadeOrdenacao(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(ordemProducoes.quantidadeFiltrados(filtro));
				return ordemProducoes.listaProducao(filtro);
			}
		};
	}


	public void imprimir_lista() throws JRException, SQLException {
		try {

			if (filtro.getInicio() == null || filtro.getTermino() == null)
				throw new NegocioException("período indefinido");

			Suporte.validaPeriodo(filtro.getInicio(), filtro.getTermino());

			filtro.setPropriedadeOrdenacao("s.servico, s.id");
			filtro.setAscendente(true);
			filtro.setPrimeiroRegistro(null);
			List<ExpedicaoItem> lst = ordemProducoes.listaProducao(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("subtitulo", "preparar subtitulo do relatorio");

			String arquivo = seguranca.pathClass("relatorio") + "ordemservico_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void comandar(Long numero, String tipo) {
		try {
			
			ExpedicaoItem item = expedicoes.itemPorId(numero);
			Producao ultimo = producoes.ultimoPorItem(item);

			listaOperadores = pessoas.listaOperadorPorProduto(item.getProduto());
			if (listaOperadores.isEmpty())
				throw new NegocioException("Sem função definida para este serviço");

			if (tipo.equals("INICIAR"))
				listaComandos = comandos.lista(TipoComando.INICIAR);
			else if (tipo.equals("PAUSAR")) {
				listaOperadores.clear();
				listaOperadores.add(ultimo.getOperador());
				listaComandos = comandos.lista(TipoComando.PAUSAR);
			} else if (tipo.equals("REINICIAR"))
				listaComandos = comandos.lista(TipoComando.REINICIAR);
			else if (tipo.equals("ENCERRAR")) {
				listaOperadores.clear();
				listaOperadores.add(ultimo.getOperador());
				listaComandos = comandos.lista(TipoComando.ENCERRAR);
			}
			producao = new ProducaoExpedicao();
			producao.setOrdemItemProducao(item);
			producao.getCronologia().setInicio(new Date());

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void salvarProducao() {

		try {

			ordem = producao.getOrdemItemProducao().getServico();

			if (producao.getComando().getTipo() == TipoComando.INICIAR) {
				if (producao.getOrdemItemProducao().getCronologia() != null
						&& producao.getOrdemItemProducao().getCronologia().getInicio() != null)
					throw new NegocioException("Item já foi iniciado!");
				if (ordem.getCronologia() != null
						&& ordem.getCronologia().getInicio().after(producao.getCronologia().getInicio()))
					throw new NegocioException("Início deve ser superior a :"
							+ SuporteData.formataDataToStr(ordem.getCronologia().getInicio(), null));
				producao = producoes.iniciar(producao);
			} else if (producao.getComando().getTipo() == TipoComando.PAUSAR) {
				producao = producoes.pausar(producao);
			} else if (producao.getComando().getTipo() == TipoComando.REINICIAR) {
				producao = producoes.reiniciar(producao);
			} else if (producao.getComando().getTipo() == TipoComando.ENCERRAR) {
				producao = producoes.encerra(producao);
				ordem.getCronologia().setTermino(producao.getCronologia().getTermino());
				ordem = producoes.encerrar(ordem);
			}

			pesquisarOrdemProducao();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}