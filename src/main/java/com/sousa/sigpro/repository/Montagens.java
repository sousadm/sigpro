package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import com.sousa.sigpro.model.Composto;
import com.sousa.sigpro.model.ProducaoMontagemLista;
import com.sousa.sigpro.model.Montagem;
import com.sousa.sigpro.model.MontagemItem;
import com.sousa.sigpro.model.ProducaoMontagem;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.tipo.TipoUnidade;
import com.sousa.sigpro.repository.filter.ProdutoFilter;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Montagens implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Produtos produtos;

	@Transactional
	public Montagem analisar(Montagem montagem) {

		montagem = porId(montagem.getId());

		double saldo = 0;
		for (MontagemItem item : montagem.getItems()) {
			saldo = saldo + item.getQuantidade() - item.getProduzido();
		}

		if (saldo <= 0)
			montagem.setTermino(new Date());

		return manager.merge(montagem);
	}

	public Montagem porId(Long id) {
		return manager.find(Montagem.class, id);
	}

	public MontagemItem itemPorId(Long id) {
		return manager.find(MontagemItem.class, id);
	}

	public List<MontagemItem> items(Montagem montagem) {
		String condicao = "from MontagemItem i where i.montagem = " + montagem.getId() + " order by i.id ";
		return manager.createQuery(condicao, MontagemItem.class).getResultList();
	}

	@Transactional
	public Montagem encerrar(Montagem montagem) {
		montagem.setTermino(new Date());
		return manager.merge(montagem);
	}

	@Transactional
	public Montagem guardar(Montagem montagem) {
		if (montagem.getItems() == null || montagem.getItems().size() == 0)
			throw new NegocioException("sem itens para gravar");
		montagem.setDataCadastro(new Date());
		return manager.merge(montagem);
	}

	@Transactional
	public void remover(Montagem montagem) {
		try {
			montagem = porId(montagem.getId());
			manager.remove(montagem);
		} catch (PersistenceException e) {
			throw new NegocioException("registro não pode ser excluído.");
		} catch (Exception e) {
			throw new NegocioException(e.getMessage());
		}
	}

	public int quantidadeFiltrados(ProdutoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select count(*) from Montagem m ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Montagem> lista(ProdutoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select m from Montagem m ");
		condicao.append(consulta(filtro));
		if (filtro.getPropriedadeOrdenacao() != null) {
			condicao.append(" ORDER BY " + filtro.getPropriedadeOrdenacao() + (filtro.isAscendente() ? "" : " desc"));
		} else {
			condicao.append(" ORDER BY m.id desc");
		}

		Query query = manager.createQuery(condicao.toString(), Montagem.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();
	}

	@Transactional
	public Montagem cancelar(Montagem montagem) {

		for (MontagemItem item : montagem.getItems()) {

			Produto produto = item.getProduto();
			produto.baixarEstoque(item.getProduzido());
			produto = manager.merge(produto);

			List<Composto> compostos = produtos.compostosPorProduto(produto);
			for (Composto composto : compostos) {
				Produto componente = composto.getComponente();
				componente.adicionarEstoque(item.getProduzido() * composto.getQuantidade());
				componente = manager.merge(componente);
				manager.flush();
			}

		}

		montagem.setDataCancelado(new Date());
		return manager.merge(montagem);

	}

	@SuppressWarnings("unchecked")
	public List<MontagemItem> listaProducao(ProdutoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		condicao.append("select i from MontagemItem i join i.montagem m ");
		condicao.append(consulta(filtro));

		if (filtro.getPropriedadeOrdenacao() != null) {
			condicao.append(" ORDER BY " + filtro.getPropriedadeOrdenacao() + (filtro.isAscendente() ? "" : " desc"));
		} else {
			condicao.append(" ORDER BY i.id desc ");
		}

		Query query = manager.createQuery(condicao.toString(), MontagemItem.class);
		if (filtro.getPrimeiroRegistro() != null) {
			query.setFirstResult(filtro.getPrimeiroRegistro().intValue());
			query.setMaxResults(filtro.getQuantidadeRegistros().intValue());
		}

		return query.getResultList();

	}

	private String consulta(ProdutoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("where 1 = 1 ");
		return condicao.toString();
	}

	public List<ProducaoMontagem> lista_ultimo(MontagemItem item) {
		String condicao = "select p from ProducaoMontagem p where p.montagemItem = " + item.getId()
				+ " and (p.cronologia.termino is null or p.comando.tipo = 'ENCERRAR') ";
		return manager.createQuery(condicao, ProducaoMontagem.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ProducaoMontagemLista> lista_imprimir(Date inicio, Date termino) {

		StringBuilder condicao;
		Query query;
		List<Object[]> lstObj;

		List<ProducaoMontagemLista> lista = new ArrayList<>();

		condicao = new StringBuilder();
		condicao.append("select p.sku, p.unidade, p.nome, sum(m.quantidade) ");
		condicao.append("from ProducaoMontagem m join m.montagemItem i join i.produto p ");
		condicao.append("where m.comando.tipo = 'ENCERRAR' ");
		condicao.append("	and m.cronologia.termino >= " + Suporte.formataDataSQL_Quoted(inicio));
		condicao.append("	and m.cronologia.termino <= " + Suporte.formataDataSQL_Quoted(termino));
		condicao.append(" group by p.sku, p.nome, p.unidade  ");

		query = manager.createQuery(condicao.toString());

		lstObj = (List<Object[]>) query.getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				lista.add(new ProducaoMontagemLista("Estoque Produzido", (String) obj[0],
						((TipoUnidade) obj[1]).getCodigo(), (String) obj[2], (Double) obj[3]));
			}
		}

		condicao = new StringBuilder();
		condicao.append("select p.sku, p.unidade, p.nome, sum(m.quantidade * c.quantidade) ");
		condicao.append("from ProducaoMontagem m join m.montagemItem i, ");
		condicao.append(" Composto c join c.componente p ");
		condicao.append("where c.produto = i.produto ");
		condicao.append("   and m.comando.tipo = 'ENCERRAR' ");
		condicao.append("	and m.cronologia.termino >= " + Suporte.formataDataSQL_Quoted(inicio));
		condicao.append("	and m.cronologia.termino <= " + Suporte.formataDataSQL_Quoted(termino));
		condicao.append(" group by p.sku, p.nome, p.unidade  ");

		query = manager.createQuery(condicao.toString());

		lstObj = (List<Object[]>) query.getResultList();
		for (Object[] obj : lstObj) {
			if (obj != null) {
				lista.add(new ProducaoMontagemLista("Estoque Consumido", (String) obj[0],
						((TipoUnidade) obj[1]).getCodigo(), (String) obj[2], (Double) obj[3]));
			}
		}

		return lista;
	}

}