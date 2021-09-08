package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.sousa.sigpro.model.Montagem;
import com.sousa.sigpro.model.MontagemItem;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.ProducaoMontagem;
import com.sousa.sigpro.model.ProducaoMontagemLista;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.evento.EventoMontagem;
import com.sousa.sigpro.model.tipo.TipoComando;
import com.sousa.sigpro.repository.Comandos;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Montagens;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Producoes;
import com.sousa.sigpro.repository.filter.ProdutoFilter;
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
public class MontagemBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Montagens montagens;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Eventos eventos;
	@Inject
	private Comandos comandos;
	@Inject
	private Producoes producoes;

	private Montagem montagem;
	private MontagemItem montagemItem;
	private ProducaoMontagem producao;
	private ProdutoFilter filtro;
	private List<Colaborador> listaOperadores;
	private List<Comando> listaComandos;
	private List<ProducaoMontagem> listaProducao;
	private LazyDataModel<Montagem> lista;
	private LazyDataModel<MontagemItem> listaItems;

	public MontagemBean() {
		// TODO Auto-generated constructor stub
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			producao = new ProducaoMontagem();
			listaOperadores = pessoas.listaOperadores();
			montagemItem = new MontagemItem();
			listaComandos = comandos.lista(TipoComando.ENCERRAR);
			listaProducao = new ArrayList<>();
			filtro = new ProdutoFilter();
			filtro.setInicio(SuporteData.primeiraDataDoMes(new Date()));
			filtro.setTermino(SuporteData.ultimaDataDoMes(new Date()));
		}
	}

	public List<Comando> getListaComandos() {
		return listaComandos;
	}

	public List<Colaborador> getListaOperadores() {
		return listaOperadores;
	}

	public Montagem getMontagem() {
		return montagem;
	}

	public ProducaoMontagem getProducao() {
		return producao;
	}

	public void setProducao(ProducaoMontagem producao) {
		this.producao = producao;
	}

	public MontagemItem getMontagemItem() {
		return montagemItem;
	}

	public ProdutoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(ProdutoFilter filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<Montagem> getLista() {
		return lista;
	}

	public void novoItem() {
		montagemItem = new MontagemItem();
	}

	public void onProdutoChosen(SelectEvent event) {
		Produto produto = (Produto) event.getObject();
		montagemItem.setProduto(produto);
	}

	public void removerItem(int linha) {
		if (linha >= 0)
			montagem.getItems().remove(linha);
	}

	public void pesquisar() {
		lista = new LazyDataModel<Montagem>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Montagem> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setPropriedadeOrdenacao(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(montagens.quantidadeFiltrados(filtro));
				return montagens.lista(filtro);
			}

		};
	}

	public void addMontagemItem() {
		try {

			if (montagemItem.getQuantidade() <= 0 || montagemItem.getQuantidade() < montagemItem.getProduzido())
				throw new NegocioException("quantidade incorreta para montagem");

			if (montagemItem.getMontagem() == null) {
				montagemItem.setMontagem(montagem);
				montagem.getItems().add(montagemItem);
			}

			novoItem();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setMontagem(Montagem montagem) {
		this.montagem = montagem;
	}

	public void onSolicitanteChosen(SelectEvent event) {
		Pessoa solicitante = (Pessoa) event.getObject();
		montagem.setSolicitante(solicitante);
	}

	public void novo() {
		montagem = new Montagem();
		montagem.setSolicitante(seguranca.getPessoaLogadoOrigem());
	}

	public void salvar() {
		try {

			boolean novo = !montagem.isExiste();

			if (montagem.getTermino() != null)
				throw new NegocioException("registro não pode ser modificado");

			montagem = montagens.guardar(montagem);
			novoItem();
			FacesUtil.addInfoMessage("gravado com sucesso");

			if (novo)
				eventos.guardar(new EventoMontagem(seguranca.getPessoaLogadoOrigem().getUsuario(), montagem,
						"montagem de produto solicitada por " + montagem.getSolicitante().getNome()));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void encerrar() {
		encerrar(montagem);
	}

	public void encerrar(Montagem montagem) {
		try {

			montagem = montagens.encerrar(montagem);
			FacesUtil.addInfoMessage("encerrado com sucesso");
			eventos.guardar(new EventoMontagem(seguranca.getPessoaLogadoOrigem().getUsuario(), montagem,
					"montagem de produto encerrada número " + Suporte.zerosAEsquerda(montagem.getId(), 6)));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void cancelar() {
		cancelar(montagem);
	}

	public void cancelar(Montagem montagem) {
		try {

			montagem = montagens.cancelar(montagem);
			FacesUtil.addInfoMessage("cancelado com sucesso");
			eventos.guardar(new EventoMontagem(seguranca.getPessoaLogadoOrigem().getUsuario(), montagem,
					"montagem de produto cancelada número " + Suporte.zerosAEsquerda(montagem.getId(), 6)));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public LazyDataModel<MontagemItem> getListaItems() {
		return listaItems;
	}

	public void setListaItems(LazyDataModel<MontagemItem> listaItems) {
		this.listaItems = listaItems;
	}

	public List<ProducaoMontagem> getListaProducao() {
		return listaProducao;
	}

	public void pesquisaOrdemProducao() {

		listaItems = new LazyDataModel<MontagemItem>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<MontagemItem> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setPropriedadeOrdenacao(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(montagens.quantidadeFiltrados(filtro));
				return montagens.listaProducao(filtro);
			}

			@Override
			public MontagemItem getRowData(String id) {
				return montagens.itemPorId(new Long(id));
			}

		};
	}

	public void preparaProducaoMontagem(MontagemItem montagemItem) {
		try {

			Calendar agora = Calendar.getInstance();
			agora.setTime(new Date());
			agora.set(Calendar.MINUTE, 0);
			agora.set(Calendar.SECOND, 0);

			this.montagemItem = montagemItem;
			producao = new ProducaoMontagem();
			producao.getCronologia().setInicio(agora.getTime());
			producao.setMontagemItem(montagemItem);
			producao.setQuantidade(montagemItem.getFaltaProduzir());

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void preparaInicioProducaoMontagem() {
		preparaProducaoMontagem(montagemItem);
		listaComandos = comandos.lista(TipoComando.INICIAR);
		if (listaComandos.size() > 0)
			producao.setComando(listaComandos.get(0));
	}

	public void comandar(ProducaoMontagem item, String tipo) {
		try {

			listaOperadores.clear();
			listaOperadores.add(item.getOperador());

			if (tipo.equals("ENCERRAR")) {
				listaComandos = comandos.lista(TipoComando.ENCERRAR);
				producao = producoes.montagemPorId(item.getId());
				producao.getCronologia().setTermino(new Date());
			} else {
				preparaProducaoMontagem(montagemItem);
				if (tipo.equals("PAUSAR")) {
					listaComandos = comandos.lista(TipoComando.PAUSAR);
				} else if (tipo.equals("REINICIAR")) {
					listaOperadores = pessoas.listaOperadores();
					listaComandos = comandos.lista(TipoComando.REINICIAR);
				}
			}

			if (listaComandos.size() > 0)
				producao.setComando(listaComandos.get(0));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void salvarProducao() {
		try {

			if (producao.getComando() == null)
				throw new NegocioException("sem comando definido na produção");

			if (producao.getComando().getTipo() == TipoComando.INICIAR)
				producao = producoes.iniciar(producao);
			else if (producao.getComando().getTipo() == TipoComando.PAUSAR)
				producao = producoes.pausar(producao);
			else if (producao.getComando().getTipo() == TipoComando.REINICIAR)
				producao = producoes.reiniciar(producao);
			else if (producao.getComando().getTipo() == TipoComando.ENCERRAR) {
				producao = producoes.encerrar(producao);
			}

			montagemItem = producao.getMontagemItem();
			listaProducao = montagens.lista_ultimo(montagemItem);

			FacesUtil.addInfoMessage("gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "montagem_producao.jasper";
			String subtitulo = filtro.getTituloRelatorio();

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("subtitulo", subtitulo);

			List<ProducaoMontagemLista> lst = montagens.lista_imprimir(filtro.getInicio(), filtro.getTermino());

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setMontagemItem(MontagemItem montagemItem) {
		this.montagemItem = montagemItem;
		if (montagemItem != null)
			listaProducao = montagens.lista_ultimo(montagemItem);
		else
			listaProducao = new ArrayList<>();
	}

}