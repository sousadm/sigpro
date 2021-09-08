package sigpro;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Agenda;

public class TesteAgenda {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		StringBuilder condicao = new StringBuilder();
		// condicao.append("select a from Agenda a, Pessoa p join p.usuario u ");
		// condicao.append("where a.usuario = p.usuario ");

		condicao.append("select count(*) from Agenda a, Pessoa p join p.usuario u ");
		condicao.append("where a.usuario = p.usuario ");

		try {

			Long valor = manager.createQuery(condicao.toString(), Long.class).getSingleResult();
			System.out.println(valor);

//			List<Agenda> lst = manager.createQuery(condicao.toString(), Agenda.class).getResultList();
//			for (Agenda a : lst) {
//				System.out.println(a.getContato().getNome());
//			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
