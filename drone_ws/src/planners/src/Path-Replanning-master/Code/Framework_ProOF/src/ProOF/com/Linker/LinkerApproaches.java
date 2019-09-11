/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Linker;

import ProOF.com.language.Factory;
import ProOF.com.language.Approach;


/**
 *
 * @author marcio
 */
public interface LinkerApproaches {

    public <T extends Approach> T add(T service) throws Exception;
    public void add(Factory factory) throws Exception;

    public Approach get(Factory factory) throws Exception;
    public <T extends Approach> T get(Factory factory, T a) throws Exception;
    
    public Approach get(Factory factory, Class type) throws Exception;
    public <T extends Approach> T get(Factory factory, Class type, T a) throws Exception;
    
    public Approach[] gets(Factory factory) throws Exception;
    
    
    public Approach need(Class type) throws Exception;
    public <T extends Approach> T need(Class type, T a) throws Exception;
    
    public Approach[] needs(Class type) throws Exception;
    public <T extends Approach> T[] needs(Class type, T[] a) throws Exception;
    
    public Approach wish(Class type) throws Exception;
    public <T extends Approach> T wish(Class type, T a) throws Exception;
    
    public Approach[] wishes(Class type) throws Exception;
    public <T extends Approach> T[] wishes(Class type, T[] a) throws Exception;
    //Verificar possibilidade de já retornar os gerenciadores de operador direto.
    //Verificar a possibilidade de fazer um filtro de tipo nos padrões factory (funções gets) 
}
