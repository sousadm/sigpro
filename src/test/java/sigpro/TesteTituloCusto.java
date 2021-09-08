package sigpro;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;

public class TesteTituloCusto {

	
	static String schema = "contrate";
	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		try {

			String condicao;

			factory = Persistence.createEntityManagerFactory("SistemaPU");
			manager = factory.createEntityManager();

			EntityTransaction trx = manager.getTransaction();
			trx.begin();

			Pessoa pessoa = manager.find(Pessoa.class, 39L);
			
			CentroDeCusto custo = manager.find(CentroDeCusto.class, 1L);
			Titulo titulo = new Titulo();
			titulo.setDescricao("teste");
			titulo.setDesconto(0);
			titulo.setEmissao(new Date());
			titulo.setResponsavel(pessoa);
			
			
			trx.commit();

			System.out.println("ok");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}