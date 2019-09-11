/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com;

import java.io.File;

/**
 *
 * @author marcio
 */
public class RgxRule {
    public static final String ANYTHING     = "(.*)";
    public static final String DIRECTORY    = "(.*["+(File.separator.equals("/") ? "/" : "\\\\")+"])";
    public static final String FILE         = "(.*[^"+(File.separator.equals("/") ? "/" : "\\\\")+"])";
    public static final String WORD         = "([a-zA-Z_][a-zA-Z_0-9]*)";
    public static final String INT          = "(([1-9][0-9]+)|([0-9]))";
    
    
    public final String regex;
    public final String[] constraints;
    
    
    public RgxRule(String regex) {
        if(regex==null || regex.isEmpty()){
            regex = ANYTHING;
        }
        this.regex = regex;
        this.constraints = null;
    }
    public RgxRule(String regex, String ...constraints) {
        if(regex==null || regex.isEmpty()){
            regex = ANYTHING;
        }
        this.regex = regex;
        this.constraints = constraints;
    }
    
    public static RgxRule One(String ...extensions) throws Exception{
        if(extensions==null || extensions.length<1){
            throw new Exception("extensions==null || extensions.length<1");
        }
        for(String e : extensions){
            if(!e.matches(WORD)){
                throw new Exception("extension '"+e+"' don't matches '"+WORD+"'");
            }
        }
        
        String s = "(.*\\."+extensions[0]+")";
        for(int i=1; i<extensions.length; i++){
            s += "|(.*\\."+extensions[i]+")";
        }
        return new RgxRule(s);
    } 
    public static RgxRule[] All(String ...extensions) throws Exception{
        if(extensions==null || extensions.length<1){
            throw new Exception("extensions==null || extensions.length<1");
        }
        for(String e : extensions){
            if(!e.matches(WORD)){
                throw new Exception("extension '"+e+"' don't matches '"+WORD+"'");
            }
        }
        RgxRule rules[] = new RgxRule[extensions.length];
        for(int i=0; i<rules.length; i++){
            rules[i] = new RgxRule("(.*\\."+extensions[i]+")");
        }
        return rules;
    }
    public static RgxRule Directory(String[] constraints) {
        return new RgxRule(DIRECTORY, constraints);
    }
}
