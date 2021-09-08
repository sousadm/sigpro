package sigpro;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.model.Pessoa;

public class CategoriaTeste {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		Pessoa empresa = manager.find(Pessoa.class, 1L);

		StringBuilder condicao = new StringBuilder();
		condicao.append("select c from Categoria c join c.categoriaPai p ");
		condicao.append(
				"where (c.pessoa = :origem or c.pessoa.origem.unidadeProdutiva.permiteCompartilhaProduto = true)");
		condicao.append("order by p.descricao, c.descricao");
		List<Categoria> lst = manager.createQuery(condicao.toString(), Categoria.class).setParameter("origem", empresa)
				.getResultList();
		for (Categoria cat : lst) {
			System.out.println("Pai:" + cat.getCategoriaPai().getDescricao() + " Filho: " + cat.getDescricao());
		}

	}

	public static List<Categoria> items(Categoria categoria) {
		return null;
	}

}
