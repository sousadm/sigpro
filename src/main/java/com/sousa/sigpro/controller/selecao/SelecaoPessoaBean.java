package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.repository.ConsultaSQLs;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;

@Named
@ViewScoped
public class SelecaoPessoaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private Pessoas pessoas;
	@Inject
	private ConsultaSQLs consultas;

	private CondicaoFilter filtro = new CondicaoFilter();
	private LazyDataModel<Pessoa> model;
	TipoPessoa tipoPessoa;
	String nome_consulta = "pessoa";

	public SelecaoPessoaBean() {

		tipoPessoa = (TipoPessoa) Suporte.getAtributoDaSessao("tipoPessoa");
		if (tipoPessoa != null) {
			nome_consulta = nome_consulta.concat(tipoPessoa.getDescricao());
			TipoPessoa[] tipoPessoas = new TipoPessoa[2];
			tipoPessoas[0] = tipoPessoa;
			tipoPessoas[1] = TipoPessoa.NA;
			filtro.setTipoPessoa(tipoPessoas);
		}

		try {
			filtro = (CondicaoFilter) consultas.consulta(seguranca.getPessoaLogado().getUsuario(), nome_consulta);
		} catch (Exception e) {
			filtro = new CondicaoFilter();
		}

	}

	@PreDestroy
	public void destroy() {
		Suporte.removerAtributoDaSessao("tipoPessoa");
	}

	public void pesquisar() {
		model = new LazyDataModel<Pessoa>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Pessoa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(pessoas.quantidadeFiltrados(filtro));
				return pessoas.lista(filtro);
			}
		};
	}

	public LazyDataModel<Pessoa> getModel() {
		return model;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void selecionar(Pessoa pessoa) {
		Pessoa choosen = pessoas.porId(pessoa.getId());
		PrimeFaces.current().dialog().closeDynamic(choosen);
	}

}