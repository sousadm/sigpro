package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
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

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoCombustivelTipo;
import com.sousa.sigpro.model.Abastecimento;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Marca;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.VeiculoDespesa;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.Veiculos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class VeiculoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Veiculos veiculos;
	@Inject
	private CentrosDeCusto centros;

	private Veiculo veiculo;
	private CondicaoFilter filtro;
	private LazyDataModel<Veiculo> listaVeiculo;
	private LazyDataModel<Abastecimento> listaAbastecimento;
	private LazyDataModel<VeiculoDespesa> listaDespesa;
	private Abastecimento abastecimento;
	private VeiculoDespesa despesa;
	private List<CentroDeCusto> listaCusto;

	private NFNotaInfoCombustivelTipo[] listaCombustivel;
	private boolean mostraAbastecimento;
	private boolean mostraDespesa;

	public VeiculoDespesa getDespesa() {
		return despesa;
	}

	public void setDespesa(VeiculoDespesa despesa) {
		this.despesa = despesa;
	}

	public NFNotaInfoCombustivelTipo[] getListaCombustivel() {
		return listaCombustivel;
	}

	public void setListaCombustivel(NFNotaInfoCombustivelTipo[] listaCombustivel) {
		this.listaCombustivel = listaCombustivel;
	}

	public List<CentroDeCusto> getListaCusto() {
		return listaCusto;
	}

	public void setListaCusto(List<CentroDeCusto> listaCusto) {
		this.listaCusto = listaCusto;
	}

	public Abastecimento getAbastecimento() {
		return abastecimento;
	}

	public void setAbastecimento(Abastecimento abastecimento) {
		if (abastecimento != null && abastecimento.isExiste())
			abastecimento = veiculos.abastecimentoPorId(abastecimento.getId());
		this.abastecimento = abastecimento;
	}

	public void limpar() {
		filtro = new CondicaoFilter();
	}

	public void novo() {
		veiculo = new Veiculo();
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void onPessoaChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		Endereco endereco = pessoa.getEndereco(TipoEndereco.RESIDENCIAL);
		veiculo.setPessoa(pessoa);
		if (endereco == null || endereco.getMunicipio() == null)
			veiculo.setUnidadeFederativaLicenciamento(
					seguranca.getPessoaLogadoOrigem().getEndereco(TipoEndereco.COMERCIAL).getMunicipio().getUf());
		else
			veiculo.setUnidadeFederativaLicenciamento(endereco.getMunicipio().getUf());
	}

	public int getMaximoAno() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR) + 1;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void onMarcaChosen(SelectEvent event) {
		Marca marca = (Marca) event.getObject();
		veiculo.setMarca(marca);
	}

	public LazyDataModel<Veiculo> getListaVeiculo() {
		return listaVeiculo;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			despesa = new VeiculoDespesa();
			abastecimento = new Abastecimento();
			limpar();
			pesquisar();
		}
	}

	public boolean isMostraAbastecimento() {
		return mostraAbastecimento;
	}

	public boolean isMostraDespesa() {
		return mostraDespesa;
	}

	public boolean isMostraVeiculo() {
		return !mostraDespesa && !mostraAbastecimento;
	}

	public LazyDataModel<Abastecimento> getListaAbastecimento() {
		return listaAbastecimento;
	}

	public LazyDataModel<VeiculoDespesa> getListaDespesa() {
		return listaDespesa;
	}

	public void setVeiculo(Veiculo veiculo) {
		mostraDespesa = false;
		mostraAbastecimento = false;
		if (veiculo != null)
			listaCombustivel = veiculos.lista_combustiveis(veiculo.getTipoCombustivel());
		this.veiculo = veiculo;
	}

	public void excluir(Abastecimento abastecimento) {
		try {
			veiculos.remover(abastecimento);
			abastecimento = new Abastecimento();
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(VeiculoDespesa despesa) {
		try {
			veiculos.remover(despesa);
			despesa = new VeiculoDespesa();
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setMostraAbastecimento(boolean mostraAbastecimento) {
		pesquisar_abastecimento();
		listaCusto = centros.listaCustoAbastecimento();
		this.mostraAbastecimento = mostraAbastecimento;
	}

	public void setMostraDespesa(boolean mostraDespesa) {
		pesquisar_despesa();
		listaCusto = centros.listaCustoVeiculo();
		this.mostraDespesa = mostraDespesa;
	}

	public void pesquisar_despesa() {

		filtro = new CondicaoFilter();
		filtro.setVeiculo(veiculo);

		listaDespesa = new LazyDataModel<VeiculoDespesa>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<VeiculoDespesa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(veiculos.quantidadeDespesaFiltrados(filtro));
				return veiculos.lista_despesa(filtro);
			}

		};
	}

	public void onFornecedorChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		despesa.getTitulo().setResponsavel(pessoa);
		despesa.getTitulo().setNominal(pessoa.getNome());
		if (pessoa.getFornecedor() != null && pessoa.getFornecedor().getCentroDeCusto() != null) {
			despesa.getTitulo().setCentroDeCusto(pessoa.getFornecedor().getCentroDeCusto());
		}
	}

	public void onPostoChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		abastecimento.getTitulo().setResponsavel(pessoa);
		abastecimento.getTitulo().setNominal(pessoa.getNome());
		if (pessoa.getFornecedor() != null && pessoa.getFornecedor().getCentroDeCusto() != null) {
			abastecimento.getTitulo().setCentroDeCusto(pessoa.getFornecedor().getCentroDeCusto());
		}
	}

	public void excluir() {
		try {
			veiculos.remover(veiculo);
			veiculo = new Veiculo();
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void calculaMedia() {
		if (abastecimento.getVeiculo() != null && abastecimento.getVeiculo().getConsumoMedio() > 0) {
			double km = abastecimento.getOdometro() - abastecimento.getVeiculo().getOdometroAbastecimento();
			km = km / abastecimento.getVeiculo().getConsumoMedio();
			this.abastecimento.setVolume(km);
		}
	}

	public void abastecimentoAdd() {

		listaCusto = centros.listaCustoAbastecimento();
		abastecimento = new Abastecimento();
		String descricao = "abastecimento veiculo " + veiculo.getDescritivo();
		abastecimento.getTitulo().setDescricao(descricao);
		abastecimento.setVeiculo(veiculo);

		Veiculo vei = veiculos.porId(veiculo.getId());
		abastecimento.setOdometro(vei.getOdometroAbastecimento());
	}

	public void despesaAdd() {
		listaCusto = centros.listaCustoVeiculo();
		despesa = new VeiculoDespesa();
		String descricao = "despesa veiculo " + veiculo.getDescritivo();
		despesa.getTitulo().setDescricao(descricao);
		despesa.setVeiculo(veiculo);
		despesa.setOdometro(veiculo.getOdometroFinal());
	}

	public void salvar_abastecimento() {
		try {
			abastecimento.getTitulo().setSaldo(abastecimento.getTitulo().getValor());
			abastecimento.getTitulo().setPrevisao(abastecimento.getTitulo().getVencimento());
			abastecimento = veiculos.guardar(abastecimento);
			veiculo = abastecimento.getVeiculo();
			FacesUtil.addInfoMessage("gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void salvar_despesa() {
		try {
			despesa.getTitulo().setSaldo(despesa.getTitulo().getValor());
			despesa.getTitulo().setPrevisao(despesa.getTitulo().getVencimento());
			despesa = veiculos.guardar(despesa);
			veiculo = despesa.getVeiculo();
			FacesUtil.addInfoMessage("gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void salvar() {
		try {
			veiculo = veiculos.guardar(veiculo);
			listaCombustivel = veiculos.lista_combustiveis(veiculo.getTipoCombustivel());
			FacesUtil.addInfoMessage("Registro gravado com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {
		try {

			filtro.setSortField(null);
			filtro.setPrimeiroRegistro(null);
			List<Veiculo> lst = veiculos.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String arquivo = seguranca.pathClass("relatorio") + "veiculo_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void pesquisar_abastecimento() {

		filtro = new CondicaoFilter();
		filtro.setVeiculo(veiculo);

		listaAbastecimento = new LazyDataModel<Abastecimento>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Abastecimento> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(veiculos.quantidadeAbastecimentoFiltrados(filtro));
				return veiculos.lista_abastecimento(filtro);
			}

		};
	}

	public void pesquisar() {
		listaVeiculo = new LazyDataModel<Veiculo>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Veiculo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(veiculos.quantidadeFiltrados(filtro));
				return veiculos.lista(filtro);
			}

		};
	}

}