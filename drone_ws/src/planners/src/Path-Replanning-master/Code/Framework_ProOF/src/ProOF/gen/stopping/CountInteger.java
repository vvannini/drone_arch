/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.stopping;

import ProOF.com.language.ApproachSingle;


/**
 *
 * @author marcio
 */
public class CountInteger extends ApproachSingle{
    private static CountInteger obj = null;
    public static CountInteger object(){
        if(obj==null){
            obj = new CountInteger();
        }
        return obj;
    }


    private CountInteger() {
    
    }
    
    private long integer_sol;
    
    public void update(){
        integer_sol++;
    }
    public long value(){
        return integer_sol;
    }
    @Override
    public String name() {
        return "CountInteger";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void start() throws Exception {
        integer_sol = 0;
    }
}
