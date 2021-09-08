package sigpro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Banco;
import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.CaixaValor;
import com.sousa.sigpro.model.Titulo;

public class TesteChequeEmitido {

	static EntityManagerFactory factory;
	static EntityManager manager;
	static EntityTransaction trx;

	public static void main(String[] args) {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();
		// trx = manager.getTransaction();

		String condicao = "select v.caixa_id, t.documento, t.vencimento, b.codigo, t.conta, t.agencia, t.valor "
				+ "FROM contrate.titulo t JOIN contrate.caixavalor v on v.titulo_id = t.id JOIN contrate.banco b on b.id = t.banco_id "
				+ "WHERE t.tipodocto = 'CH_CHEQUE' ";
		@SuppressWarnings("unchecked")
		List<Object[]> lstObj = manager.createNativeQuery(condicao).getResultList();
		List<CaixaValor> lista = new ArrayList<>();
		for (Object[] obj : lstObj) {
			CaixaValor cv = new CaixaValor();
			cv.setCaixa((Caixa) obj[0]);

			Titulo titulo = new Titulo();
			titulo.setDocumento((String) obj[1]);
			titulo.setVencimento((Date) obj[2]);
			titulo.setBanco((Banco) obj[3]);
			titulo.setConta((String) obj[4]);
			titulo.setAgencia((String) obj[4]);
			titulo.setValor((Double) obj[5]);
			cv.setTitulo(titulo);

			lista.add(cv);
		}

		for (CaixaValor cv : lista) {
			Caixa caixa = cv.getCaixa();
			System.out.println(cv.getDocumento() + " / " + caixa == null ? "" : caixa.getId());
		}

	}

}
