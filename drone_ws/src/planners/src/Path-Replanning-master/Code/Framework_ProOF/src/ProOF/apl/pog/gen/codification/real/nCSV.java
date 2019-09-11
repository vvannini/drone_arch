/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.codification.real;

import ProOF.apl.pog.gen.callback.cbCSV;
import ProOF.com.language.ApproachSingle;
import java.io.PrintStream;
import java.util.ArrayList;


/**
 *
 * @author marcio
 */
public class nCSV extends ApproachSingle{
    private static nCSV obj = null;
    public static nCSV object(){
        if(obj==null){
            obj = new nCSV();
        }
        return obj;
    }
    private nCSV() {
    }
    @Override
    public String name() {
        return "CallBack CSV";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private ArrayList<cbCSV> call_back_list = new ArrayList<cbCSV>();
    
    @Override
    public void start() throws Exception {
        
    }
    public void add(cbCSV obj){
        call_back_list.add(obj);
    }
    public <T> void title(PrintStream out, T data) throws Exception {
        for(cbCSV cb : call_back_list){
            cb.title(out, data);
        }
    }
    public <T> void values(PrintStream out, T data) throws Exception {
        for(cbCSV cb : call_back_list){
            cb.values(out, data);
        }
    }
}
