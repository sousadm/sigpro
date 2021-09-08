package com.sousa.sigpro.security;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoGrupo;
import com.sousa.sigpro.model.tipo.TipoModulo;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.repository.ContratoAdesaos;
import com.sousa.sigpro.repository.Municipios;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;

@Named
@RequestScoped
public class Seguranca {

	@Inject
	private Pessoas pessoas;
	@Inject
	private ExternalContext externalContext;
	@Inject
	private Municipios municipios;
	@Inject
	private ContratoAdesaos contratos;

	public Seguranca() {

	}

	public String getEditorConfig() {
		return "bold italic underline | font size style color highlight | bullets numbering | "
				+ "alignleft center alignright justify | undo redo | copy cut paste pastetext | outdent indent | removeformat";
	}

	public void defineEnderecoFocus(EnderecoFocus ende, Endereco endereco) {
		endereco.setCep(ende.getCep());
		endereco.setBairro(ende.getBairro());
		endereco.setLogradouro(ende.getNome_logradouro());
		Municipio municipio = municipios.porId(Long.parseLong(ende.getCodigo_ibge()));
		endereco.setMunicipio(municipio);
	}

	public boolean isUsuarioTemRotina(TipoRotina tipo) {
		Pessoa user;
		try {
			user = pessoas.porId(getPessoaLogado().getId());
			if (user != null) {
				List<TipoRotina> lst = user.getUsuario().getRotinas();
				return lst.contains(tipo);
			} else {
				return false;
			}
		} finally {
			user = null;
		}
	}

	public boolean isUsuarioConvidado() {
		List<TipoGrupo> grupos = getPessoaLogado().getUsuario().getGrupos();
		return grupos.contains(TipoGrupo.CON);
	}

	public String getEmpresa() {
		String nome = (String) Suporte.getAtributoDaSessao("empresa");
		return nome.toUpperCase();
	}

	public boolean isUsuarioPrincipal() {
		Pessoa pessoa = getPessoaLogado();
		if (pessoa == null)
			return false;
		else
			return pessoa.getOrigem() == pessoa;
	}

	public boolean isClinica() {
		return getPessoaLogadoOrigem().getAtividade() == TipoAtividadePrincipal.CLINICA;
	}

	public boolean isAgenteFinanceiro() {
		return getPessoaLogadoOrigem().getDefineAgente();
	}

	public boolean isEmpresaNormal() {
		return getPessoaLogadoOrigem().getPJ() == null || getPessoaLogadoOrigem().getPJ().getRegime() == null ? false
				: getPessoaLogadoOrigem().getPJ().getRegime().equals(NFRegimeTributario.NORMAL);
	}

	public boolean isEmpresaContrate() {
		return getEmpresa().toUpperCase().equals("contrate".toUpperCase());
	}

	public String conteudoTemplate(String arquivo, VelocityContext context) {
		URL resource = getClass().getResource("/");
		String path = resource.getPath() + "emails";
		Properties props = new Properties();
		props.setProperty("file.resource.loader.path", path);
		VelocityEngine engine = new VelocityEngine(props);
		engine.init();
		Template template = engine.getTemplate(arquivo);
		StringWriter conteudo = new StringWriter();
		template.merge(context, conteudo);
		return conteudo.toString();
	}

	public String pathClass(String valor) {
		String path = this.getClass().getResource("/").getPath();
		return path.concat(valor + "/");
		// URL resource = getClass().getResource("/" + valor);
		// return resource.getPath();
	}

	public boolean isUsuarioNaoConvidado() {
		return !externalContext.isUserInRole("CON");
	}

	public boolean isPermiteModuloAnalise() {
		List<TipoGrupo> grupos = getPessoaLogado().getUsuario().getGrupos();
		List<TipoRotina> rotinas = getPessoaLogado().getUsuario().getRotinas();
		List<String> modulos = getPessoaLogado().getModulos();
		return grupos != null && !grupos.contains(TipoGrupo.CON) && modulos.contains(TipoModulo.ANALISE.name())
				&& rotinas.contains(TipoRotina.INDICADORES);
	}

	public StreamedContent getLogomarca() throws FileNotFoundException {
		return Suporte.imagemStreamedOutContent(Suporte.logomarca);
	}

	public StreamedContent getImagemProduto(String arquivo) throws FileNotFoundException {
		return Suporte.imagemStreamedOutContent(arquivo);
	}

	public StreamedContent getImagem() {
		String arquivo = (String) Suporte.getAtributoDaSessao("imagem");
		File foto = new File(arquivo);
		DefaultStreamedContent content = null;
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(foto));
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			in.close();
			content = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "image/jpeg");
		} catch (Exception e) {
			// log.error(e);
		}
		return content;
	}

	public void definePastaDoCliente(String empresa) throws SQLException {
		String destination = "/UPLOADS/" + empresa + "/";
		File diretorio = new File(destination);
		if (!diretorio.exists()) {
			diretorio.mkdirs();
		}
		Suporte.uploadLocal = destination;
		Suporte.DATA_LICENCA_VALIDA = contratos.getDataValidade(empresa);
	}

	public void prapara(String usuario) throws IOException, SQLException {
		URL resource = getClass().getResource("/META-INF");
		String arquivo = resource.getPath() + "Parametros.properties";
		Properties prop = Suporte.getPropriedade(arquivo);
		
		Suporte.EMPRESA_PADRAO = prop.getProperty("EMPRESA_PADRAO");
		Suporte.SENHA_BANCO_DADOS = prop.getProperty("SENHA_BANCO_DADOS");
		Suporte.USUARIO_BANCO_DADOS = usuario; // prop.getProperty("USUARIO_BANCO_DADOS");
		
		Suporte.TOKEN_PROVISORIO = prop.getProperty("TOKEN_PROVISORIO");
		Suporte.CNPJ_PROVISORIO = prop.getProperty("CNPJ_PROVISORIO");
		Suporte.URL_DATABASE = prop.getProperty("URL_DATABASE");
		Suporte.TEMPO_PARA_ENCERRAMENTO_CAIXA = Integer.parseInt(prop.getProperty("TEMPO_PARA_ENCERRAMENTO_CAIXA"));
		Suporte.PRAZO_VENCIMENTO_GERENCIAVEL = Integer.parseInt(prop.getProperty("PRAZO_VENCIMENTO_GERENCIAVEL"));
		Suporte.SERIE_NFE = Integer.parseInt(prop.getProperty("SERIE_NFE"));
		Suporte.PERCENTUAL_MARGEM_TRIBUTACAO = Double.parseDouble(prop.getProperty("PERCENTUAL_MARGEM_TRIBUTACAO"));
		Suporte.PERCENTUAL_MARGEM_CONTRIBUICAO = Double.parseDouble(prop.getProperty("PERCENTUAL_MARGEM_CONTRIBUICAO"));
		Suporte.PERCENTUAL_MARGEM_LUCRO = Double.parseDouble(prop.getProperty("PERCENTUAL_MARGEM_LUCRO"));
		Suporte.PERCENTUAL_MARGEM_RESIDUAL = Double.parseDouble(prop.getProperty("PERCENTUAL_MARGEM_RESIDUAL"));
		Suporte.VERSAO_EMISSOR = prop.getProperty("VERSAO_EMISSOR");

		
	}

	public Pessoa getPessoaLogado() {
		return pessoas.porUsuario(Suporte.USUARIO_LOGADO);
	}

	public Pessoa getPessoaLogadoOrigem() {
		Pessoa pessoa = getPessoaLogado();
		if (pessoa.getOrigem() == null)
			return pessoa;
		else
			return pessoa.getOrigem();
	}

	public Endereco enderecoEmpresa() {
		return getPessoaLogadoOrigem().getEndereco(TipoEndereco.COMERCIAL);
	}

	public String getDataLicenca() {
		return Suporte.DATA_LICENCA_VALIDA != null
				? "válido até ".concat(SuporteData.formataDataToStr(Suporte.DATA_LICENCA_VALIDA, "dd/MM/yyyy"))
				: "licença não localizada";
	}

	public boolean isBloqueioValidade() {
		int tempo = 0;
		if (Suporte.DATA_LICENCA_VALIDA != null)
			tempo = SuporteData.diasEntreDatas(new Date(), Suporte.DATA_LICENCA_VALIDA);
		return tempo < 0;
	}

}