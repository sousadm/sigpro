package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Categorias implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public Categoria porId(Long id) {
		return manager.find(Categoria.class, id);
	}

	@SuppressWarnings("unchecked")
	public String ativos() {
		String condicao = "select c from Categoria c where c.categoriaPai != null";
		Query query = manager.createQuery(condicao, Categoria.class);
		List<Categoria> lst = query.getResultList();
		String lista = "";
		for (Categoria categoria : lst) {
			if (categoria.getCategoriaPai() != null && categoria.getCategoriaPai().getTipoProduto().isAtivo()) {
				if (lista != "")
					lista = lista + ", ";
				lista = lista + categoria.getId();
			}
		}
		return lista;
	}

	public Categoria produtoPadrao() {
		String condicao = "select c from Categoria c where categoriaPai.tipoProduto = :tipo and pessoa = :empresa";
		List<Categoria> lst = manager.createQuery(condicao, Categoria.class).setParameter("tipo", TipoProduto.REVENDA)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).getResultList();
		if (lst.isEmpty())
			return null;
		else
			return lst.get(0);
	}

	@Transactional
	public void remover(Categoria categoria) {
		categoria = porId(categoria.getId());
		manager.remove(categoria);
	}

	@Transactional
	public Categoria guardar(Categoria categoria) {
		if (categoria.getDescricao().length() < 3)
			throw new NegocioException("Descrição inválida (mínimo 3 caracteres)");

		if (categoria.getTipoProduto() == null)
			throw new NegocioException("Tipo de cadastro indefinido");

		if (categoria.getPessoa() == null)
			categoria.setPessoa(seguranca.getPessoaLogado().getOrigem());
		return manager.merge(categoria);
	}

	public List<Categoria> raizes() {
		CondicaoFilter filtro = new CondicaoFilter();
		filtro.setDisponivel(true);
		return lista(filtro);
	}

	public List<Categoria> lista(CondicaoFilter filtro) {

		StringBuilder condicao = new StringBuilder();
		// filtro.getDisponivel() => lista categoria RAIZS
		if (filtro.getDisponivel() == true) {
			condicao.append("select c from Categoria c where c.categoriaPai is null ");
			condicao.append(consulta(filtro));
			condicao.append("order by c.descricao");
		} else {
			condicao.append("select c from Categoria c join c.categoriaPai p where 1 = 1 ");
			condicao.append(consulta(filtro));
			condicao.append("order by p.descricao, c.descricao");
		}

		return manager.createQuery(condicao.toString(), Categoria.class).getResultList();

	}

	public int quantidadeFiltrados(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		if (filtro.getDisponivel() == true)
			condicao.append("select count(*) from Categoria c where c.categoriaPai is null ");
		else
			condicao.append("select count(*) from Categoria c join c.categoriaPai p where 1 = 1 ");
		condicao.append(consulta(filtro));
		return ((Number) manager.createQuery(condicao.toString()).getSingleResult()).intValue();
	}

	private String consulta(CondicaoFilter filtro) {
		StringBuilder condicao = new StringBuilder();
		condicao.append(" and (c.pessoa.origem.unidadeProdutiva.permiteCompartilhaProduto = true ");
		condicao.append("  	or c.pessoa = " + seguranca.getPessoaLogado().getOrigem().getId() + " ) ");
		if (Suporte.stringComValor(filtro.getDescricao()))
			condicao.append(" and UPPE(c.descricao) like "
					+ Suporte.getQuotedStr("%" + filtro.getDescricao().toUpperCase() + "%"));
		return condicao.toString();
	}

	public List<Categoria> subcategoriasDe(Categoria categoriaPai) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select c from Categoria c where c.categoriaPai = " + categoriaPai.getId());
		return manager.createQuery(condicao.toString(), Categoria.class).getResultList();
	}

}