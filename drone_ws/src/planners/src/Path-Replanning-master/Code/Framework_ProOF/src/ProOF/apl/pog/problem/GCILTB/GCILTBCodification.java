/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILTB;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author Hossomi
 */
public class GCILTBCodification extends Codification<GCILTBProblem, GCILTBCodification> {

	public GCILTBVar var[][];

	public GCILTBCodification(GCILTBProblem problem) {
		GCILTBInstance inst = problem.inst;
		this.var = new GCILTBVar[inst.Y][inst.T];

		for (int y = 0; y < problem.inst.Y; y++) {
			for (int t = 0; t < problem.inst.T; t++) {
				this.var[y][t] = new GCILTBVar(inst);
			}
		}
	}

	@Override
	public void copy(GCILTBProblem problem, GCILTBCodification source) throws Exception {
		for (int y = 0; y < problem.inst.Y; y++) {
			for (int t = 0; t < problem.inst.T; t++) {
				this.var[y][t] = new GCILTBVar(source.var[y][t]);
			}
		}
	}

	@Override
	public GCILTBCodification build(GCILTBProblem gcilt) throws Exception {
		return new GCILTBCodification(gcilt);
	}

	public void print(GCILTBProblem problem) throws Exception {
		int count = 0;
		for (int y = 0; y < problem.inst.Y; y++) {
			for (int t = 0; t < problem.inst.T; t++) {
				if (var[y][t].free) count++;
			}
		}
		System.out.println("Codification: " + count + " free variables");
		for (int y = 0; y < problem.inst.Y; y++) {
			System.out.printf("[%d]: ", y);
			for (int t = 0; t < problem.inst.T; t++) {
				if (t == 0 || var[y][t-1].Zyt == 1) {
					int start = var[y][t].getStartColor();
					int l = start;
					int u = var[y][t].getNextColor(l);
					System.out.printf("%d", l);
					while (u != -1 && u != start) {
						System.out.printf(", %d", u);
						l = u;
						u = var[y][t].getNextColor(l);
					}
				}
				System.out.print(" | ");
			}
			System.out.println();
			
			/*for (int t = 0; t < problem.inst.T; t++) {
				System.out.print(var[y][t].getStartColor() + "-" + var[y][t].getLastColor() + " | ");
			}
			System.out.println();

			for (int t = 0; t < problem.inst.T; t++)
				System.out.print(var[y][t].Zyt + " | ");
				System.out.println();
	    
			for (int t = 0; t < problem.inst.T; t++)
				if (var[y][t].free)
					System.out.printf("(%d,%d) | ", y, t);
				System.out.println();
			 
			
			for (int l = 0; l < problem.inst.L; l++) {
				for (int t = 0; t < problem.inst.T; t++) {
					for (int u = 0; u < problem.inst.L; u++) {
						System.out.print((var[y][t].Tluyt[l][u] == 1 ? "x" : ".") + " ");
					}
					System.out.print("| ");
				}
				System.out.println();
			}
			System.out.println();*/
		}
	}
}
