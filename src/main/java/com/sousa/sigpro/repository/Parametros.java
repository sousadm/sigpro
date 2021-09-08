package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Parametro;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;;

public class Parametros implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public List<Parametro> lista(String codigo, String grupo, Pessoa pessoa) {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select p from Parametro p where 1 = 1 ");
		if (grupo != null)
			condicao.append("	and p.grupo = " + Suporte.getQuotedStr(grupo));
		if (codigo != null)
			condicao.append("	and p.codigo = " + Suporte.getQuotedStr(codigo));
		if (pessoa != null && pessoa.isExiste())
			condicao.append("and p.empresa = " + pessoa.getId());
		condicao.append(" order by p.codigo");
		return manager.createQuery(condicao.toString(), Parametro.class).getResultList();
	}

	public List<Parametro> listaPorGrupo(String grupo) {
		return lista(null, grupo, seguranca.getPessoaLogadoOrigem());
	}

	public List<Parametro> listaPorGrupo(String grupo, Pessoa pessoa) {
		return lista(null, grupo, pessoa);
	}

	public Parametro porId(Long valor) {
		return manager.find(Parametro.class, valor);
	}

	@Transactional
	public void guardar(Parametro parametro) {
		parametro = manager.merge(parametro);
	}

	@Transactional
	public void guardar(List<Parametro> lista) {
		Parametro param;
		for (Parametro item : lista) {
			List<Parametro> lst = this.lista(item.getCodigo(), item.getGrupo(), item.getEmpresa());
			if (lst == null || lst.size() == 0) {
				param = new Parametro(item.getCodigo(), item.getGrupo(), item.getValor(), item.getEmpresa());
			} else {
				param = lst.get(0);
				param.setValor(item.getValor());
			}
			manager.merge(param);
		}
	}

	public void guardar(String valor, String codigo, String grupo, Pessoa pessoa) {

		Parametro param;

		List<Parametro> lst = this.lista(codigo, grupo, pessoa);
		if (lst == null || lst.size() == 0) {
			param = new Parametro();
			param.setEmpresa(pessoa);
			param.setGrupo(grupo);
			param.setCodigo(codigo);
		} else {
			param = lst.get(0);
		}
		param.setValor(valor);
		guardar(param);
	}

	public Parametro porCodigoGrupo(String codigo, String grupo, Pessoa pessoa) {
		List<Parametro> lst = manager.createNamedQuery("Parametro.definicao", Parametro.class)
				.setParameter("grupo", grupo).setParameter("codigo", codigo)
				.setParameter("empresa", seguranca.getPessoaLogadoOrigem()).getResultList();
		if (lst == null || lst.size() == 0)
			return null;
		else
			return lst.get(0);
	}

}
