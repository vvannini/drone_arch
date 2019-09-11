/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.runner;

import ProOF.com.Communication;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameterAbstract;
import ProOF.com.RgxRule;
import ProOF.com.language.Factory;
import ProOF.com.language.Approach;
import ProOF.com.language.Run;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class Runner {
    private final Fact sets[];
    private final Serv servs[];
    private final Run exec;
    private final No nodes[];
    
    public static boolean PRINT = false;
    public static boolean LOCAL = false;
    
    public Runner(File file, File input_dir, Factory<Run> obj) throws FileNotFoundException, Exception {
        Approach.job = file;
        Scanner input = new Scanner(file);
        if(PRINT){
            System.out.println(input.nextLine());
        }else{
            input.nextLine();
        }
            
        int n_facts = Integer.parseInt(input.nextLine());
        sets = new Fact[n_facts];
        for(int f = 0; f<n_facts; f++){
            sets[f] = new Fact(input);
        }
        if(sets[0].chooses.length!=1){
            throw new Exception(String.format("Factory '%s' chooses = %d != 1", sets[0].name, sets[0].chooses.length));
        }
        if(PRINT){
            System.out.println(input.nextLine());
        }else{
            input.nextLine();
        }
        int n_servs = Integer.parseInt(input.nextLine());
        servs = new Serv[n_servs];
        for(int s = 0; s<n_servs; s++){
            servs[s] = new Serv(input);
        }
        input.close();
        
        if(PRINT){
            System.out.println("-------------[factories]--------------");
            for(int f = 0; f<n_facts; f++){
                System.out.println(sets[f].name);
            }
            System.out.println("-------------[services]--------------");
            for(int s = 0; s<n_servs; s++){
                System.out.println(servs[s].name);
            }
        }
        
        
        if(PRINT)System.out.println("-------------[exec node]--------------");
        exec = obj.build_runner(sets[0].chooses[0]);
        if(PRINT)System.out.println(exec);
        
        
        WinServices winA = new WinServices(exec);
        
        if(PRINT)System.out.println("-------------[link factorys]--------------");
        final LinkedList<No> all_list = new LinkedList<No>();
        
        while(winA.n_servs()>0){
            Approach serv = winA.nextServ();
            No node = new No(serv);
            
            if(PRINT)System.out.printf("%s\n",serv.name());
            serv.services(winA);
            
            while(winA.n_facts()>0){
                Factory fact = winA.nextFact();
                Fact[] factorys = find(sets, fact);
                if(factorys==null || factorys.length==0){
                    if(PRINT)System.out.printf("\t- : %s\n", fact.name());
                    //throw new Exception(String.format("Not find factory '%s'", fact.name()));
                }else{
                    if(PRINT)System.out.printf("\t%d : %s\n", factorys.length, fact.name());
                    for(Fact f : factorys){
                        for(int c : f.chooses){
                            Approach s = fact.build(c);
                            if(s==null){
                                throw new Exception(String.format("Factory '%s' NewService(%d)==null", fact.name(), c));
                            }
                            winA.add(s);
                            node.add(s);
                        }
                    }
                }
            }
            all_list.add(node);
        }
        nodes = all_list.toArray(new No[all_list.size()]);
        
        if(PRINT){
            System.out.println("-------------[after link]--------------");
            for(No no : nodes){
                System.out.printf("%s\n",no.serv.name());
                for(Approach s : no.facts){
                    System.out.printf("\t%s\n",s.name());
                }
            }
        }
        
        if(PRINT)System.out.println("-------------[read params]--------------");
        WinParams winc = new WinParams(input_dir);
        for(Serv s : servs){
            No no = find(nodes, s);
            if(no==null){
                throw new Exception(String.format("Not find service '%s', class-name '%s'", s.name, s.class_name));
            }
            //no.set(s.params);
            winc.start(s.params);
            no.serv.parameters(winc);
        }
        
        if(PRINT)System.out.println("-------------[link needs]--------------");
        WinNeeds winb = new WinNeeds(nodes);
        for(No no : nodes){
            winb.start(no);
            no.serv.services(winb);
            
            if(PRINT){
                System.out.printf("%s\n",no.serv.name());
                if(no.facts.size()>0){
                    System.out.printf("\t%s\n", "factorys");
                    for(Approach s : no.facts){
                        System.out.printf("\t\t%s\n",s.name());
                    }
                }
                if(no.needs.size()>0){
                    System.out.printf("\t%s\n", "needs");
                    for(LinkedList<Approach> list : no.needs){
                        System.out.printf("\t\t%s\n",list);
                    }
                }
            }
        }
    }
    
    
    public void run() throws FileNotFoundException, Exception{
        if(PRINT)System.out.println("-------------[loading]--------------");
        for(No no : nodes){
            no.serv.load();
        }
        if(PRINT)System.out.println("-------------[starting]--------------");
        for(No no : nodes){
            no.serv.start();
        }
        
        if(PRINT)System.out.println("-------------[executing]--------------");
        try{
            exec.execute();
        }catch(ExceptionForceFinish ex){
            if(PRINT)System.out.println("-------------[ForceFinish enabled]--------------");
            if(PRINT)ex.printStackTrace(System.out);
        }catch(Throwable ex){
            System.err.println("-------------[Throwable]--------------");
            ex.printStackTrace(System.err);
        }
        
        if(PRINT)System.out.println("-------------[finish]--------------");
        for(No no : nodes){
            try{
                no.serv.finish();
            }catch(Throwable ex){
                System.err.println("-------------[Throwable]--------------");
                ex.printStackTrace(System.err);
            }
        }
        if(PRINT)System.out.println("-------------[results]--------------");
        for(No no : nodes){
            try{
                no.serv.results(Communication.restuls());
            }catch(Throwable ex){
                System.err.println("-------------[Throwable]--------------");
                ex.printStackTrace(System.err);
            }
        }
        Communication.restuls().close();
    }
    
    

    private static class Fact{
        private final String name;
        private final String class_name;
        private final int chooses[];

        public Fact(Scanner input) {
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            int id = Integer.parseInt(input.nextLine());
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            int type = Integer.parseInt(input.nextLine());
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            name = input.nextLine();
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            class_name = input.nextLine();
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            chooses = split(input.nextLine());
        }
        private final int[] split(String line){
            String v[] = line.split(",");
            int values[] = new int[v.length];
            for(int i=0; i<v.length; i++){
                values[i] = Integer.parseInt(v[i]);
            }
            return values;
        }
    }
    private static class Serv{
        private final String name;
        private final String class_name;
        private final String params[];

        public Serv(Scanner input) {
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            int id = Integer.parseInt(input.nextLine());
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            int type = Integer.parseInt(input.nextLine());
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            name = input.nextLine();
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            class_name = input.nextLine();
            if(PRINT) System.out.println(input.nextLine()); else input.nextLine();
            int n_param = Integer.parseInt(input.nextLine());
            params = new String[n_param];
            for(int i=0; i<n_param; i++){
                params[i] = input.nextLine();
            }
        }
    }
    private static class No{
        private final Approach serv;
        private final LinkedList<Approach> facts = new LinkedList<Approach>();
        private final LinkedList<LinkedList<Approach>> needs = new LinkedList<LinkedList<Approach>>();
        private String[] params;
        public No(Approach serv) {
            this.serv = serv;
        }
        public void add(Approach serv){
            this.facts.addLast(serv);
        }
        public void add(LinkedList<Approach> list){
            this.needs.addLast(list);
        }
        public void set(String params[]){
            this.params = params;
        }
    }
    public static Fact[] find(Fact sets[], Factory fact){
        //if(PRINT)System.out.println("fact = "+fact);
        
        LinkedList<Fact> list = new LinkedList<Fact>();
        for(Fact f : sets){
            if(f.name.equals(fact.name()) && f.class_name.equals(fact.class_name())){
                list.addLast(f);
            }
        }
        return list.toArray(new Fact[list.size()]);
    }
    public static No find(No all_nodes[], Serv serv){
        for(No no : all_nodes){
            if(no.serv.name().equals(serv.name) && no.serv.class_name().equals(serv.class_name)){
                return no;
            }
        }
        return null;
    }
    private static class FactoryNode{ 
        private final Factory fact;
        private final Class type;

        public FactoryNode(Factory fact, Class type) {
            this.fact = fact;
            this.type = type;
        }
        @Override
        public boolean equals(Object obj) {
            FactoryNode f = ((FactoryNode)obj);
            if(type==null){
                return fact.equals(f.fact) && f.type==null;
            }else{
                return fact.equals(f.fact) && type.equals(f.type);
            }
        }
    }
    private static class WinServices implements LinkerApproaches{
        private final LinkedList<FactoryNode> factorys = new LinkedList<FactoryNode>();
        private final LinkedList<Approach> services = new LinkedList<Approach>();
        
        public WinServices(Run exec) {
            services.addLast(exec);
        }
        public int n_servs(){
            return services.size();
        }
        public int n_facts(){
            return factorys.size();
        }
        public Approach nextServ(){
            return services.removeFirst();
        }
        public Factory nextFact(){
            return factorys.removeFirst().fact;
        }
        
        @Override
        public <T extends Approach> T add(T service) throws Exception {
            if(service!=null){
                if(PRINT) {
                    System.out.printf("[%h = %s]\n", service, service.name());
                }
                services.addLast(service);
            }
            return service;
        }
        
        public void add(Factory factory, Class type) throws Exception {
            if(factory!=null){
                if(PRINT) {
                    System.out.printf("[%s = %s]\n", type==null?"null":type.getSimpleName(), factory.name());
                }
                factorys.addLast(new FactoryNode(factory, type));
            }
        }
        @Override
        public void add(Factory factory) throws Exception {
            add(factory, null);
        }
        @Override
        public Approach get(Factory factory) throws Exception {
            add(factory, null);
            return null;
        }
        @Override
        public <T extends Approach> T get(Factory factory, T a) throws Exception {
            add(factory, null);
            return null;
        }
        @Override
        public Approach get(Factory factory, Class type) throws Exception {
            add(factory, type);
            return null;
        }
        @Override
        public <T extends Approach> T get(Factory factory, Class type, T a) throws Exception {
            add(factory, type);
            return null;
        }
        
        @Override
        public Approach[] gets(Factory factory) throws Exception {
            add(factory, null);
            return null;
        }
        @Override
        public Approach need(Class type) throws Exception {
            return null;
        }
        @Override
        public <T extends Approach> T need(Class type, T b) throws Exception {
            return null;
        }
        @Override
        public Approach[] needs(Class type) throws Exception {
            return null;
        }
        @Override
        public <T extends Approach> T[] needs(Class type, T[] a) throws Exception {
            return null;
        }
        @Override
        public Approach wish(Class type) throws Exception {
            return null;
        }
        @Override
        public <T extends Approach> T wish(Class type, T b) throws Exception {
            return null;
        }
        @Override
        public Approach[] wishes(Class type) throws Exception {
            return null;
        }
        @Override
        public <T extends Approach> T[] wishes(Class type, T[] a) throws Exception {
            return null;
        }
    }
    
    private static class WinNeeds implements LinkerApproaches{
        private final No all_nodes[];
        private No activate = null;
        private int get = 0;
        
        public WinNeeds(No[] all_nodes) {
            this.all_nodes = all_nodes;
        }

        public void start(No activate){
            this.activate = activate;
            this.get = 0;
        }
        
        @Override
        public <T extends Approach> T add(T service) throws Exception {
            return service;
        }
        @Override
        public void add(Factory factory) throws Exception {
            get++;
        }
        @Override
        public Approach get(Factory factory) throws Exception {
            return activate.facts.get(get++);
        }
        @Override
        public <T extends Approach> T get(Factory factory, T a) throws Exception {
            //System.out.println(activate.facts.get(get));
            return (T) activate.facts.get(get++);
        }
        @Override
        public Approach get(Factory factory, Class type) throws Exception {
            return activate.facts.get(get++);
        }
        @Override
        public <T extends Approach> T get(Factory factory, Class type, T a) throws Exception {
            return (T) activate.facts.get(get++);
        }
        
        @Override
        public Approach[] gets(Factory factory) throws Exception {
            return null;
        }
        @Override
        public Approach need(Class type) throws Exception {
            LinkedList<Approach> list = new LinkedList<Approach>();
            for(No no : all_nodes){
                if(type.isInstance(no.serv)){
                    list.addLast(no.serv);
                }
            }
            if(list.size()==0){
                throw new Exception("Conflict: there is no component "+type.getSimpleName()+" selected for this task. ");
            }else if(list.size()>1){
                throw new Exception("Conflict: there is more than one component "+type.getSimpleName()+" selected for this task.");
            }
            activate.add(list);
            return list.getFirst();
        }
        @Override
        public <T extends Approach> T need(Class type, T b) throws Exception {
            LinkedList<Approach> list = new LinkedList<Approach>();
            for(No no : all_nodes){
                if(type.isInstance(no.serv)){
                    list.addLast(no.serv);
                }
            }
            if(list.size()==0){
                throw new Exception("Conflict: there is no component "+type.getSimpleName()+" selected for this task. ");
            }else if(list.size()>1){
                throw new Exception("Conflict: there is more than one component "+type.getSimpleName()+" selected for this task.");
            }
            activate.add(list);
            return (T)list.getFirst();
        }
        @Override
        public Approach[] needs(Class type) throws Exception {
            LinkedList<Approach> list = new LinkedList<Approach>();
            for(No no : all_nodes){
                if(type.isInstance(no.serv)){
                    list.addLast(no.serv);
                }
            }
            if(list.size()<=0){
                throw new Exception("Conflict: there is no component "+type.getSimpleName()+" selected for this task. ");
            }
            activate.add(list);
            
            return list.toArray(new Approach[list.size()]);
        }
        @Override
        public <T extends Approach> T[] needs(Class type, T[] b) throws Exception {
            LinkedList<Approach> list = new LinkedList<Approach>();
            for(No no : all_nodes){
                if(type.isInstance(no.serv)){
                    list.addLast(no.serv);
                }
            }
            if(list.size()<=0){
                throw new Exception("Conflict: there is no component "+type.getSimpleName()+" selected for this task. ");
            }
            activate.add(list);
            
            T[] a = (T[])java.lang.reflect.Array.newInstance(b.getClass().getComponentType(), list.size());
        
            int i=0;
            for(Approach s : list){
                a[i++] = (T) s;
            }
            if (a.length > list.size())
                a[list.size()] = null;
            return a;
        }
        
        @Override
        public Approach wish(Class type) throws Exception {
            LinkedList<Approach> list = new LinkedList<Approach>();
            for(No no : all_nodes){
                if(type.isInstance(no.serv)){
                    list.addLast(no.serv);
                }
            }
            if(list.size()==0){
                activate.add(list);
                return null;
            }else if(list.size()>1){
                throw new Exception("Conflict: there is more than one component "+type.getSimpleName()+" selected for this task.");
            }else{
                activate.add(list);
                return list.getFirst();
            }
        }
        @Override
        public <T extends Approach> T wish(Class type, T b) throws Exception {
            LinkedList<Approach> list = new LinkedList<Approach>();
            for(No no : all_nodes){
                if(type.isInstance(no.serv)){
                    list.addLast(no.serv);
                }
            }
            if(list.size()==0){
                activate.add(list);
                return null;
            }else if(list.size()>1){
                throw new Exception("Conflict: there is more than one component "+type.getSimpleName()+" selected for this task.");
            }else{
                activate.add(list);
                return (T)list.getFirst();
            }
        }
        @Override
        public Approach[] wishes(Class type) throws Exception {
            LinkedList<Approach> list = new LinkedList<Approach>();
            for(No no : all_nodes){
                if(type.isInstance(no.serv)){
                    list.addLast(no.serv);
                }
            }
            if(list.size()<=0){
                activate.add(list);
                return null;
            }else{
                activate.add(list);
                return list.toArray(new Approach[list.size()]);
            }
        }
        @Override
        public <T extends Approach> T[] wishes(Class type, T[] b) throws Exception {
            LinkedList<Approach> list = new LinkedList<Approach>();
            for(No no : all_nodes){
                if(type.isInstance(no.serv)){
                    list.addLast(no.serv);
                }
            }
            if(list.size()<=0){
                activate.add(list);
                return null;
            }else{
                activate.add(list);

                T[] a = (T[])java.lang.reflect.Array.newInstance(b.getClass().getComponentType(), list.size());

                int i=0;
                for(Approach s : list){
                    a[i++] = (T) s;
                }
                if (a.length > list.size())
                    a[list.size()] = null;
                return a;
            }
        }
    };
    private static class WinParams extends LinkerParameterAbstract{
        private String params[] = null;
        private int get = 0;
        private final File input_dir;

        public WinParams(File input_dir) {
            this.input_dir = input_dir;
        }
        
        public void start(String params[]){
            this.params = params;
            this.get = 0;
        }
        
        @Override
        public int Int(String name, int init, int min, int max, String description) throws Exception {
            return Integer.parseInt(params[get++]);
        }
        @Override
        public int Itens(String name, int init, String itens[], String description) throws Exception {
            int index = Integer.parseInt(params[get++]);
            if(index<0 || index>= itens.length){
                throw new Exception("index = "+index+" out of bound [ 0 , "+(itens.length-1)+" ] "); 
            }
            return index;
        }
        @Override
        public long Long(String name, long init, long min, long max, String description) throws Exception {
            return Long.parseLong(params[get++]);
        }
        @Override
        public double Dbl(String name, double init, double min, double max, String description) throws Exception {
            return Double.parseDouble(params[get++]);
        }
        @Override
        public String String(String name, String init, String description) throws Exception {
            return params[get++];
        }
        @Override
        public String Regex(String name, String init, String regex, String description) throws Exception {
            return params[get++];
        }
        @Override
        public InputStream InputStream(String name, String description) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        @Override
        public File FileRgx(String name, String description, RgxRule rule) throws Exception {
            return new File(input_dir, params[get++]);
        }
        @Override
        public File[] FilesRgx(String name, String description, RgxRule... rules) throws Exception {
            String v[] = params[get++].split(";");
            File[] files = new File[v.length];
            for(int i=0; i<files.length; i++){
                files[i] = new File(input_dir, v[i]);
            }
            return files;
        }
    }
}
