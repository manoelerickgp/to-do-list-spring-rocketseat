package br.com.manoelerick.todolist.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static void copyNonNullProperties(Object source, Object target) {
        // Depois do 'getNullPropertyNames' pegar todos as propriedades nulas, serão atribuitos dentro do
        // 'BeanUtils.copyProperties' e assim fazendo a mescla dos dados
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /* Este método tem como finalidade criar um 'copy' do objeto do repository para o objeto do body da requisição,
    * ou seja, todos os atributos que forem nulos(não foram alterados), serão substituidos pelos valores anteriores,
    * assim fazendo com que seja alterado somente oque foi requisitado no corpo(body) da requisição. */
    public static String[] getNullPropertyNames(Object source) {

        /* BeanWrapper é uma interface que fornece uma forma de acessar as propriedades de um objeto dentro do java
        * enquanto o BeanWrapperImpl é sua implementação */
        final BeanWrapper src = new BeanWrapperImpl(source);

        // Obtendo as propriedades do objeto, ele irá gerar um 'Array' com todas as propriedades dentro do objeto
        PropertyDescriptor[] pds =  src.getPropertyDescriptors();

        // Criando um conjunto de propriedades com valores null
        Set<String> emptyNames = new HashSet<>();

        // Inserindo as informações dentro do emptyNames

        /* Criando um for para inserir as informações do 'pds' dentro de cara 'pd' */
        for (PropertyDescriptor pd : pds) {
            /* Para cada 'getPropertyValue' será pego o 'nome' da propriedade e será inserido o valor atual da mesma
            no 'srcValue' */
            Object srcValue =  src.getPropertyValue(pd.getName());

            // Depois de pegar a propriedade com seu devido valor, será verificado se é 'null'
            if (srcValue == null) {
                // caso seja null, todas as propriedades serão adicionadas dentro do conjunto 'emptyNames'
                emptyNames.add(pd.getName());
            }
        }
        /* Depois de pegar todos os 'null' será criado um array de string para armazenar todos os nomes
        * dessas propriedades do mesmo tamanho da quantidade de 'null' que foram armazenados*/
        String[] result = new String[emptyNames.size()];

        // Convertendo o conjuto de nomes das propriedades em um array de strings
        return emptyNames.toArray(result);
    }
}
