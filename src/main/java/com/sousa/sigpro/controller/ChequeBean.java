package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoEntradaSaida;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.Contas;
import com.sousa.sigpro.repository.Financeiros;
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
public class ChequeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Titulos titulos;
	@Inject
	private Contas contas;
	@Inject
	private CentrosDeCusto centros;
	@Inject
	private Financeiros financeiros;

	private CondicaoFilter filtro;
	private Titulo cheque;
	private List<CentroDeCusto> listaCusto;
	private LazyDataModel<Titulo> lista;
	private List<ContaCorrente> listaContaCorrente;
	private Date dataCompensacao;
	private boolean emitidoNoCaixa;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			filtro = new CondicaoFilter();
			listaContaCorrente = contas.listaContaCheque();
			listaCusto = centros.lista(TipoEntradaSaida.SAIDA);
		}
	}

	public Date getDataCompensacao() {
		return dataCompensacao;
	}

	public void setDataCompensacao(Date dataCompensacao) {
		this.dataCompensacao = dataCompensacao;
	}

	public List<ContaCorrente> getListaContaCorrente() {
		return listaContaCorrente;
	}

	public Titulo getCheque() {
		return cheque;
	}

	public void setCheque(Titulo cheque) {
		this.cheque = cheque;
		analisaOrigemCheque();
	}

	private void analisaOrigemCheque() {
		CaixaValor origem = financeiros.chequePorCaixaValor(cheque);
		emitidoNoCaixa = origem != null;
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

	public void onPessoaChosen(SelectEvent event) {
		cheque.setResponsavel((Pessoa) event.getObject());
	}

	public void addNovoCheque() {
		emitidoNoCaixa = false;
		cheque = new Titulo();
		cheque.setTipoDocto(TipoDeTitulo.CH_CHEQUE);
		cheque.setTipoDC(TipoTituloOrigem.PAGAR);
	}

	public void salvar() {
		try {

			if (!Suporte.numeroEInteiro(cheque.getDocumento()))
				throw new NegocioException("número do documento incorreto");

			Integer numero = Integer.valueOf(cheque.getDocumento());
			cheque.setDocumento(Suporte.zerosAEsquerda(numero, 6));

			if (cheque.getId() == null) {
				Titulo che = titulos.chequePorNumero(cheque.getDocumento(), cheque.getContaBancaria());
				if (che != null)
					throw new NegocioException("cheque já foi emitido para ".concat(cheque.getNominal())
							+ " com vencimento em " + SuporteData.formataDataToStr(che.getVencimento(), "dd/MM/yyyy"));
			}

			cheque.setSaldo(cheque.getValor());
			cheque = titulos.guardar(cheque);

			FacesUtil.addInfoMessage("gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isEmitidoNoCaixa() {
		return emitidoNoCaixa;
	}

	public void compensar() {
		try {

			Titulo che = titulos.porId(cheque.getId());
			if (che.isPago())
				throw new NegocioException("documento se encontra compensado");

			cheque.setDataBaixa(dataCompensacao);
			cheque = financeiros.compensarChequeEmitido(cheque);

			FacesUtil.addInfoMessage("concluído com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir() {
		try {
			titulos.remover(cheque);
			addNovoCheque();
			FacesUtil.addInfoMessage("concluído com sucesso");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir_lista() throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "cheque_lista.jasper";

			filtro.setOrdem("t.vencimento, t.id");
			filtro.setSql("contaBancaria is not null "
					+ " and tipoDocto = ".concat(Suporte.getQuotedStr(TipoDeTitulo.CH_CHEQUE.name())));

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(titulos.lista(filtro));
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void pesquisar() {
		filtro.setSql("contaBancaria is not null "
				+ " and tipoDocto = ".concat(Suporte.getQuotedStr(TipoDeTitulo.CH_CHEQUE.name())));
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

}