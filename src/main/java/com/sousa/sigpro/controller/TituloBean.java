package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.BoletoDigital;
import com.sousa.sigpro.model.CaixaTitulo;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.Cartao;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.evento.EventoTitulo;
import com.sousa.sigpro.model.tipo.TipoEntradaSaida;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.Caixas;
import com.sousa.sigpro.repository.Cartoes;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Financeiros;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Titulos;
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
public class TituloBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Titulos titulos;
	@Inject
	private CentrosDeCusto centros;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Caixas caixas;
	@Inject
	private Eventos eventos;
	@Inject
	private Financeiros financeiros;
	@Inject
	private Cartoes cartoes;

	private Titulo titulo;
	private Titulo tituloDeCartao;
	private Titulo[] selecionados;
	private List<Titulo> listaParcelaTitulo;
	private List<Titulo> listaTitulo;
	private LazyDataModel<Titulo> lista;
	private List<CentroDeCusto> listaCusto;
	private List<CaixaTitulo> historico;
	private List<Cartao> listaCartao;
	private CondicaoFilter filtro = new CondicaoFilter();
	private boolean emitidoNoCaixa;
	private List<BoletoDigital> listaBoletoDigital;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			listaParcelaTitulo = new ArrayList<>();
			listaTitulo = new ArrayList<>();
			listaCartao = cartoes.lista();
		}
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<Titulo> getLista() {
		return lista;
	}

	public List<CentroDeCusto> getListaCusto() {
		return listaCusto;
	}

	public void setListaCusto(List<CentroDeCusto> listaCusto) {
		this.listaCusto = listaCusto;
	}

	public List<Pessoa> getAgentes() {
		return pessoas.agentes();
	}

	public void duplicar() {
		titulo.setId(null);
		Calendar cal = Calendar.getInstance();
		cal.setTime(titulo.getVencimento());
		cal.add(Calendar.MONTH, 1);
		titulo.setDataBaixa(null);
		titulo.setVencimento(cal.getTime());
		titulo.setPrevisao(titulo.getVencimento());
		if (!titulo.getDescricao().toLowerCase().contains("duplicado")) {
			titulo.setDescricao(titulo.getDescricao() + "/duplicado");
		}
		defineCentroDeCusto();
		FacesUtil
				.addInfoMessage("Duplicado para " + SuporteData.formataDataToStr(titulo.getVencimento(), "dd/MM/YYYY"));
	}

	public List<CaixaTitulo> getHistorico() {
		return historico;
	}

	public void preparaHistorico(Titulo titulo) {
		historico = caixas.movimentoPorTitulo(titulo);
	}

	public void inativar() {
		try {

			if (titulo.getSaldo() <= 0)
				throw new NegocioException("sem saldo para inativar");

			titulo = titulos.inativar(titulo);
			eventos.guardar(new EventoTitulo(seguranca.getPessoaLogado().getUsuario(), titulo,
					"removido saldo de " + Suporte.formataCurrency(titulo.getSaldo()) + " por inatividade"));

			FacesUtil.addInfoMessage("saldo removido com sucesso.");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Titulo getTitulo() {
		return titulo;
	}

	public void novo() {
		titulo = new Titulo();
	}

	public void excluir() {
		try {
			titulo = titulos.porId(titulo.getId());

			if (titulo.getSaldo() != titulo.getValor())
				throw new NegocioException("Este título não pode ser excluído.");

			if (titulo.getExpedicao() != null)
				throw new NegocioException("Este título está ligado ao pedido: " + titulo.getExpedicao().getId()
						+ " e não pode ser excluído.");

			titulos.remover(titulo);
			titulo = null;

			FacesUtil.addInfoMessage("Removido com sucesso.");

		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir_lista() throws JRException, SQLException {
		try {

			filtro.setSortField("tipoDC, vencimento, responsavel.nome");
			filtro.setAscendente(true);
			filtro.setPrimeiroRegistro(null);
			List<Titulo> lst = titulos.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String arquivo = seguranca.pathClass("relatorio") + "titulo_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isEmitidoNoCaixa() {
		return emitidoNoCaixa;
	}

	public void gravarCompensacao() {
		try {
			if (titulo.getDataBaixa() != null)
				throw new NegocioException("título já está com data de baixa");

			Titulo origem = titulos.porId(titulo.getId());
			if (origem != null)
				titulo.setValorInicial(origem.getValor());
			titulo.setDataBaixa(titulo.getPrevisao());
			titulo.setSaldo(0);
			titulo = titulos.guardar(titulo);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<Cartao> getListaCartao() {
		return listaCartao;
	}

	public List<BoletoDigital> getListaBoletoDigital() {
		return listaBoletoDigital;
	}

	public void defineCentroDeCusto() {
		defineCentroDeCusto(titulo);
	}

	public void defineCentroDeCusto(Titulo titulo) {
		if (titulo != null) {
			switch (titulo.getTipoDC()) {
			case RECEBER:
				listaCusto = centros.lista(TipoEntradaSaida.ENTRADA);
				break;
			case PAGAR:
				listaCusto = centros.lista(TipoEntradaSaida.SAIDA);
				break;
			default:
				break;
			}
		}
	}

	private void analisaOrigemCheque(Titulo titulo) {
		CaixaValor origem = financeiros.chequePorCaixaValor(titulo);
		emitidoNoCaixa = origem != null;
	}

	public void salvar() {
		try {
			if (titulo.getPrevisao() == null)
				titulo.setPrevisao(titulo.getVencimento());

			if (titulo.getValor() <= 0)
				throw new Exception("Valor deve ser maior que zero.");

			if (seguranca.getPessoaLogado().getAgente() == null
					|| seguranca.getPessoaLogado().getAgente().getId() == null)
				throw new Exception("O usuário logado deve ser um agente financeiro!");

			if (titulo.getId() == null) {
				titulo.setVencimento(titulo.getPrevisao());
				titulo.setAgente(seguranca.getPessoaLogado().getAgente());
				titulo.setSaldo(titulo.getValor());
			} else {
				Titulo teste = titulos.porId(titulo.getId());
				if (titulo.getSaldo() == teste.getValor()) {
					titulo.setSaldo(titulo.getValor());
				}
			}

			titulo = titulos.guardar(titulo);
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onPessoaChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		titulo.setResponsavel(pessoa);

		if (titulo.getNominal() == null)
			titulo.setNominal(pessoa.getNome());
		if (titulo.getCentroDeCusto() == null) {
			if (titulo.getTipoDC() == TipoTituloOrigem.PAGAR && pessoa.getFornecedor() != null) {
				titulo.setCentroDeCusto(pessoa.getFornecedor().getCentroDeCusto());
			} else if (titulo.getTipoDC() == TipoTituloOrigem.RECEBER && pessoa.getCliente() != null) {
				titulo.setCentroDeCusto(pessoa.getCliente().getCentroDeCustoCliente());
			}
		}

	}

	public void ajustaPrevisao() {
		if (titulo != null && titulo.getVencimento() != null) {
			titulo.setPrevisao(titulo.getVencimento());
		}
	}

	public void pesquisar() {
		lista = new LazyDataModel<Titulo>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Titulo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(titulos.quantidadeFiltrados(filtro));
				return titulos.lista(filtro);
			}
		};
	}

	public List<Titulo> getListaTitulo() {
		return listaTitulo;
	}

	public void imprimir_fatura() throws JRException, SQLException {
		try {

			List<Titulo> lst = titulos.lista(titulo);

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("origem", titulo);

			String arquivo = seguranca.pathClass("relatorio") + "titulo_composicao.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Titulo getTituloDeCartao() {
		return tituloDeCartao;
	}

	public void setTituloDeCartao(Titulo tituloDeCartao) {
		this.tituloDeCartao = tituloDeCartao;
	}

	public Titulo[] getSelecionados() {
		return selecionados;
	}

	public void setSelecionados(Titulo[] selecionados) {
		this.selecionados = selecionados;
	}

	public List<Titulo> getListaParcelaTitulo() {
		return listaParcelaTitulo;
	}

	public void transferirParcelaTitulo() {
		if (selecionados != null && selecionados.length > 0) {
			listaParcelaTitulo = titulos.lista(selecionados[0].getOrigem().getCartao());
			if (tituloDeCartao != null)
				listaParcelaTitulo.remove(titulo);
		}
	}

	public void pesquisarTituloAssociado() {
		this.pesquisarTituloAssociado(titulo);
	}

	public void pesquisarTituloAssociado(Titulo titulo) {
		tituloDeCartao = null;
		selecionados = null;
		listaParcelaTitulo = null;
		listaTitulo = titulos.lista(titulo);
	}

	public void transferirTituloCartao() {
		try {
			titulos.transferirTituloCartao(selecionados, tituloDeCartao);
		} finally {
			listaTitulo = titulos.lista(selecionados[0].getOrigem());
			tituloDeCartao = null;
			selecionados = null;
			listaParcelaTitulo = null;
			titulo = titulos.porId(titulo.getId());
			this.pesquisar();
		}

	}

	public void corrigirValorFatura() {
		titulo = titulos.atualizaPorCartao(titulo);
	}

	public void setTitulo(Titulo titulo) {
		if (titulo != null) {
			listaBoletoDigital = titulos.listaBoletoDigital(titulo);
			analisaOrigemCheque(titulo);
		}
		this.titulo = titulo;
		defineCentroDeCusto();
	}

}