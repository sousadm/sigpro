package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;

import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Producao;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoMetodoCalculo;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Producoes;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.OrdemServicoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class RemuneradorOrdemBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Producoes producoes;
	@Inject
	private CentrosDeCusto centros;
	@Inject
	private Titulos titulos;

	private int indice = -1;
	private Titulo titulo;
	private ExpedicaoItem expedicaoItem;
	private OrdemServicoFilter filtro;
	private List<Pessoa> listaColaboradores;
	private List<ExpedicaoItem> lista;
	private List<CentroDeCusto> listaCusto;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			titulo = new Titulo();
			expedicaoItem = new ExpedicaoItem();

			filtro = new OrdemServicoFilter();
			filtro.setInicio(SuporteData.primeiraDataDoMes(new Date()));
			filtro.setTermino(SuporteData.ultimaDataDoMes(new Date()));

			listaColaboradores = pessoas.colaboradores();
			listaCusto = centros.listaComissao();
		}
	}

	public boolean isPodeEncerrar() {
		return lista != null && lista.size() > 0;
	}

	public void salvar() {
		if (indice >= 0) {
			lista.get(indice).getProduto().getCusto().setMetodo(expedicaoItem.getProduto().getCusto().getMetodo());
			indice = -1;
			calcular();
		}
	}

	public TipoMetodoCalculo[] getListaMetodoCalculo() {
		return TipoMetodoCalculo.values();
	}

	public void remover(int linha) {
		lista.remove(linha);
	}

	public OrdemServicoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(OrdemServicoFilter filtro) {
		this.filtro = filtro;
	}

	public List<Pessoa> getListaColaboradores() {
		return listaColaboradores;
	}

	public List<ExpedicaoItem> getLista() {
		return lista;
	}

	public ExpedicaoItem getExpedicaoItem() {
		return expedicaoItem;
	}

	public void setExpedicaoItem(ExpedicaoItem expedicaoItem) {
		this.expedicaoItem = expedicaoItem;
	}

	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	public List<CentroDeCusto> getListaCusto() {
		return listaCusto;
	}

	public void financeiro() {
		double valor = 0;
		for (ExpedicaoItem item : lista)
			valor = valor + item.getValorComissao();
		Pessoa colaborador = pessoas.porColaborador(filtro.getColaborador());
		titulo.setValor(valor);
		titulo.setSaldo(valor);
		titulo.setResponsavel(filtro.getColaborador().getFuncoes().get(0).getOrigem());
		titulo.setTipoDocto(TipoDeTitulo.NCC_NOTA_DE_CREDITO_COMERCIAL);
		titulo.setTipoDC(TipoTituloOrigem.PAGAR);
		titulo.setPrevisao(new Date());
		titulo.setResponsavel(colaborador);
		titulo.setNominal(colaborador.getNome());
		titulo.setDocumento("COMISSAO");
		titulo.setDescricao("comissão por produção");
	}

	public void imprimir() {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "comissao_pgto.jasper";

			List<Producao> lst = producoes.listaComissao(filtro);
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("subtitulo", "PERIODO");

			if (lst != null && lst.size() <= 0)
				throw new NegocioException("Sem dados para imprimir");

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void pesquisar() {
		lista = producoes.listaComissionar(filtro);
		calcular();
	}

	public void calcular() {
		for (ExpedicaoItem item : lista) {

			item.setTempoToStr(
					SuporteData.convertTimeWithTimeZome(producoes.tempoMillis(item, filtro.getColaborador())));

			if (item.getProduto().getCusto().getMetodo() == TipoMetodoCalculo.ITEM)
				item.setValorComissao(item.getProduto().getCusto().getFatorCalculo());
			else if (item.getProduto().getCusto().getMetodo() == TipoMetodoCalculo.PERC) {
				double valor = item.getValorTotal();
				valor = valor * item.getProduto().getCusto().getFatorCalculo() / 100;
				item.setValorComissao(valor);
			} else {
				item.setValorComissao(0);
			}
		}
	}

	public void encerrar() {
		try {
			titulo = titulos.gravarComissao(titulo, lista);
			FacesUtil.addInfoMessage("Ordem de pagamento gerada com sucesso (titulo: " + titulo.getId() + ")");
			lista.clear();
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}