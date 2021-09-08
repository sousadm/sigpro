package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import com.sousa.sigpro.model.Agente;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Remessa;
import com.sousa.sigpro.model.RemessaItem;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.repository.Contas;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Remessas;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class RemessaBean implements Serializable {

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
	private Remessas remessas;
	@Inject
	private Pessoas pessoas;

	private int activeIndex;
	private int opcao;
	private Remessa remessa;
	private RemessaItem remessaItem;
	private CondicaoFilter filtro;
	private List<ContaCorrente> listaContaCorrente;
	private List<Pessoa> agentes;
	private List<Remessa> lista;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			lista = new ArrayList<>();
			limpar();
			if (listaContaCorrente == null)
				listaContaCorrente = contas.listaParaBoleto();
			agentes = pessoas.agentes();
		}
	}

	public RemessaItem getRemessaItem() {
		return remessaItem;
	}

	public void setRemessaItem(RemessaItem remessaItem) {
		this.remessaItem = remessaItem;
	}

	public List<Remessa> getLista() {
		return lista;
	}

	public List<Pessoa> getAgentes() {
		return agentes;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public List<ContaCorrente> getListaContaCorrente() {
		return listaContaCorrente;
	}

	public void onPessoaChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		filtro.setPessoa(pessoa);
	}

	public int getOpcao() {
		return opcao;
	}

	public void setOpcao(int opcao) {
		this.opcao = opcao;
	}

	public void remover(int linha) {
		remessa.getItems().remove(linha);
	}

	public void visualizarBoleto(Titulo titulo) {
		String arquivo = "/UPLOADS/" + seguranca.getEmpresa() + "/boleto_" + titulo.getId() + ".pdf";
		Suporte.visualizarPdf(arquivo);
	}

	public Remessa getRemessa() {
		return remessa;
	}

	// public void gerarArquivoRemessa() {
	// try {
	//
	// if (remessa.getTitulos().size() == 0)
	// throw new NegocioException("sem dados para gerar remessa");
	//
	// remessa.setUsuario(seguranca.getPessoaLogado().getUsuario());
	// remessa.setSequencia(remessas.proximoLoteNaRemessa(filtro.getContaCorrente()));
	// remessa = remessas.guardar(remessa);
	// eventos.guardar(new
	// EventoRemessa(seguranca.getPessoaLogadoOrigem().getUsuario(), remessa,
	// "remessa a " + filtro.getContaCorrente().getBanco().getDescricao() + " no
	// valor "
	// + Suporte.formataCurrency(remessa.getValorTotal())));
	// FacesUtil.addInfoMessage(
	// "Remessa " + Suporte.zerosAEsquerda(remessa.getSequencia(), 6) + " concluída
	// com sucesso");
	//
	// } catch (Exception e) {
	// FacesUtil.addErrorMessage(e.getMessage());
	// }
	// }

	private void limpar() {
		filtro = new CondicaoFilter();
	}

	public void abrir() {
		remessa = remessas.remessaPorContaSequencia(filtro);
	}

	public boolean isStatusContaSelecionada() {
		return filtro.getContaCorrente() != null;
	}

	public boolean isPodeGerarRemessa() {
		return remessa.getValorTotal() > 0;
	}

	public boolean isExiste() {
		return remessa != null && remessa.isExiste();
	}

	public void pesquisar() {
		lista = remessas.lista(filtro);
	}

	public void imprimir_lista() throws JRException, SQLException {
		try {

			// filtro.setPropriedadeOrdenacao("tipoDC, vencimento, responsavel.nome");
			filtro.setAscendente(true);
			filtro.setPrimeiroRegistro(null);
			List<Titulo> lst = titulos.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String arquivo = seguranca.pathClass("relatorio") + "remessa_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public void onTabChange(TabChangeEvent event) {
		TabView tabView = (TabView) event.getComponent();
		activeIndex = tabView.getChildren().indexOf(event.getTab());
	}

	public void salvar() {
		try {
			remessa = remessas.guardar(remessa);
			FacesUtil.addInfoMessage("Registro salvo com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void emitir() {
		try {
			remessa = remessas.emitir(remessa);
			FacesUtil.addInfoMessage("emitido com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void cancelar() {
		try {
			remessa = remessas.cancelar(remessa);
			FacesUtil.addInfoMessage("cancelado com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removerTitulo(int linha) {
		remessa.getItems().remove(linha);
		remessa.calcular();
	}

	public void calcularValorItems(RemessaItem item) {
		if (remessa.getDataEmissao() == null) {
			item.setDias(SuporteData.diasEntreDatas(new Date(), item.getTitulo().getVencimento()));
		} else {
			item.setDias(SuporteData.diasEntreDatas(remessa.getDataEmissao(), item.getTitulo().getVencimento()));
		}
		double valor = item.getValor() * remessa.getAliquotaDesconto() / 100 / 10 * item.getDias();
		item.setLiquido(item.getValor() - valor);
	}

	public void calcularValorItems() {
		for (RemessaItem item : remessa.getItems()) {
			calcularValorItems(item);
		}
	}

	public void defineAgenteConfiguracao() {
		if (remessa.getDestino() != null) {
			Agente agente = pessoas.agentePorId(remessa.getDestino().getId());
			remessa.setAliquotaDesconto(agente.getDesconto());
			remessa.setAliquotaAdValorem(agente.getAdvalorem());
			remessa.setTaxaTac(agente.getTac());
			remessa.setTaxaCobranca(agente.getCobranca());
			remessa.setTaxaPostagem(agente.getPostagem());
		}
	}

	public void novo() {
		remessa = new Remessa();
	}

	public void onTituloChosen(SelectEvent event) {

		boolean incluiu = false;
		boolean existe = false;

		Titulo[] selecionados = (Titulo[]) event.getObject();
		for (Titulo titulo : selecionados) {
			for (RemessaItem item : remessa.getItems()) {
				if (!existe)
					existe = item.getTitulo().equals(titulo);
			}
			if (!existe) {
				incluiu = true;
				Titulo tt = titulos.porId(titulo.getId());
				remessaItem = new RemessaItem();
				remessaItem.setRemessa(remessa);
				remessaItem.setTitulo(tt);
				remessaItem.setValor(tt.getSaldo());
				calcularValorItems(remessaItem);
				remessa.getItems().add(remessaItem);
			}
		}

		if (incluiu)
			remessa.calcular();

	}

	public void setRemessa(Remessa remessa) {
		this.remessa = remessa;
		if (remessa != null)
			remessa.setItems(remessas.itemsPorRemessa(remessa));
	}

}