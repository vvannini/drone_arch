/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import ProOF.com.language.Factory;
import ProOF.com.language.Approach;

/**
 *
 * @author marcio
 */
public class Set {
    protected Approach serv;
    protected Factory fact;
    protected Class type;
    public Set(Approach serv, Factory fact, Class type) {
        this.serv = serv;
        this.fact = fact;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Set)obj).serv.equals(serv);
        /*if(obj instanceof Service){
            return ((Service)obj).equals(serv);
        }else{
            return ((Set)obj).serv.equals(serv);
        }*/
    }

    boolean equal(dFactory f) {
        if(type==null){
            return fact.equals(f.factory) && f.type==null;
        }else{
            return fact.equals(f.factory) && type.equals(f.type);
        }
    }
    
    
}
