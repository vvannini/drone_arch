/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.language;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public abstract class Approach{
    private int ID = -1;
    private LinkedList<Integer> dads = new LinkedList<Integer>();
   
    public final int getID() {
        return ID;
    }
    public final void setID(int ID) {
        this.ID = ID;
    }
    
    public static File job;
    
    public abstract String name();
    
    
    public String class_name(){
        return this.getClass().getName();
    }
    
    public abstract String description();
    @Override
    public final String toString(){
        return name();
    }
    
    public abstract void services(LinkerApproaches link) throws Exception;
    public abstract void parameters(LinkerParameters link) throws Exception;
    public abstract void load() throws Exception;
    public abstract void start() throws Exception;
    public abstract boolean validation(LinkerValidations link) throws Exception;
    
    public void finish() throws Exception{
        
    }
    public abstract void results(LinkerResults link) throws Exception;
    
    public void add_dad(int index) {
        dads.addLast(index);
    }
    public LinkedList<Integer> get_dads() {
        return dads;
    }
}
