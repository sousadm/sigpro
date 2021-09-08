package sigpro;


import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;

public class AnaliseCliente {



	public AnaliseCliente(Pessoa pessoa) {

	}

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {
		
		Date inicio = SuporteData.primeiraDataDoMes(new Date());
		inicio = SuporteData.incrementaMesNaData(inicio, -3);

		Date termino = SuporteData.incrementaMesNaData(inicio, 3);
		termino = SuporteData.incrementaDiaNaData(termino, -1);

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		Pessoa pessoa = manager.find(Pessoa.class, 17L);

		StringBuilder condicao;

		condicao = new StringBuilder();
		condicao.append("select MIN(e.dataEmissao), MAX(e.dataEmissao), AVG(valorTotal), COUNT(*) from expedicao e ");
		condicao.append("where e.cliente = " + pessoa.getCliente().getId());
		condicao.append(" and dataCancelamento is null ");
		condicao.append(" and dataEmissao is not null ");
		Object[] obj = (Object[]) manager.createQuery(condicao.toString()).getSingleResult();

		Date dataPrimeiraCompra = (Date) obj[0];
		Date dataUltimaCompra = (Date) obj[1];
		double valorTicketMedio = (double) obj[2];
		Long volumeCompras = (Long) obj[3];
		double prazoRecebimento;

		String empresa = "sanpil";
		condicao = new StringBuilder();
		condicao.append("select sum(valor * case when (vencimento - cast(emissao as date)) < 0 then 0 ");
		condicao.append(" else vencimento - cast(emissao as date) end) / sum(valor) ");
		condicao.append("from " + empresa + ".Titulo where dataBaixa is null ");
		condicao.append(" and responsavel_id = " + pessoa.getId());
		condicao.append(" and tipoDC = 'RECEBER' ");
		condicao.append(" and tipoDocto not in ('CRE', 'DEB', 'DIN') ");
		condicao.append(" and cast(emissao as date) < " + Suporte.formataDataSQL_Quoted(termino));
		if (inicio != null)
			condicao.append(" and cast(emissao as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		Double prazoNegociacao = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();

		condicao = new StringBuilder();
		condicao.append("select sum(valor * case when (dataBaixa - cast(emissao as date)) < 0 then 0 ");
		condicao.append(" else dataBaixa - cast(emissao as date) end) / sum(valor) ");
		condicao.append("from " + empresa + ".Titulo ");
		condicao.append("where dataBaixa is not null ");
		condicao.append(" and responsavel_id = " + pessoa.getId());
		condicao.append(" and tipoDC = 'RECEBER' ");
		condicao.append(" and tipoDocto not in ('CRE') ");
		condicao.append(" and cast(emissao as date) < " + Suporte.formataDataSQL_Quoted(termino));
		if (inicio != null)
			condicao.append(" and cast(emissao as date) >= " + Suporte.formataDataSQL_Quoted(inicio));
		try {
			prazoRecebimento = (Double) manager.createNativeQuery(condicao.toString()).getSingleResult();
		} catch (Exception e) {
			prazoRecebimento = 0.0;
		}

		System.out.println("Primeira compra: " + dataPrimeiraCompra);
		System.out.println("Última compra: " + dataUltimaCompra);
		System.out.println("Ticket médio: " + valorTicketMedio);
		System.out.println("Volume de compras: " + volumeCompras);
		System.out.println("Prazo negociado: " + prazoNegociacao);
		System.out.println("Prazo de pagamento: " + prazoRecebimento);

	}

}
