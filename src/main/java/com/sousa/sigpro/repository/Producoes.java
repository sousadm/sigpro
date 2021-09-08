package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Colaborador;
import com.sousa.sigpro.model.Composto;
import com.sousa.sigpro.model.Cronologia;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.Montagem;
import com.sousa.sigpro.model.MontagemItem;
import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.model.Producao;
import com.sousa.sigpro.model.ProducaoExpedicao;
import com.sousa.sigpro.model.ProducaoMontagem;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.tipo.TipoStatusProducao;
import com.sousa.sigpro.repository.filter.OrdemServicoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jpa.Transactional;

public class Producoes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Produtos produtos;

	public ProducaoMontagem montagemPorId(Long id) {
		String condicao = "select p from ProducaoMontagem p where p.id = " + id;
		return manager.createQuery(condicao, ProducaoMontagem.class).getSingleResult();
	}

	public ProducaoExpedicao expedicaoPorId(Long id) {
		String condicao = "select p from ProducaoExpedicao p where p.id = " + id;
		return manager.createQuery(condicao, ProducaoExpedicao.class).getSingleResult();
	}

	public ProducaoMontagem ultimoPorItem(MontagemItem item) {
		ProducaoMontagem montagem = null;
		String condicao = "select max(p.id) from ProducaoMontagem p where p.montagemItem = " + item.getId();
		Long id = manager.createQuery(condicao, Long.class).getSingleResult();
		if (id != null && id > 0)
			montagem = montagemPorId(id);
		return montagem;
	}

	public ProducaoExpedicao ultimoPorItem(ExpedicaoItem item) {
		ProducaoExpedicao montagem = null;
		String condicao = "select max(p.id) from ProducaoExpedicao p where p.ordemItemProducao = " + item.getId();
		Long id = manager.createQuery(condicao, Long.class).getSingleResult();
		if (id != null && id > 0)
			montagem = expedicaoPorId(id);
		return montagem;
	}

	public List<Producao> lista(OrdemServicoFilter filtro) {
		return manager.createNamedQuery("Producao.listaPorColaborador", Producao.class)
				.setParameter("operador", filtro.getColaborador()).getResultList();
	}

	@Transactional
	public OrdemServico encerrar(OrdemServico ordem) {
		List<ExpedicaoItem> lst = manager.createNamedQuery("ExpedicaoItem.servicoPendente", ExpedicaoItem.class)
				.setParameter("ordem", ordem).getResultList();
		if (lst != null && lst.isEmpty()) {
			Date termino = ordem.getCronologia().getTermino();
			ordem = manager.find(OrdemServico.class, ordem.getId());
			ordem.getCronologia().setTermino(termino);
			ordem = manager.merge(ordem);
		}
		return ordem;
	}

	public List<Producao> listaComissao(OrdemServicoFilter filtro) {

		String condicao = "select p from Producao p where p.titulo is not null ";
		if (filtro.getColaborador() != null)
			condicao = condicao + " and p.operador = " + filtro.getColaborador().getId();

		if (filtro.getInicio() != null)
			condicao = condicao + " and p.cronologia.termino >= " + Suporte.formataDataSQL_Quoted(filtro.getInicio());

		if (filtro.getTermino() != null)
			condicao = condicao + " and p.cronologia.termino <= " + Suporte.formataDataSQL_Quoted(filtro.getTermino());

		condicao = condicao + "order by p.operador.nome, p.cronologia.termino ";

		return manager.createQuery(condicao, Producao.class).getResultList();

	}

	@Transactional
	public ProducaoExpedicao iniciar(ProducaoExpedicao producao) {

		producao.setUsuario(seguranca.getPessoaLogado().getUsuario());
		producao = manager.merge(producao);

		if (producao.getOrdemItemProducao() != null && producao.getOrdemItemProducao().getProduto() != null) {

			ExpedicaoItem item = producao.getOrdemItemProducao();

			if (item.getCronologia() == null)
				item.setCronologia(new Cronologia());

			item.setStatus(TipoStatusProducao.PRODUCAO);
			item.getCronologia().setInicio(producao.getCronologia().getInicio());
			item = manager.merge(item);

			OrdemServico ordem = item.getServico();
			if (ordem.getCronologia() == null)
				ordem.setCronologia(new Cronologia());
			if (ordem.getCronologia().getInicio() == null) {
				ordem.getCronologia().setInicio(producao.getCronologia().getInicio());
				ordem = manager.merge(ordem);
			}

		}

		return producao;
	}

	@Transactional
	public ProducaoExpedicao pausar(ProducaoExpedicao producao) {

		producao.setUsuario(seguranca.getPessoaLogado().getUsuario());
		ExpedicaoItem item = producao.getOrdemItemProducao();
		Producao ultimo = ultimoPorItem(item);

		if (ultimo == null)
			throw new NegocioException("Último evento não localizado!");

		if (ultimo.getCronologia().getTermino() != null
				&& producao.getCronologia().getInicio().before(ultimo.getCronologia().getTermino()))
			throw new NegocioException("Este comando deve ser superior a: "
					+ SuporteData.formataDataToStr(ultimo.getCronologia().getTermino(), ""));

		ultimo.getCronologia().setTermino(producao.getCronologia().getInicio());
		manager.merge(ultimo);

		producao = manager.merge(producao);

		item.setStatus(TipoStatusProducao.PAUSA);
		item = manager.merge(item);

		return producao;
	}

	@Transactional
	public ProducaoExpedicao reiniciar(ProducaoExpedicao producao) {

		producao.setUsuario(seguranca.getPessoaLogado().getUsuario());

		producao = manager.merge(producao);
		ExpedicaoItem item = producao.getOrdemItemProducao();

		Producao ultimo = ultimoPorItem(item);
		if (ultimo == null)
			throw new NegocioException("Último evento não localizado!");

		if (ultimo.getCronologia().getTermino() != null
				&& producao.getCronologia().getInicio().before(ultimo.getCronologia().getTermino()))
			throw new NegocioException("Este comando deve ser superior a: "
					+ SuporteData.formataDataToStr(ultimo.getCronologia().getTermino(), ""));

		ultimo.getCronologia().setTermino(producao.getCronologia().getInicio());
		manager.merge(ultimo);

		item.setStatus(TipoStatusProducao.PRODUCAO);
		item = manager.merge(item);

		return producao;
	}

	@Transactional
	public ProducaoExpedicao encerra(ProducaoExpedicao producao) {

		producao.setUsuario(seguranca.getPessoaLogado().getUsuario());

		if (producao.getOrdemItemProducao() != null) {
			ExpedicaoItem item = producao.getOrdemItemProducao();
			Producao ultimo = ultimoPorItem(item);

			if (ultimo == null)
				throw new NegocioException("Último evento não localizado!");

			if (ultimo.getCronologia().getTermino() != null
					&& producao.getCronologia().getInicio().before(ultimo.getCronologia().getTermino()))
				throw new NegocioException("Este comando deve ser superior a: "
						+ SuporteData.formataDataToStr(ultimo.getCronologia().getTermino(), ""));

			ultimo.getCronologia().setTermino(producao.getCronologia().getInicio());
			manager.merge(ultimo);

			item.setStatus(TipoStatusProducao.CONCLUIDO);
			item.getCronologia().setTermino(producao.getCronologia().getInicio());
			item = manager.merge(item);

			producao.getCronologia().setTermino(producao.getCronologia().getInicio());

		}

		return manager.merge(producao);

	}

	@Transactional
	public ProducaoMontagem iniciar(ProducaoMontagem producao) {

		producao.setUsuario(seguranca.getPessoaLogado().getUsuario());
		producao = manager.merge(producao);

		if (producao.getMontagemItem() != null && producao.getMontagemItem().getProduto() != null) {

			MontagemItem item = producao.getMontagemItem();
			if (item.getCronologia() == null)
				item.setCronologia(new Cronologia());

			item.setStatus(TipoStatusProducao.PRODUCAO);
			item.getCronologia().setInicio(producao.getCronologia().getInicio());
			item = manager.merge(item);

			Montagem montagem = item.getMontagem();
			if (montagem.getInicio() == null) {
				montagem.setInicio(producao.getCronologia().getInicio());
				montagem = manager.merge(montagem);
			}
		}

		return producao;
	}

	@Transactional
	public ProducaoMontagem pausar(ProducaoMontagem producao) {

		MontagemItem item = producao.getMontagemItem();
		ProducaoMontagem ultimo = ultimoPorItem(item);
		if (ultimo == null)
			throw new NegocioException("Último evento não localizado!");

		if (ultimo.getCronologia().getTermino() != null
				&& producao.getCronologia().getInicio().before(ultimo.getCronologia().getTermino()))
			throw new NegocioException("Este comando deve ser superior a: "
					+ SuporteData.formataDataToStr(ultimo.getCronologia().getTermino(), ""));

		ultimo.getCronologia().setTermino(producao.getCronologia().getInicio());
		ultimo = manager.merge(ultimo);

		item.setStatus(TipoStatusProducao.PAUSA);
		item = manager.merge(item);

		producao.setQuantidade(ultimo.getQuantidade());
		producao.setUsuario(seguranca.getPessoaLogado().getUsuario());
		return manager.merge(producao);

	}

	@Transactional
	public ProducaoMontagem reiniciar(ProducaoMontagem producao) {

		MontagemItem item = producao.getMontagemItem();
		ProducaoMontagem ultimo = ultimoPorItem(item);
		if (ultimo == null)
			throw new NegocioException("Último evento não localizado!");

		if (ultimo.getCronologia().getTermino() != null
				&& producao.getCronologia().getInicio().before(ultimo.getCronologia().getTermino()))
			throw new NegocioException("Este comando deve ser superior a: "
					+ SuporteData.formataDataToStr(ultimo.getCronologia().getTermino(), ""));

		ultimo.getCronologia().setTermino(producao.getCronologia().getInicio());
		ultimo = manager.merge(ultimo);

		item.setStatus(TipoStatusProducao.PRODUCAO);
		item = manager.merge(item);

		producao.setQuantidade(ultimo.getQuantidade());
		producao.setUsuario(seguranca.getPessoaLogado().getUsuario());
		return manager.merge(producao);

	}

	@Transactional
	public ProducaoMontagem encerrar(ProducaoMontagem producao) {

		MontagemItem item = producao.getMontagemItem();
		Montagem montagem = item.getMontagem();

		Produto produto = producao.getMontagemItem().getProduto();
		List<Composto> compostos = produtos.compostosPorProduto(produto);
		for (Composto composto : compostos) {
			Produto componente = composto.getComponente();
			componente.baixarEstoque(producao.getQuantidade() * composto.getQuantidade());
			componente = manager.merge(componente);
			manager.flush();
		}

		produto.adicionarEstoque(producao.getQuantidade());
		produto = manager.merge(produto);
		manager.flush();

		boolean aberto = false;

		for (MontagemItem mi : montagem.getItems()) {
			aberto = mi.getQuantidade() > mi.getProduzido();
			if (aberto) {
				break;
			}
		}

		if (aberto == false) {
			montagem.setTermino(new Date());
			montagem = manager.merge(montagem);
			manager.flush();
			item.setMontagem(montagem);
		}

		item.setProduzido(item.getProduzido() + producao.getQuantidade());
		item.setStatus(TipoStatusProducao.ENCERRADO);
		item = manager.merge(item);
		manager.flush();

		producao.setMontagemItem(item);
		producao = manager.merge(producao);
		return producao;

	}

	public List<ExpedicaoItem> listaComissionar(OrdemServicoFilter filtro) {

		String condicao = "select distinct e from ProducaoExpedicao p join p.ordemItemProducao e "
				+ "join e.servico s where p.ordemItemProducao.dataComissao is null and e.produto.custo.metodo <> 'NONE' ";

		if (filtro.getColaborador() != null)
			condicao = condicao + " and p.operador = " + filtro.getColaborador().getId();

		if (filtro.getInicio() != null)
			condicao = condicao + " and p.cronologia.termino >= " + Suporte.formataDataSQL_Quoted(filtro.getInicio());

		if (filtro.getTermino() != null)
			condicao = condicao + " and p.cronologia.termino <= " + Suporte.formataDataSQL_Quoted(filtro.getTermino());

		if (filtro.getPlaca() != null && !filtro.getPlaca().isEmpty())
			condicao = condicao + " and UPPER(s.veiculo.placa) = '" + filtro.getPlaca().toUpperCase() + "' ";

		if (filtro.getNome() != null && !filtro.getNome().isEmpty())
			condicao = condicao + " and UPPER(e.expedicao.cliente.nome) like '%" + filtro.getNome().toUpperCase()
					+ "%' ";

		return manager.createQuery(condicao, ExpedicaoItem.class).getResultList();
	}

	public Long tempoMillis(ExpedicaoItem item, Colaborador operador) {
		Long millis = (long) 0;

		List<Producao> lst = manager.createNamedQuery("Producao.tempoPorOperador", Producao.class)
				.setParameter("item", item).setParameter("operador", operador).getResultList();
		for (Producao producao : lst) {
			millis += Suporte.MillisEntreDatas(producao.getCronologia().getInicio(),
					producao.getCronologia().getTermino());
		}
		return millis;
	}

}