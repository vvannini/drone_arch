/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.notZyt;

import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.gen.operator.oMutation;
import ProOF.apl.pog.problem.GCILT.GCILTInstance;
import ProOF.apl.pog.problem.GCILT.GCILTLot;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.codification.Operator;
import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author Hossomi
 */
public class GCILTOperators extends Factory<Operator> {

    public static GCILTOperators obj = new GCILTOperators();

    @Override
    public String name() {
        return "GCILT Operators";
    }

    @Override
    public Operator build(int index) {
        switch (index) {
            case 0: return new opInitializer();
            case 1: return new opCrossoverMacroOX();
	    case 2: return new opCrossoverMacro1PT();
	    case 3: return new opCrossoverMacro1PY();
	    case 4: return new opCrossoverMicroOX();
	    case 5: return new opCrossoverMicro1P();
            case 6: return new opMutationColorChange();
	    case 7: return new opMutationLotSwap();
	    case 8: return new opMutationTimeSwap();
	    case 9: return new opMutationRemoveLot();
	    case 10: return new opMutationAddLot();
	    case 11: return new opMutationTimeTransfer();
            case 12: return new opMutationDaysChange();
            case 13: return new opLocalMoveColorChange();
	    case 14: return new opLocalMoveLotSwap();
	    case 15: return new opLocalMoveTimeSwap();
	    case 16: return new opLocalMoveRemoveLot();
	    case 17: return new opLocalMoveAddLot();
	    case 18: return new opLocalMoveTimeTransfer();
            case 19: return new opLocalMoveDaysChange();
        }
        return null;
    }

    private static class opInitializer extends oInitialization <GCILTNProblem, GCILTCodification> {
        @Override
        public String name() {
            return "Initializer";
        }
        @Override
        public void initialize(GCILTNProblem problem, GCILTCodification ind) throws Exception {
            GCILTInstance inst = problem.inst;
	    final int Y = inst.Y;
	    final int L = inst.L;
	    final int T = inst.T;
            
            for (int t = 0; t < T; t++) {
		for (int y = 0; y < Y; y++) {
                    ind.Syt[y][t].clear();
                }
            }
            
            for (int y = 0; y < Y; y++){
                ind.Zy[y] = 0;
            }
            
            int LASTy[] = new int[Y];
            for (int y = 0; y < Y; y++) {
                LASTy[y] = problem.inst.ovenStartColor(y);
            }
            
            double Dl[] = new double[L];
            double Rl[] = new double[L];
            System.arraycopy(problem.inst.Dl, 0, Dl, 0, L);
            
            boolean STOPy[] = new boolean[Y];
            for (int t = 0; t < T; t++) {
                for (int y = 0; y < Y && !STOPy[y]; y++) {
                    int remDays = inst.Qt[t];
                    while(remDays>0 && !STOPy[y]){
                        int l = LASTy[y];
                        double sum = 0;
                        for(int w = 0; w<L; w++){
                            if(problem.inst.Ly[y].contains(w)){
                                Rl[w] = Math.min(Dl[w], problem.inst.STluy[l][w][y]*problem.inst.Cly[w][y]);
                            }else{
                                Rl[w] = Dl[w];
                            }
                            Dl[w] -= Rl[w];
                            sum += Dl[w];
                        }
                        if(sum<problem.inst.Cy[y]){
                            STOPy[y] = true;
                        }else{
                            int u = problem.rnd.roulette_wheel(Dl);

                            if (inst.fullSetup(l, u, y) < remDays) {
                                int time = problem.rnd.nextInt( inst.fullSetup(l, u, y) + 1, remDays);
                                time = Math.min(time, inst.fullSetup(l, u, y) + 1 + (int)((Dl[u]+Rl[u])/problem.inst.Cly[u][y]));

                                double q = (time - inst.STluy[l][u][y])*inst.Cly[u][y];
                                if(Dl[u]+Rl[u]>q){
                                    Dl[u] -= q;
                                }else{
                                    Rl[u] = 0;
                                    Dl[u] = 0;
                                }
                                
                                /*int index = ind.Syt[y][t].indexOf(u);
                                if(index != -1){
                                    GCILTLot lot = ind.Syt[y][t].get(index);
                                    ind.Syt[y][t].set( index, new GCILTLot(lot.color, lot.days + time));
                                }else{
                                    ind.Syt[y][t].add(new GCILTLot( u, time ));
                                    LASTy[y] = u;
                                }*/
                                
                                ind.Syt[y][t].add(new GCILTLot( u, time ));
                                LASTy[y] = u;
                                
                                remDays -= time;
                                ind.Zy[y]+= time;
                            }else{
                                double q = remDays*inst.Cly[l][y];
                                if(Dl[l]+Rl[l]>q){
                                    Dl[l] -= q;
                                }else{
                                    Rl[l] = 0;
                                    Dl[l] = 0;
                                }
                                
                                GCILTLot lot = ind.Syt[y][t].getLast();
                                ind.Syt[y][t].set( ind.Syt[y][t].size()-1, new GCILTLot(lot.color, lot.days + remDays));
                                ind.Zy[y]+= remDays;
                                remDays = 0;
                            }
                        }
                        for(int w = 0; w<L; w++){
                            Dl[w] += Rl[w];
                        }
                    }
                }
            }
            
            for (int y = 0; y < Y; y++) {
                int l = LASTy[y];
                for (int t = 0; t < T; t++) {
                    int days = 0;
                    for(GCILTLot lot : ind.Syt[y][t]){
                        days += lot.days;
                    }
                    if(days>inst.Qt[t]){
                        throw new Exception("days ("+days+") > inst.Qt[t] ("+inst.Qt[t]+")");
                    }else if(days<inst.Qt[t]){
                        int time = inst.Qt[t]-days;
                        while(time>5){
                            ind.Syt[y][t].addLast(new GCILTLot(l, 5));
                            time -= 5;
                        }
                        ind.Syt[y][t].addLast(new GCILTLot(l, time));
                    }
                }
            }
            
            /*
            System.out.println("---------------------------");
            ind.print(problem);
            System.out.println("---------------------------");
            */ 
        }
    }
    
   
    
    private static class opCrossoverMacroOX extends oCrossover <GCILTNProblem, GCILTCodification> {
        @Override
        public GCILTCodification crossover(GCILTNProblem problem, GCILTCodification ind1, GCILTCodification ind2) throws Exception {
            GCILTCodification child = ind1.build(problem);
	    
	    for (int y = 0; y < problem.inst.Y; y++) {
		for (int t = 0; t < problem.inst.T; t++) {
		    // Select from ind2
		    if (problem.rnd.nextBoolean()) {
			child.Syt[y][t] = (LinkedList<GCILTLot>) ind1.Syt[y][t].clone();
		    }else{
                        child.Syt[y][t] = (LinkedList<GCILTLot>) ind2.Syt[y][t].clone();
                    }
		}
	    }
	    for(int y=0; y<problem.inst.Y; y++){
                child.Zy[y] = problem.rnd.nextInt(ind1.Zy[y], ind2.Zy[y]);
            }
	    repairAllOvens(problem, child);
            return child;
        }

        @Override
        public String name() {
            return "Cr. Macro OX";
        } 
    }
    
    private static class opCrossoverMacro1PT extends oCrossover <GCILTNProblem, GCILTCodification> {

        @Override
        public GCILTCodification crossover(GCILTNProblem problem, GCILTCodification ind1, GCILTCodification ind2) throws Exception {
            GCILTCodification child = ind1.clone(problem);
	    int tc;
	    
	    for (int y = 0; y < problem.inst.Y; y++) {
		tc = problem.rnd.nextInt(1, problem.inst.T - 1 );
		for (int t = tc; t < problem.inst.T; t++){
		    child.Syt[y][t] = (LinkedList<GCILTLot>) ind2.Syt[y][t].clone();
                }
		repairOvenLots(problem, child, y, tc, 0);
	    }
            for(int y=0; y<problem.inst.Y; y++){
                child.Zy[y] = problem.rnd.nextInt(ind1.Zy[y], ind2.Zy[y]);
            }
	    
            return child;
        }

        @Override
        public String name() {
            return "Cr. Macro 1PT";
        } 
    }
    
    private static class opCrossoverMacro1PY extends oCrossover <GCILTNProblem, GCILTCodification> {

        @Override
        public GCILTCodification crossover(GCILTNProblem problem, GCILTCodification ind1, GCILTCodification ind2) throws Exception {
            GCILTCodification child = ind1.clone(problem);
	    int yc;
	    
	    for (int t = 0; t < problem.inst.T; t++) {
		yc = problem.rnd.nextInt(1, problem.inst.Y - 1 );
		for (int y = yc; y < problem.inst.Y; y++){
		    child.Syt[y][t] = (LinkedList<GCILTLot>) ind2.Syt[y][t].clone();
                }
	    }
            for(int y=0; y<problem.inst.Y; y++){
                child.Zy[y] = problem.rnd.nextInt(ind1.Zy[y], ind2.Zy[y]);
            }
	    
	    repairAllOvens(problem, child);
            return child;
        }

        @Override
        public String name() {
            return "Cr. Macro 1PY";
        } 
    }
    
    private static class opCrossoverMicroOX extends oCrossover <GCILTNProblem, GCILTCodification> {

        @Override
        public GCILTCodification crossover(GCILTNProblem problem, GCILTCodification ind1, GCILTCodification ind2) throws Exception {
            GCILTCodification child = (GCILTCodification) problem.build_codif();
	    LinkedList<GCILTLot> list1, list2;
	    int max;
	    
	    for (int y = 0; y < problem.inst.Y; y++) {
		for (int t = 0; t < problem.inst.T; t++) {
		    list1 = ind1.Syt[y][t];
		    list2 = ind2.Syt[y][t];
		    max = (list1.size() > list2.size()) ? (list1.size()) : (list2.size());
		    
		    for (int i = 0; i < max; i++) {
			// From ind1
			if (problem.rnd.nextBoolean()) {
			    if (i < list1.size())
				child.Syt[y][t].add( list1.get(i).getCopy() );
			}
			
			// From ind2
			else {
			    if (i < list2.size())
				child.Syt[y][t].add( list2.get(i).getCopy() );
			}
		    }
		    
		    normalize(problem, child, y, t);
		}
	    }
            for(int y=0; y<problem.inst.Y; y++){
                child.Zy[y] = problem.rnd.nextInt(ind1.Zy[y], ind2.Zy[y]);
            }

	    repairAllOvens(problem, child);
            return child;
        }

        @Override
        public String name() {
            return "Cr. Micro OX";
        } 
    }
    
    private static class opCrossoverMicro1P extends oCrossover <GCILTNProblem, GCILTCodification> {

        @Override
        public GCILTCodification crossover(GCILTNProblem problem, GCILTCodification ind1, GCILTCodification ind2) throws Exception {
            GCILTCodification child = (GCILTCodification) problem.build_codif();
	    LinkedList<GCILTLot> listMin, listMax;
	    int cut;
	    
	    for (int y = 0; y < problem.inst.Y; y++) {
		for (int t = 0; t < problem.inst.T; t++) {
		    if (ind1.Syt[y][t].size() > ind2.Syt[y][t].size()) {
			listMax = ind1.Syt[y][t];
			listMin = ind2.Syt[y][t];
		    }
		    else {
			listMax = ind2.Syt[y][t];
			listMin = ind1.Syt[y][t];
		    }
		    
		    cut = problem.rnd.nextInt( 0, listMin.size() - 1 );
		    
		    for (int i = 0; i < listMax.size(); i++) {
			// Get from min
			if (i < cut)
			    child.Syt[y][t].add( listMin.get(i).getCopy() );
			
			// Get from max
			else
			    child.Syt[y][t].add( listMax.get(i).getCopy() );
		    }
		    
		    normalize(problem, child, y, t);
		}
	    }
	    for(int y=0; y<problem.inst.Y; y++){
                child.Zy[y] = problem.rnd.nextInt(ind1.Zy[y], ind2.Zy[y]);
            }
            
	    repairAllOvens(problem, child);
            return child;
        }

        @Override
        public String name() {
            return "Cr. Micro 1P";
        } 
    }
    
    private class opMutationColorChange extends oMutation<GCILTNProblem, GCILTCodification> {

        @Override
        public void mutation(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationColorChange( mem, ind );
        }

        @Override
        public String name() {
            return "Mut. Color Change";
        }
        
    }
    
    private class opLocalMoveColorChange extends oLocalMove<GCILTNProblem, GCILTCodification> {

        @Override
        public void local_search(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationColorChange( mem, ind );
        }

        @Override
        public String name() {
            return "LM. Color Change";
        }
        
    }
    
    private class opMutationLotSwap extends oMutation<GCILTNProblem, GCILTCodification> {

        @Override
        public void mutation(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationLotSwap( mem, ind );
        }

        @Override
        public String name() {
            return "Mut. Lot Swap";
        }
    }
    
    private class opLocalMoveLotSwap extends oLocalMove<GCILTNProblem, GCILTCodification> {

        @Override
        public void local_search(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationLotSwap( mem, ind );
        }

        @Override
        public String name() {
            return "LM. Lot Swap";
        }
    }
    
    private class opMutationTimeSwap extends oMutation<GCILTNProblem, GCILTCodification> {

        @Override
        public void mutation(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationTimeSwap( mem, ind );
        }

        @Override
        public String name() {
            return "Mut. Time Swap";
        }
    }
    
    private class opLocalMoveTimeSwap extends oLocalMove<GCILTNProblem, GCILTCodification> {

        @Override
        public void local_search(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationTimeSwap( mem, ind );
        }

        @Override
        public String name() {
            return "LM. Time Swap";
        }
    }
    
    private class opMutationRemoveLot extends oMutation<GCILTNProblem, GCILTCodification> {

        @Override
        public void mutation(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationRemoveLot( mem, ind );
        }

        @Override
        public String name() {
            return "Mut. Lot Remove";
        }
    }
    
    private class opLocalMoveRemoveLot extends oLocalMove<GCILTNProblem, GCILTCodification> {

        @Override
        public void local_search(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationRemoveLot( mem, ind );
        }

        @Override
        public String name() {
            return "LM. Lot Remove";
        }
    }
    
    private class opMutationAddLot extends oMutation<GCILTNProblem, GCILTCodification> {

        @Override
        public void mutation(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationAddLot( mem, ind );
        }

        @Override
        public String name() {
            return "Mut. Lot Add";
        }
    }
    
    private class opLocalMoveAddLot extends oLocalMove<GCILTNProblem, GCILTCodification> {

        @Override
        public void local_search(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationAddLot( mem, ind );
        }

        @Override
        public String name() {
            return "LM. Lot Add";
        }
    }
    
    private class opMutationTimeTransfer extends oMutation<GCILTNProblem, GCILTCodification> {

        @Override
        public void mutation(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationTimeTransfer( mem, ind );
        }

        @Override
        public String name() {
            return "Mut. Time Transfer";
        }
    }
    
    private class opLocalMoveTimeTransfer extends oLocalMove<GCILTNProblem, GCILTCodification> {

        @Override
        public void local_search(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationTimeTransfer( mem, ind );
        }

        @Override
        public String name() {
            return "LM. Time Transfer";
        }
    }
    private class opMutationDaysChange extends oMutation<GCILTNProblem, GCILTCodification> {
        @Override
        public void mutation(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationDaysChange( mem, ind );
        }
        @Override
        public String name() {
            return "Mut. Days change";
        }
    }
    private class opLocalMoveDaysChange extends oLocalMove<GCILTNProblem, GCILTCodification> {
        @Override
        public void local_search(GCILTNProblem mem, GCILTCodification ind) throws Exception {
            mutationDaysChange( mem, ind );
        }
        @Override
        public String name() {
            return "LM. Days change";
        }
    }
    private static void mutationDaysChange( GCILTNProblem problem, GCILTCodification ind ) throws Exception {
	int y = problem.rnd.nextInt(problem.inst.Y);
        if(ind.Zy[y]>0 && problem.rnd.nextBoolean()){
            ind.Zy[y]--;
        }else if(ind.Zy[y]<problem.inst.Days){
            ind.Zy[y]++;
        }
    }
    private static void mutationColorChange( GCILTNProblem problem, GCILTCodification ind ) throws Exception {
	GCILTLot lot;
        int y, t, i, l;
	
	y = problem.rnd.nextInt( 0, problem.inst.Y - 1 );
	t = problem.rnd.nextInt( 0, problem.inst.T - 1 );
	i = problem.rnd.nextInt( 0, ind.Syt[y][t].size() - 1);
	l = randomColor( problem, y );
	lot = ind.Syt[y][t].get(i);
	
	ind.Syt[y][t].set(i, new GCILTLot( l, lot.days ));
	repairOvenLots( problem, ind, y, t, i );
    }
    public static int randomColor( GCILTNProblem problem, int y ) {
	int l;
	do {
	    l = problem.rnd.nextInt(0, problem.inst.L - 1);
	} while (!problem.inst.Ly[y].contains(l));
	
	return l;
    }
    
    private static void mutationLotSwap( GCILTNProblem problem, GCILTCodification ind ) throws Exception {
	GCILTLot lota, lotb;
	LinkedList<GCILTLot> list;
        int y, t, i, j;
	
	y = problem.rnd.nextInt( 0, problem.inst.Y - 1 );
	t = problem.rnd.nextInt( 0, problem.inst.T - 1 );
	if (ind.Syt[y][t].size() > 1) {
	    list = ind.Syt[y][t];
	    i = problem.rnd.nextInt( 0, list.size() - 1);
	    do {
		j = problem.rnd.nextInt( 0, list.size() - 1);
	    } while (i == j);
	    
	    lota = list.get(i);
	    lotb = list.get(j);
	    
	    list.set(i, lotb);
	    list.set(j, lota);
	    
	    repairOvenLots( problem, ind, y, t, (i < j)?(i):(j) );
	}
    }
    
    private static void mutationTimeSwap( GCILTNProblem problem, GCILTCodification ind ) throws Exception {
	GCILTLot lota, lotb;
	LinkedList<GCILTLot> list;
        int y, t, i, j;
	
	y = problem.rnd.nextInt( 0, problem.inst.Y - 1 );
	t = problem.rnd.nextInt( 0, problem.inst.T - 1 );
	if (ind.Syt[y][t].size() > 1) {
	    list = ind.Syt[y][t];
	    i = problem.rnd.nextInt( 0, list.size() - 1);
	    do {
		j = problem.rnd.nextInt( 0, list.size() - 1);
	    } while (i == j);
	    
	    lota = list.get(i);
	    lotb = list.get(j);
	    
	    list.set(i, new GCILTLot( lota.color, lotb.days ));
	    list.set(j, new GCILTLot( lotb.color, lota.days ));
	    
	    repairOvenLots( problem, ind, y, t, (i < j)?(i):(j) );
	}
    }
    
    private static void mutationTimeTransfer( GCILTNProblem problem, GCILTCodification ind ) throws Exception {
	GCILTLot lota, lotb;
	LinkedList<GCILTLot> list;
        int y, t, i, j, time;
	
	y = problem.rnd.nextInt( 0, problem.inst.Y - 1 );
	t = problem.rnd.nextInt( 0, problem.inst.T - 1 );
	if (ind.Syt[y][t].size() > 1) {
	    list = ind.Syt[y][t];
	    i = problem.rnd.nextInt( 0, list.size() - 1);
	    do {
		j = problem.rnd.nextInt( 0, list.size() - 1);
	    } while (i == j);
	    
	    lota = list.get(i);
	    lotb = list.get(j);
	    
	    time = problem.rnd.nextInt( 1, lota.days );
	    
	    list.set(j, new GCILTLot( lotb.color, lotb.days + time ));
	    if (time == lota.days)
		list.remove(i);
	    else
		list.set(i, new GCILTLot( lota.color, lota.days - time ));
	    
	    repairOvenLots( problem, ind, y, t, (i < j)?(i):(j) );
	}
    }
    
    private static void mutationRemoveLot( GCILTNProblem problem, GCILTCodification ind ) throws Exception {
	LinkedList<GCILTLot> list;
        int y, t, i;
	
	y = problem.rnd.nextInt( 0, problem.inst.Y - 1 );
	t = problem.rnd.nextInt( 0, problem.inst.T - 1 );
	if (ind.Syt[y][t].size() > 1) {
	    list = ind.Syt[y][t];
	    i = problem.rnd.nextInt( 0, list.size() - 1);
	    list.remove(i);
	    
	    normalize(problem, ind, y, t);
	    repairOvenLots(problem, ind, y, t, (i >= list.size())?(list.size() - 1):(i) );
	}
    }
    
    private static void mutationAddLot( GCILTNProblem problem, GCILTCodification ind ) throws Exception {
	LinkedList<GCILTLot> list;
        int y, t, i, l, time, prevColor;
	
	y = problem.rnd.nextInt( 0, problem.inst.Y - 1 );
	t = problem.rnd.nextInt( 0, problem.inst.T - 1 );
	if (ind.Syt[y][t].size() > 1) {
	    list = ind.Syt[y][t];
	    i = problem.rnd.nextInt( -1, list.size() - 1);
	    
	    if (i >= 0)
		prevColor = list.get(i).color;
	    else
		prevColor = problem.inst.ovenStartColor(y);
	    
	    l = randomColor(problem, y);
	    time = problem.rnd.nextInt( problem.inst.fullSetup(prevColor, l, y) + 1, problem.inst.Qt[t] );
	    list.add(i + 1, new GCILTLot( l, time ) );
	    
	    normalize(problem, ind, y, t);
	    repairOvenLots(problem, ind, y, t, (i < 0) ? (0) : (i));
	}
    }
    
    private static void repairAllOvens( GCILTNProblem problem, GCILTCodification ind ) throws Exception {
        for (int y = 0; y < problem.inst.Y; y++)
	    repairOvenLots( problem, ind, y, 0, 0);
    }
    
    private static void repairOvenLots( GCILTNProblem problem, GCILTCodification ind, int y, int t0, int i0 ) throws Exception {
	GCILTInstance inst = problem.inst;
	GCILTLot curLot, prevLot, nextLot;
	LinkedList<GCILTLot> curList, nextList;
	int prevColor, initColor, t, i, nt, ni;
	
	curList = ind.Syt[y][t0];
	
	// Getting last color and initializing iterators
	// Index is first lot in period
	if (i0 == 0) {
	    if (t0 == 0) {
		prevLot = null;
		prevColor = inst.ovenStartColor(y);
	    }
	    else {
		prevLot  = ind.Syt[y][t0-1].getLast();
		prevColor = prevLot.color;
	    }
	}
	
	// Index is not first lot in period
	else {
	    prevLot = curList.get(i0 - 1);
	    prevColor = prevLot.color;
	}
	
	curLot = curList.get(i0);
	t = t0;
	i = i0;
	
	ni = i;
	nt = t;

	nextList = ind.Syt[y][nt];
	nextLot = nextList.get(ni);
	
	// Now start repairing by removing lots with no
	// remaining production time
	int setupTime, colorTime;
	do {
	    setupTime = inst.fullSetup(prevColor, curLot.color, y);
	    
	    // Get all the time this color is produced
	    
	    colorTime = 0;
	    
	    while ( nextLot != null && nextLot.color == curLot.color ) {
		colorTime += nextLot.days;
		
		if (ni + 1 < nextList.size()) {
		    ni++;
		    nextLot = nextList.get(ni);
		}
		else if (nt + 1 < problem.inst.T) {
		    nt++;
		    ni = 0;
		    nextList = ind.Syt[y][nt];
		    nextLot = nextList.getFirst();
		}
		else
		    nextLot = null;
	    }
	    
	    // This lot is too small, change its color to the
	    // previous one

	    if (setupTime >= colorTime) {
		initColor = curLot.color;
		
		while (curLot != null && curLot.color == initColor) {
		    curLot = new GCILTLot( prevColor, curLot.days );
		    curList.set( i, curLot );
		    
		    if (i + 1 < curList.size()) {
			i++;
			curLot = curList.get(i);
		    }
		    else if (t + 1 < problem.inst.T) {
			t++;
			i = 0;
			curList = ind.Syt[y][t];
			curLot = curList.getFirst();
		    }
		    else
			curLot = null;
		}
	    }
	    
	    // This lot is ok
	    
	    else {
		prevColor = curLot.color;
		
		curList = nextList;
		curLot = nextLot;
		i = ni;
		t = nt;
	    }
	} while (curLot != null);
    }
    
    private static void normalize( GCILTNProblem problem, GCILTCodification ind, int y, int t ) throws Exception {
	GCILTLot curLot;
	LinkedList<GCILTLot> list;
	int totalTime = 0, maxTime = problem.inst.Qt[t], time;
	
	list = ind.Syt[y][t];
	
	for (GCILTLot lot: list)
	    totalTime += lot.days;
	
	for (int i = 0; i < list.size(); i++) {
	    curLot = list.get(i);
	    time = Math.max( curLot.days * maxTime / totalTime, 1 );
	    
	    maxTime -= time;
	    totalTime -= curLot.days;
	    
	    list.set(i, new GCILTLot( curLot.color, time ));
	}
    }
    
    public static void main( String[] args ) throws Exception {
	GCILTNProblem problem = new GCILTNProblem();
        
        //hemj 00
        //onzh 05
        //darc 10
        //flcy 15
        //aciz 20
        //mtda 30
        //reyx 40
        //jbvm 67
        //pitv 80
        //yfxs 91
        //juvm 94
        
	problem.inst.file = new File("./gcilt/00.dat");
	problem.inst.load();
	
	opInitializer opinit = new opInitializer();
	GCILTCodification ind1 = (GCILTCodification) problem.build_codif();
	GCILTCodification ind2 = (GCILTCodification) problem.build_codif();
	opinit.initialize(problem, ind1);
	opinit.initialize(problem, ind2);

	ind1.print(problem);
	ind2.print(problem);
	
	oCrossover cross = new opCrossoverMicroOX();
	
	GCILTCodification child = (GCILTCodification) cross.crossover(problem, ind1, ind2);
	
	child.print(problem);

	repairAllOvens(problem, child);
	
	child.print(problem);
    }
    
    
    
     /*
            ind.lotSchedule[0][0].add(new GCILTLot(0, 22));
            ind.lotSchedule[0][0].add(new GCILTLot(1, 4));
            ind.lotSchedule[0][0].add(new GCILTLot(2, 5));
            
            ind.lotSchedule[0][1].add(new GCILTLot(2, 24));
            ind.lotSchedule[0][1].add(new GCILTLot(1, 7));
            
            ind.lotSchedule[0][2].add(new GCILTLot(1, 6));
            ind.lotSchedule[0][2].add(new GCILTLot(0, 24));
            
            ind.lotSchedule[0][3].add(new GCILTLot(0, 22));

            --------------- Alyt: (y,l) x t ---------------------
                ( 0, 3) |  1.00  0.00  1.00  1.00  1.00  1.00 
                ( 0, 5) |  0.00  1.00  0.00  0.00  0.00  0.00 
                ( 1, 2) |  1.00  1.00  1.00  1.00  1.00  1.00 
            --------------- Tluyt: (y,l,u) x t ---------------------
             ( 0, 0, 1) |  1.00  0.00  0.00  0.00  0.00  0.00 
             ( 0, 0, 3) |  0.00  1.00  0.00  0.00  0.00  0.00 
             ( 0, 1, 2) |  1.00  0.00  0.00  0.00  0.00  0.00 
             ( 0, 2, 5) |  1.00  0.00  0.00  0.00  0.00  0.00 
             ( 0, 3, 0) |  1.00  0.00  0.00  0.00  0.00  0.00 
             ( 0, 5, 0) |  0.00  1.00  0.00  0.00  0.00  0.00 
            --------------- Blyt: (y,l) x t ---------------------
            ( 0, 0) | 10.00 15.00  0.00  0.00  0.00  0.00 
            ( 0, 1) |  8.00  0.00  0.00  0.00  0.00  0.00 
            ( 0, 2) |  2.00  0.00  0.00  0.00  0.00  0.00 
            ( 0, 3) |  5.00  2.00 16.00  0.00  0.00  0.00 
            ( 0, 5) |  6.00 14.00  0.00  0.00  0.00  0.00 
            ( 1, 2) | 14.00  0.00  0.00  0.00  0.00  0.00 
            
            
            ind.Syt[0][0].add(new GCILTLot(3, 5));
            ind.Syt[0][0].add(new GCILTLot(0, 10));
            ind.Syt[0][0].add(new GCILTLot(1, 8));
            ind.Syt[0][0].add(new GCILTLot(2, 2));
            ind.Syt[0][0].add(new GCILTLot(5, 6));
            
            ind.Syt[0][1].add(new GCILTLot(5, 14));
            ind.Syt[0][1].add(new GCILTLot(0, 15));
            ind.Syt[0][1].add(new GCILTLot(3, 2));
            
            ind.Syt[0][2].add(new GCILTLot(3, 16));
            
            ind.Syt[1][0].add(new GCILTLot(2, 14));

            */
}
