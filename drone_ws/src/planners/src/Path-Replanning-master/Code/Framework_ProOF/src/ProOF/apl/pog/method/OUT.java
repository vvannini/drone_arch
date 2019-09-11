
package ProOF.apl.pog.method;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import java.io.File;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Hossomi
 */
public class OUT extends Run {
    private File file;
    
    @Override
    public String name() {
        return "OUT";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void services(LinkerApproaches com) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters com) throws Exception {
        file    = com.File("Files for OUT",null,"out");
    }
    @Override
    public boolean validation(LinkerValidations com) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void load() throws Exception {
        
    }

    @Override
    public void start() throws Exception {
        
    }

    @Override
    public void results(LinkerResults com) throws Exception {
        
    }
    
    @Override
    public void execute() throws Exception {
       System.out.println("file.name = '"+file.getName()+"'");
        Scanner input = new Scanner(file);
        for(int i=0; i<24; i++){
            input.nextLine();
        }
        String name = input.nextLine();
        input.close();
        System.out.println("line = '"+name+"'");
        name = name.split(";")[1];
        System.out.println("name = '"+name+"'");
        name = name.replaceAll(".out", "");
        System.out.println("val = '"+name+"'");
        
        System.out.println("file.name = '"+file.getName()+"'");
        Scanner job = new Scanner(new File("D:/fFramework/ProOF/ProOFClient_vA/jobs/", name));
        for(int i=0; i<38; i++){
            job.nextLine();
        }
        name = job.nextLine();
        job.close();
        
        System.out.println("line = '"+name+"'");
        name = name.split(";")[1];
        System.out.println("name = '"+name+"'");
        String val = name.split("_")[0];
        System.out.println("val = '"+val+"'");
        
        System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s\n", "results", "str", "instance", val);
        
        File dir = new File("D:/fFramework/Instances/Instancia_GCI_longoprazo/GCIJul2008_13/");
        String group = null;
        for(File d : dir.listFiles()){
            for(File f : new File(d,"Opl/").listFiles()){
                if(f.getName().equals(name)){
                    if(group!=null){
                        throw new Exception("File '"+name+"' is in two groups '"+group+"' and '"+d.getName()+"'");
                    }else{
                        group = d.getName();
                    }
                }
            }
        }
        System.out.printf(Locale.ENGLISH,"#%s$%s$%s$%s\n", "results", "str", "group", group);
        
        Scanner sc = new Scanner(file);
        while(sc.hasNextLine()){
            System.out.println(sc.nextLine());
        }
        sc.close();
        System.out.printf("#%s$%s\n", "close", "results");
    }
}
