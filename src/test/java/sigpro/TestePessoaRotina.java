package sigpro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Pessoa;

public class TestePessoaRotina {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		List<String> rotinas = new ArrayList<>();
		Pessoa pessoa = manager.find(Pessoa.class, 1L);

//		if (pessoa.getUsuario().getRotinas().size() > 0)
//			for (String rotina : pessoa.getUsuario().getRotinas())
//				rotinas.add(rotina);

		System.out.println(rotinas.toString());

		try {

			EntityTransaction trx = manager.getTransaction();
			trx.begin();

			pessoa = manager.merge(pessoa);

			trx.commit();
			System.out.println("gravado com sucesso");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
