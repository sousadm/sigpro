package com.sousa.sigpro.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.ContratoAdesao;
import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Montagem;
import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.model.Remessa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.clinica.ClinicaAgenda;
import com.sousa.sigpro.model.evento.EventoAquisicao;
import com.sousa.sigpro.model.evento.EventoClinica;
import com.sousa.sigpro.model.evento.EventoContaCorrente;
import com.sousa.sigpro.model.evento.EventoContratoAdesao;
import com.sousa.sigpro.model.evento.EventoDiario;
import com.sousa.sigpro.model.evento.EventoExpedicao;
import com.sousa.sigpro.model.evento.EventoHistorico;
import com.sousa.sigpro.model.evento.EventoMontagem;
import com.sousa.sigpro.model.evento.EventoNotaFiscal;
import com.sousa.sigpro.model.evento.EventoOrdemServico;
import com.sousa.sigpro.model.evento.EventoPessoa;
import com.sousa.sigpro.model.evento.EventoProduto;
import com.sousa.sigpro.model.evento.EventoProjeto;
import com.sousa.sigpro.model.evento.EventoRemessa;
import com.sousa.sigpro.model.evento.EventoTitulo;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jpa.Transactional;

public class Eventos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Evento porId(Long id) {
		return manager.find(Evento.class, id);
	}

	public List<EventoHistorico> lista(CondicaoFilter filtro) {

		List<EventoHistorico> lista = null;
		StringBuilder condicao = new StringBuilder();
		condicao.append("from Evento e where 1 = 1 ");
		if (filtro.getUsuario() != null)
			condicao.append(" and usuario = " + filtro.getUsuario().getId());

		if (filtro.getInicio() != null)
			condicao.append(" and cast(data as date) >= '" + filtro.getInicio() + "' ");

		if (filtro.getTermino() != null)
			condicao.append(" and cast(data as date) <= '" + filtro.getTermino() + "' ");

		condicao.append("order by id");
		List<Evento> lst = manager.createQuery(condicao.toString(), Evento.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (Evento ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(Expedicao expedicao) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoExpedicao e where expedicao = " + expedicao.getId() + " order by id";
		List<EventoExpedicao> lst = manager.createQuery(condicao, EventoExpedicao.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoExpedicao ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(Pessoa pessoa) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoPessoa e where pessoa = " + pessoa.getId() + " order by id";
		List<EventoPessoa> lst = manager.createQuery(condicao, EventoPessoa.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoPessoa ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(ClinicaAgenda agenda) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoClinica e where agenda = " + agenda.getId() + " order by id";
		List<EventoClinica> lst = manager.createQuery(condicao, EventoClinica.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoClinica ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(Titulo titulo) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoTitulo e where titulo = " + titulo.getId() + " order by id";
		List<EventoTitulo> lst = manager.createQuery(condicao, EventoTitulo.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoTitulo ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(Produto produto) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoProduto e where produto = " + produto.getId() + " order by id";
		List<EventoProduto> lst = manager.createQuery(condicao, EventoProduto.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoProduto ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(Aquisicao aquisicao) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoAquisicao e where aquisicao = " + aquisicao.getId() + " order by id";
		List<EventoAquisicao> lst = manager.createQuery(condicao, EventoAquisicao.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoAquisicao ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(Remessa remessa) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoRemessa e where aquisicao = " + remessa.getId() + " order by id";
		List<EventoRemessa> lst = manager.createQuery(condicao, EventoRemessa.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoRemessa ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(Projeto projeto) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoProjeto e where projeto = " + projeto.getId() + " order by id";
		List<EventoProjeto> lst = manager.createQuery(condicao, EventoProjeto.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoProjeto ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(ContaCorrente conta) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoContaCorrente e where conta = " + conta.getId() + " order by id";
		List<EventoContaCorrente> lst = manager.createQuery(condicao, EventoContaCorrente.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoContaCorrente ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(EventoDiario diario) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoDiario e where diario = " + diario.getId() + " order by id";
		List<EventoDiario> lst = manager.createQuery(condicao, EventoDiario.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoDiario ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(ContratoAdesao contrato) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoContratoAdesao e where contrato = " + contrato.getId() + " order by id";
		List<EventoContratoAdesao> lst = manager.createQuery(condicao, EventoContratoAdesao.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoContratoAdesao ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(OrdemServico ordem) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoOrdemServico e where ordem = " + ordem.getId() + " order by id";
		List<EventoOrdemServico> lst = manager.createQuery(condicao, EventoOrdemServico.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoOrdemServico ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(Montagem montagem) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoMontagem e where ordem = " + montagem.getId() + " order by id";
		List<EventoMontagem> lst = manager.createQuery(condicao, EventoMontagem.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoMontagem ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	public List<EventoHistorico> lista(NotaFiscal nota) {

		List<EventoHistorico> lista = null;
		String condicao = "from EventoNotaFiscal e where ordem = " + nota.getId() + " order by id";
		List<EventoNotaFiscal> lst = manager.createQuery(condicao, EventoNotaFiscal.class).getResultList();

		if (lst != null && lst.size() > 0) {
			lista = new ArrayList<>();
			for (EventoNotaFiscal ee : lst) {
				lista.add(new EventoHistorico(ee.getId(), ee.getData(), ee.getDescritivo(), ee.getDocumento()));
			}
		}

		return lista;
	}

	@Transactional
	public EventoNotaFiscal guardar(EventoNotaFiscal evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoMontagem guardar(EventoMontagem evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoOrdemServico guardar(EventoOrdemServico evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoContratoAdesao guardar(EventoContratoAdesao evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoDiario guardar(EventoDiario evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoContaCorrente guardar(EventoContaCorrente evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getConta().getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoProjeto guardar(EventoProjeto evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getProjeto().getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoRemessa guardar(EventoRemessa evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getRemessa().getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoTitulo guardar(EventoTitulo evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getTitulo().getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoPessoa guardar(EventoPessoa evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getPessoa().getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoExpedicao guardar(EventoExpedicao evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getExpedicao().getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoClinica guardar(EventoClinica evento) {
		evento.setDocumento(Suporte.zerosAEsquerda(evento.getAgenda().getId(), 6));
		return manager.merge(evento);
	}

	@Transactional
	public EventoProduto guardar(EventoProduto evento) {
		evento.setDocumento(evento.getProduto().getSku());
		return manager.merge(evento);
	}

	@Transactional
	public EventoAquisicao guardar(EventoAquisicao evento) {
		if (evento.getAquisicao().getNota() != null)
			evento.setDocumento(evento.getAquisicao().getNota().getChave());
		return manager.merge(evento);
	}

}