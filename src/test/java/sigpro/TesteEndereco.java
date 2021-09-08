package sigpro;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Pessoa;

public class TesteEndereco {

	static String schema = "teste";
	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		try {

			factory = Persistence.createEntityManagerFactory("SistemaPU");
			manager = factory.createEntityManager();

			Pessoa pessoa = manager.find(Pessoa.class, 1L);

			List<Endereco> lst = manager.createNamedQuery("Endereco.porPessoa", Endereco.class).setParameter("pessoa", pessoa)
					.getResultList();
			
			System.out.println(lst.size());
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
