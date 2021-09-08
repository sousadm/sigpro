package sigpro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Participante;
import com.sousa.sigpro.model.Pergunta;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.model.Questionario;
import com.sousa.sigpro.model.Resposta;
import com.sousa.sigpro.model.TermoDeAbertura;
import com.sousa.sigpro.model.tipo.TipoImportancia;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoStakeHolder;
import com.sousa.sigpro.util.SuporteData;

public class TesteProjeto {

	static EntityManagerFactory factory;
	static EntityManager manager;
	static EntityTransaction trx;

	public static void main(String[] args) {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();
		trx = manager.getTransaction();

		testeCadastroDeProjeto();

	}

	public static void testeCadastroDeProjeto() {
		try {

			trx.begin();

			Pessoa empresa = manager.find(Pessoa.class, 1L);

			TermoDeAbertura abertura = new TermoDeAbertura();
			abertura.setDataAbertura(new Date());
			abertura.setDataPrevistaTermino(SuporteData.incrementaDiaNaData(new Date(), 90));
			abertura.setJustificativa("teste inicial");
			abertura.setObjetivo("teste inicial");

			List<Resposta> respostas = new ArrayList<>();
			Questionario questionario = manager.find(Questionario.class, 1L);
			for (Pergunta pergunta : questionario.getPerguntas()) {
				Resposta resposta = new Resposta(pergunta, null);
				resposta = manager.merge(resposta);
				manager.flush();
				respostas.add(resposta);
			}

			Projeto projeto = new Projeto();
			projeto.setAbertura(abertura);
			projeto.setDataCadastro(new Date());
			projeto.setTitulo("projeto teste");
			projeto.setUsuario(empresa.getUsuario());
			projeto.setRespostas(respostas);

			Participante participante = null;
			Pessoa membro = null;
			for (int i = 2; i <= 7; i++) {
				membro = manager.find(Pessoa.class, new Long(i));
				participante = new Participante(membro, null);
				participante = manager.merge(participante);
				participante.setAtribuicao(membro.getNome());
				participante.setImpacto(TipoImportancia.ALTO);
				participante.setInfluencia(TipoImportancia.ALTO);
				participante.setInteresse(TipoImportancia.ALTO);
				participante.setPoder(TipoImportancia.ALTO);
				participante.setTipo(TipoStakeHolder.CLIENTE);
				manager.flush();
				projeto.getParticipantes().add(participante);
			}

			membro = new Pessoa();
			membro.setNome("APAGAR ESTE REGISTRO");
			membro.setTipo(TipoPessoa.NA);
			membro.setOrigem(empresa);
			membro.setCliente(null);
			membro.setColaborador(null);
			membro.setFornecedor(null);
			membro.setVendedor(null);
			membro.setTransportador(null);
			membro.setUsuario(null);
			membro.setAgente(null);
			participante = manager.merge(participante);
			participante.setImpacto(TipoImportancia.ALTO);
			participante.setInfluencia(TipoImportancia.ALTO);
			participante.setInteresse(TipoImportancia.ALTO);
			participante.setPoder(TipoImportancia.ALTO);
			participante.setTipo(TipoStakeHolder.CLIENTE);
			manager.flush();
			projeto.getParticipantes().add(participante);

			projeto = manager.merge(projeto);

			trx.commit();
			System.out.println("gravado projeto número: " + projeto.getId());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}