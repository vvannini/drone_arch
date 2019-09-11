/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.model;

import ProOF.com.Linker.LinkerParameterAbstract;
import ProOF.com.RgxRule;
import java.io.File;
import java.io.InputStream;
import java.util.Locale;

/**
 *
 * @author marcio
 */
public class SaveSgl extends LinkerParameterAbstract{
    private String output;
    private int size;
    public SaveSgl() {
        output = "";
        size = 0;
    }
    
    private void printf(String format, Object ...args){
        output += String.format(Locale.ENGLISH,format, args);
    }
    public int size() {
        return size;
    }
    

    @Override
    public String toString() {
        return output;
    }
    
    @Override
    public int Int(String name, int init, int min, int max, String description) throws Exception {
        printf("<type>\n"
                + "Int\n"
                + "<name>\n"
                + "%s\n"
                + "<init>\n"
                + "%d\n"
                + "<min>\n"
                + "%d\n"
                + "<max>\n"
                + "%d\n"
                + "<description>\n"
                + "%s\n",name, init, min, max, description);
        size++;
        return 0;
    }
    @Override
    public int Itens(String name, int init, String itens[], String description) throws Exception {
        if(itens.length<2){
            throw new Exception("itens.length<2");
        }
        String s = itens[0];
        for(int i=1; i<itens.length; i++){
            s += ";"+itens[i];
        }
        printf("<type>\n"
                + "Items\n"
                + "<name>\n"
                + "%s\n"
                + "<init>\n"
                + "%d\n"
                + "<itens>\n"
                + "%s\n"
                + "<description>\n"
                + "%s\n",name, init, s, description);
        size++;
        return 0;
    }
    @Override
    public long Long(String name, long init, long min, long max, String description) throws Exception {
        printf("<type>\n"
                + "Long\n"
                + "<name>\n"
                + "%s\n"
                + "<init>\n"
                + "%d\n"
                + "<min>\n"
                + "%d\n"
                + "<max>\n"
                + "%d\n"
                + "<description>\n"
                + "%s\n",name, init, min, max, description);
        size++;
        return 0;
    }
    @Override
    public double Dbl(String name, double init, double min, double max, String description) throws Exception {
        printf("<type>\n"
                + "Dbl\n"
                + "<name>\n"
                + "%s\n"
                + "<init>\n"
                + "%g\n"
                + "<min>\n"
                + "%g\n"
                + "<max>\n"
                + "%g\n"
                + "<description>\n"
                + "%s\n",name, init, min, max, description);
        size++;
        return 0;
    }
    @Override
    public String String(String name, String init, String description) throws Exception {
        printf("<type>\n"
                + "String\n"
                + "<name>\n"
                + "%s\n"
                + "<init>\n"
                + "%s\n"
                + "<description>\n"
                + "%s\n",name, init, description);
        size++;
        return null;
    }
    @Override
    public String Regex(String name, String init, String regex, String description) throws Exception {
        printf("<type>\n"
                + "Regex\n"
                + "<name>\n"
                + "%s\n"
                + "<init>\n"
                + "%s\n"
                + "<regex>\n"
                + "%s\n"
                + "<description>\n"
                + "%s\n",name, init, regex, description);
        size++;
        return null;
    }
    
    @Override
    public InputStream InputStream(String name, String description) throws Exception{
        printf("<type>\n"
                + "InputStream\n"
                + "<name>\n"
                + "%s\n"
                + "<description>\n"
                + "%s\n",name, description);
        size++;
        return null;
    }

    @Override
    public File FileRgx(String name, String description, RgxRule rule) throws Exception {
        if(rule==null){
            throw new Exception("rule==null");
        }
        printf("<type>\n"
                + "FileRgx\n"
                + "<name>\n"
                + "%s\n"
                + "<description>\n"
                + "%s\n"
                + "<regex>\n"
                + "%s\n"
                + "<constrants>\n"
                + "%d\n", name, description, rule.regex, rule.constraints==null ? 0 : rule.constraints.length);
        if(rule.constraints!=null && rule.constraints.length>0){
            for(int i=0; i<rule.constraints.length; i++){
                printf("%s\n", rule.constraints[i]);
            }
        }
            
        size++;
        return null;
    }

    @Override
    public File[] FilesRgx(String name, String description, RgxRule... rules) throws Exception {
        if(rules==null || rules.length<1){
            throw new Exception("rules==null || rules.length<1");
        }
        printf("<type>\n"
                + "FilesRgx\n"
                + "<name>\n"
                + "%s\n"
                + "<description>\n"
                + "%s\n"
                + "<--------------rules---------------->\n"
                + "%d\n", name, description, rules.length);
        for(RgxRule r : rules){
            printf("%s\n", "<regex>");
            printf("%s\n", r.regex);
            printf("%d\n", r.constraints.length);
            for(int i=0; i<r.constraints.length; i++){
                printf("%s\n", r.constraints[i]);
            }
        }
        size++;
        return null;
    }    
    
}

