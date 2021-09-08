package sigpro;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class TesteProduto {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) throws Exception {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		StringBuilder condicao = new StringBuilder();
		// condicao.append("select count(*) from Composto c join c.produto p join
		// p.categoria c ");
		condicao.append("select count(*) from Produto p join p.categoria c ");
		condicao.append("where exists (select c from Composto c where c.produto = p ) ");
		Query query = manager.createQuery(condicao.toString());
		Long valor = (Long) query.getSingleResult();

		System.out.println(valor);

	}

}