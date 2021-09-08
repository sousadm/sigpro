package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.sousa.sigpro.model.Participante;
import com.sousa.sigpro.model.Planner;
import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.model.ProjetoRequisito;
import com.sousa.sigpro.model.Resposta;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoPlanner;
import com.sousa.sigpro.model.tipo.TipoRequisito;
import com.sousa.sigpro.model.tipo.TipoStakeHolder;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.jpa.Transactional;

public class Projetos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	@Inject
	private Seguranca seguranca;

	public Projeto porId(Long id) {
		return manager.find(Projeto.class, id);
	}

	public List<Resposta> respostas(Projeto projeto) {
		return manager.createNamedQuery("Resposta.lista", Resposta.class).setParameter("projeto", projeto)
				.getResultList();
	}

	public List<Participante> participantes(Projeto projeto) {
		return manager.createNamedQuery("Participante.lista", Participante.class).setParameter("projeto", projeto)
				.getResultList();
	}

	public List<ProjetoRequisito> requisitos(Projeto projeto) {
		try {
			String condicao = "from ProjetoRequisito p where p.planner.projeto = :projeto order by p.tipo, p.id";
			return manager.createQuery(condicao, ProjetoRequisito.class).setParameter("projeto", projeto)
					.getResultList();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public List<ProjetoRequisito> premissas(Projeto projeto) {

		List<ProjetoRequisito> premissas = new ArrayList<>();
		List<ProjetoRequisito> requisitos = this.requisitos(projeto);

		if (requisitos != null && requisitos.size() > 0) {
			for (ProjetoRequisito requisito : requisitos) {
				if (requisito.getTipo() == TipoRequisito.PREMISSA) {
					premissas.add(requisito);
				}
			}
		}

		return premissas;
	}

	public List<ProjetoRequisito> restricoes(Projeto projeto) {

		List<ProjetoRequisito> restricoes = new ArrayList<>();
		List<ProjetoRequisito> requisitos = this.requisitos(projeto);

		for (ProjetoRequisito requisito : requisitos) {
			if (requisito.getTipo() == TipoRequisito.RESTRICAO) {
				restricoes.add(requisito);
			}
		}

		return restricoes;
	}

	public List<Planner> objetivos(Projeto projeto) {
		try {
			String condicao = "from Planner p where p.projeto = :projeto and p.tipo = :tipo order by p.id";
			return manager.createQuery(condicao, Planner.class).setParameter("projeto", projeto)
					.setParameter("tipo", TipoPlanner.PROJETO_OBJETIVO).getResultList();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public List<Planner> marcos(Projeto projeto) {
		try {
			String condicao = "from Planner p where p.projeto = :projeto and p.tipo = :tipo order by p.id";
			return manager.createQuery(condicao, Planner.class).setParameter("projeto", projeto)
					.setParameter("tipo", TipoPlanner.PROJETO_MARCO).getResultList();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public List<Planner> riscos(Projeto projeto) {
		try {
			String condicao = "from Planner p where p.projeto = :projeto and p.tipo = :tipo order by p.id";
			return manager.createQuery(condicao, Planner.class).setParameter("projeto", projeto)
					.setParameter("tipo", TipoPlanner.PROJETO_RISCO).getResultList();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public List<Projeto> lista() {
		StringBuilder condicao = new StringBuilder();
		condicao.append("select p from Projeto p JOIN p.usuario u, Pessoa x ");
		condicao.append("WHERE x.usuario = p.usuario and x.origem = " + seguranca.getPessoaLogadoOrigem().getId());
		return manager.createQuery(condicao.toString(), Projeto.class).getResultList();
	}

	@Transactional
	public ProjetoRequisito guardar(ProjetoRequisito requisito) {
		return manager.merge(requisito);
	}

	@Transactional
	public void remover(ProjetoRequisito requisito) {
		try {
			requisito = manager.find(ProjetoRequisito.class, requisito.getId());
			manager.remove(requisito);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("requisito não pode ser excluído.");
		}
	}

	@Transactional
	public Projeto guardar(Projeto projeto, List<Titulo> listaTitulo) {

		if (projeto.getAbertura().getDataAbertura() != null) {

			boolean temCoordenador = false;
			boolean temGerente = false;
			boolean temSponsor = false;

			for (Participante parte : projeto.getParticipantes()) {
				if (parte.getTipo().equals(TipoStakeHolder.COORDENARDOR))
					temCoordenador = true;
				else if (parte.getTipo().equals(TipoStakeHolder.RESPONSAVEL))
					temGerente = true;
				else if (parte.getTipo().equals(TipoStakeHolder.PROPRIETARIO))
					temSponsor = true;
			}
			if (!(temCoordenador || temGerente || temSponsor))
				throw new NegocioException("defina pelo menos um (Gerente/Coordenador/Financeiro)");
		}

		if (listaTitulo != null && listaTitulo.size() > 0) {
			for (Titulo titulo : listaTitulo) {
				Planner planner = titulo.getPlanner();
				if (!planner.isExiste()) {
					planner = manager.merge(planner);
					manager.flush();
				}
				titulo = manager.merge(titulo);
				manager.flush();
			}
		}

		if (projeto.getDataCadastro() == null)
			projeto.setDataCadastro(new Date());
		if (projeto.getUsuario() == null)
			projeto.setUsuario(seguranca.getPessoaLogado().getUsuario());

		return manager.merge(projeto);
	}

	@Transactional
	public void remover(Projeto projeto) {
		try {
			projeto = porId(projeto.getId());
			manager.remove(projeto);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("Projeto não pode ser excluído.");
		}
	}

}