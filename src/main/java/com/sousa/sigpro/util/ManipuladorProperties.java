package com.sousa.sigpro.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
* @author Jean Jorge Michel
* @version 1.0
*/
public class ManipuladorProperties {

    /**
     * Método que retorna um objeto Properties com as informações do sistema.
     * @param arquivo O Caminho físico do arquivo e seu nome.
     * @return Um objeto Properties.
     */
    public Properties carregarPropriedades(String origem) {
        FileInputStream arquivoConfig = null;

        Properties configSistema = new Properties();

        try {
            arquivoConfig = new FileInputStream(origem);

            configSistema.load(arquivoConfig);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                arquivoConfig.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        return configSistema;
    }

    /**
     * Método que imprime no console todas as propriedades e seus respectivos
     * valores encontradas no arquivo de propriedades.
     * @param propriedades Um objeto do tipo Properties.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void imprimir(Map propriedades) {
        Set<String> valores = propriedades.keySet();

        for(String valor: valores) {
            System.out.println(valor + "=" + propriedades.get(valor));
        }
    }

    /**
     * Método que retorna o valor de uma propriedade de acordo com o seu nome.
     * @param propriedades Um objeto do tipo Properties.
     * @param propriedade Nome da propriedade.
     * @return O valor da propriedade.
     */
    public String getPropriedade(Properties propriedades, String propriedade) {
        return propriedades.getProperty(propriedade);
    }

    /**
     * Método que adiciona ao objeto Properties mais uma propriedade.
     * @param chave O nome da nova propriedade a incluir.
     * @param valor O valor da nova propriedade
     * @param propriedades O objeto Properties a ser modificado.
     * @return Retorna o objeto Properties modificado.
     */
    public Properties incluirPropriedade(String chave,
                                         String valor,
                                         Properties propriedades) {
        propriedades.put(chave, valor);

        return propriedades;
    }

    /**
     * Método que remove do objeto Properties uma propriedade.
     * @param chave A propriedade a ser removida
     * @param propriedades O objeto Properties a ser modificado.
     * @return Retorna o objeto Properties modificado.
     */
    public Properties removerPropriedade(String chave, Properties propriedades) {
        propriedades.remove(chave);

        return propriedades;
    }

    /**
     * Método que persiste no disco o arquivo properties.
     * @param propriedades O objeto Properties a ser gravado.
     * @param destino O caminho físico e o nome do arquivo a ser criado.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void salvar(Map propriedades, String destino) {
        FileWriter fw = null;
        
        try {
            fw = new FileWriter(destino);

            Set<String> valores = propriedades.keySet();

            for(String valor: valores) {
                fw.write(valor + "=" + propriedades.get(valor) + "\r\n");
            }
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}