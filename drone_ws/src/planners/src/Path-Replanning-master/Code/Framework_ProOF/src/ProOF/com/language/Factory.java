/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.language;

import java.util.LinkedList;

/**
 *
 * @author marcio
 * @param <No>
 */
public abstract class Factory<No extends Approach> {
    
    private Approach[] array;
    public final Approach[] split() throws Exception{
        if(array==null){
            LinkedList<Approach> list = new LinkedList<Approach>();
            int index = 0;
            Approach node = build(index);
            while(node!=null){
                list.addLast(node);
                index++;
                node = build(index);
            }
            array = list.toArray(new Approach[list.size()]);
        }
        return array;
    }
    
    public abstract String name();
    
    public final String class_name(){
        return this.getClass().getName();
    }
    public abstract No build(int index) throws Exception;

    private No choise = null;
    public final No build_runner(int index) throws Exception{
        System.out.println("build_runner = "+index);
        if(choise!=null){
            throw new Exception("the build runner can't be call more that one time");
        }
        choise = build(index);
        return choise;
    }
    public final No choise(){
        return choise;
    }
            
    @Override
    public final String toString() {
        return name();
    }
}
