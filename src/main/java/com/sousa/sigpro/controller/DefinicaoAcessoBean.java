package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoModulo;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.UnidadesProdutivas;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class DefinicaoAcessoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UnidadesProdutivas unidades;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Pessoas pessoas;

	private Pessoa pessoa;
	private List<TipoRotina> listaRotinas;
	private List<SelectItem> cars;

	public DefinicaoAcessoBean() {

	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			prepara();
		}
	}

	public List<SelectItem> getCars() {
		return cars;
	}

	public void setCars(List<SelectItem> cars) {
		this.cars = cars;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public List<TipoRotina> getListaRotinas() {
		return listaRotinas;
	}

	public void setListaRotinas(List<TipoRotina> listaRotinas) {
		this.listaRotinas = listaRotinas;
	}

	public void adicionaNoGrupo(List<SelectItem> listaSelect, String nome, List<SelectItem> array) {
		if (array.size() > 0) {

			if (listaSelect == null)
				listaSelect = new ArrayList<SelectItem>();

			SelectItemGroup grupo = new SelectItemGroup(nome);
			grupo.setSelectItems((SelectItem[]) array.toArray(new SelectItem[array.size()]));
			cars.add(grupo);

		}
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public void prepara() {

		cars = new ArrayList<SelectItem>();

		SelectItemGroup grupo;
		List<TipoRotina> lstBloqueios = new ArrayList<TipoRotina>();

		listaRotinas = pessoas.rotinas(pessoa.getUsuario());

		Pessoa matriz = unidades.matriz();
		List<String> lstModulo = matriz.getModulos();
		for (TipoModulo modulo : TipoModulo.values()) {
			if (modulo.equals(TipoModulo.CADASTRO) && lstModulo.contains(modulo.name())) {

				List<SelectItem> lst = new ArrayList<>();
				lst.add(new SelectItem(TipoRotina.PESSOA_CADASTRO, TipoRotina.PESSOA_CADASTRO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.PRODUTO_CADASTRO, TipoRotina.PRODUTO_CADASTRO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.CENTROCUSTO, TipoRotina.CENTROCUSTO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.VEICULO, TipoRotina.VEICULO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.AGENDA, TipoRotina.AGENDA.getDescricao()));
//				 lst.add(new SelectItem(TipoRotina.PLANOCONTA, TipoRotina.PLANOCONTA.getDescricao()));
				if (pessoa.getOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {
					lst.add(new SelectItem(TipoRotina.CLINICA_CONVENIO, TipoRotina.CLINICA_CONVENIO.getDescricao()));
					lst.add(new SelectItem(TipoRotina.CLINICA_HORARIO, TipoRotina.CLINICA_HORARIO.getDescricao()));
				} else {
					lst.add(new SelectItem(TipoRotina.AQUISICAOCADASTRO, TipoRotina.AQUISICAOCADASTRO.getDescricao()));
					lst.add(new SelectItem(TipoRotina.CATEGORIAPRODUTO, TipoRotina.CATEGORIAPRODUTO.getDescricao()));
				}
				lst.add(new SelectItem(TipoRotina.ATIVIDADE, TipoRotina.ATIVIDADE.getDescricao()));
				adicionaNoGrupo(cars, "Cadastros", lst);

				lstBloqueios.add(TipoRotina.PESSOA_LEITURA);
				lstBloqueios.add(TipoRotina.PRODUTO_LEITURA);

			} else if (modulo.equals(TipoModulo.COMERCIAL) && lstModulo.contains(modulo.name())) {
				List<SelectItem> lst = new ArrayList<>();
				if (pessoa.getOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {

				} else {
					lst.add(new SelectItem(TipoRotina.VENDA, TipoRotina.VENDA.getDescricao()));
					lst.add(new SelectItem(TipoRotina.RELATORIOVENDA, TipoRotina.RELATORIOVENDA.getDescricao()));
				}
				adicionaNoGrupo(cars, "Comercial", lst);
				lstBloqueios.add(TipoRotina.VENDA_LEITURA);

			} else if (modulo.equals(TipoModulo.SERVICO) && lstModulo.contains(modulo.name())) {
				List<SelectItem> lst = new ArrayList<>();
				if (pessoa.getOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {
					lst.add(new SelectItem(TipoRotina.CLINICA_ACOLHIMENTO,
							TipoRotina.CLINICA_ACOLHIMENTO.getDescricao()));
					lst.add(new SelectItem(TipoRotina.CLINICA_AGENDAMENTO,
							TipoRotina.CLINICA_AGENDAMENTO.getDescricao()));
					lst.add(new SelectItem(TipoRotina.CLINICA_ATENDIMENTO,
							TipoRotina.CLINICA_ATENDIMENTO.getDescricao()));
				} else if (pessoa.getOrigem().getAtividade() == TipoAtividadePrincipal.OFICINA) {
					lst.add(new SelectItem(TipoRotina.SERVICORDEM, TipoRotina.SERVICORDEM.getDescricao()));
					lst.add(new SelectItem(TipoRotina.SERVICOSPOOL, TipoRotina.SERVICOSPOOL.getDescricao()));
					lst.add(new SelectItem(TipoRotina.SERVICOCOMANDO, TipoRotina.SERVICOCOMANDO.getDescricao()));
					lst.add(new SelectItem(TipoRotina.SERVICOREMUNERADOR,
							TipoRotina.SERVICOREMUNERADOR.getDescricao()));
				}

				lst.add(new SelectItem(TipoRotina.MONTAGEMPRODUTOSPOOL,
						TipoRotina.MONTAGEMPRODUTOSPOOL.getDescricao()));

				adicionaNoGrupo(cars, "Serviços", lst);

			} else if (modulo.equals(TipoModulo.FINANCEIRO) && lstModulo.contains(modulo.name())) {

				List<SelectItem> lst = new ArrayList<>();
				lst.add(new SelectItem(TipoRotina.DIARIO, TipoRotina.DIARIO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.CAIXA, TipoRotina.CAIXA.getDescricao()));
				lst.add(new SelectItem(TipoRotina.TITULO, TipoRotina.TITULO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.FORMAPGTO, TipoRotina.FORMAPGTO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.REMESSA, TipoRotina.REMESSA.getDescricao()));
				lst.add(new SelectItem(TipoRotina.CHEQUE, TipoRotina.CHEQUE.getDescricao()));
				lst.add(new SelectItem(TipoRotina.CONTABCO, TipoRotina.CONTABCO.getDescricao()));

//				lst.add(new SelectItem(TipoRotina.FLUXO_FINANCEIRO, TipoRotina.FLUXO_FINANCEIRO.getDescricao()));

//				if (pessoa.getOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA) {

//				} else {
					lst.add(new SelectItem(TipoRotina.POSFIN, TipoRotina.POSFIN.getDescricao()));
					lst.add(new SelectItem(TipoRotina.CONCILIACAO, TipoRotina.CONCILIACAO.getDescricao()));
					lst.add(new SelectItem(TipoRotina.CONTABCO, TipoRotina.CONTABCO.getDescricao()));
					lst.add(new SelectItem(TipoRotina.CARTAO, TipoRotina.CARTAO.getDescricao()));
//				}

				adicionaNoGrupo(cars, "Financeiro", lst);

			} else if (modulo.equals(TipoModulo.FISCAL) && lstModulo.contains(modulo.name())) {

				List<SelectItem> lst = new ArrayList<>();
				lst.add(new SelectItem(TipoRotina.FISCALOPERACAO, TipoRotina.FISCALOPERACAO.getDescricao()));
				if (pessoa.getOrigem().getUnidadeProdutiva().isUsaNFe()) {
					lst.add(new SelectItem(TipoRotina.MODULO_FISCAL, TipoRotina.MODULO_FISCAL.getDescricao()));
					lst.add(new SelectItem(TipoRotina.NFE, TipoRotina.NFE.getDescricao()));
				}
				if (pessoa.getOrigem().getUnidadeProdutiva().isUsaNFSe())
					lst.add(new SelectItem(TipoRotina.NFSE, TipoRotina.NFSE.getDescricao()));
				if (pessoa.getOrigem().getUnidadeProdutiva().isUsaNFCe())
					lst.add(new SelectItem(TipoRotina.NFCE, TipoRotina.NFCE.getDescricao()));
				adicionaNoGrupo(cars, "Fiscal", lst);

			} else if (modulo.equals(TipoModulo.PLANEJAMENTO) && lstModulo.contains(modulo.name())) {

				List<SelectItem> lst = new ArrayList<>();
				lst.add(new SelectItem(TipoRotina.PATRIMONIO, TipoRotina.PATRIMONIO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.FORMACAO_PRECO, TipoRotina.FORMACAO_PRECO.getDescricao()));
				lst.add(new SelectItem(TipoRotina.QUESTIONARIO, TipoRotina.QUESTIONARIO.getDescricao()));

				lst.add(new SelectItem(TipoRotina.PROJETO, TipoRotina.PROJETO.getDescricao()));
//				lst.add(new SelectItem(TipoRotina.DEFINICAO, TipoRotina.DEFINICAO.getDescricao()));
//				lst.add(new SelectItem(TipoRotina.SWOT, TipoRotina.SWOT.getDescricao()));
//				lst.add(new SelectItem(TipoRotina.SWOTCRUZA, TipoRotina.SWOTCRUZA.getDescricao()));
//				lst.add(new SelectItem(TipoRotina.PLANODEACAO, TipoRotina.PLANODEACAO.getDescricao()));				
//				lst.add(new SelectItem(TipoRotina.META, TipoRot ina.META.getDescricao()));
				adicionaNoGrupo(cars, "Planejamento", lst);

			} else if (modulo.equals(TipoModulo.ANALISE) && lstModulo.contains(modulo.name())) {
				List<SelectItem> lst = new ArrayList<>();
				lst.add(new SelectItem(TipoRotina.INDICADORES, TipoRotina.INDICADORES.getDescricao()));
				adicionaNoGrupo(cars, "Análise", lst);
			}

		}

		if (lstBloqueios.size() > 0) {
			grupo = new SelectItemGroup("Bloqueios");
			SelectItem[] bloqueios = new SelectItem[lstBloqueios.size()];
			for (int i = 0; i < bloqueios.length; i++) {
				bloqueios[i] = new SelectItem(lstBloqueios.get(i), lstBloqueios.get(i).getDescricao());
			}
			grupo.setSelectItems(bloqueios);
			cars.add(grupo);
		}

	}

	public void gravarAcessos() {
		try {

			if (!pessoa.getOrigem().equals(seguranca.getPessoaLogadoOrigem()))
				throw new NegocioException("usuário atual só pode atualizar acessos de sua unidade");

			pessoas.guardarRotinas(pessoa, listaRotinas);

			FacesUtil.addInfoMessage("registro gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

}