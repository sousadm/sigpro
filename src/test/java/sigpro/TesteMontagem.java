package sigpro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.sousa.sigpro.model.ProducaoMontagemLista;
import com.sousa.sigpro.model.tipo.TipoUnidade;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;

public class TesteMontagem {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) throws Exception {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		try {

			Date inicio = SuporteData.somenteData("01/06/2020");
			Date termino = SuporteData.somenteData("30/06/2020");

			List<ProducaoMontagemLista> lista = new ArrayList<>();

			StringBuilder condicao = new StringBuilder();
			condicao.append("select p.sku, p.unidade, p.nome, sum(m.quantidade * c.quantidade) ");
			condicao.append("from ProducaoMontagem m join m.montagemItem i, ");
			condicao.append(" Composto c join c.componente p ");
			condicao.append("where c.produto = i.produto ");
			condicao.append("   and m.comando.tipo = 'ENCERRAR' ");
			condicao.append("	and m.cronologia.termino >= " + Suporte.formataDataSQL_Quoted(inicio));
			condicao.append("	and m.cronologia.termino <= " + Suporte.formataDataSQL_Quoted(termino));
			condicao.append(" group by p.sku, p.nome, p.unidade  ");

			Query query = manager.createQuery(condicao.toString());

			List<Object[]> lstObj = (List<Object[]>) query.getResultList();
			for (Object[] obj : lstObj) {
				if (obj != null) {
					lista.add(new ProducaoMontagemLista("Consumido", (String) obj[0],
							((TipoUnidade) obj[1]).getCodigo(), (String) obj[2], (Double) obj[3]));
				}
			}

			for (ProducaoMontagemLista producao : lista) {
				System.out.println(producao.getDescricao()+" - " + producao.getQuantidade());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}