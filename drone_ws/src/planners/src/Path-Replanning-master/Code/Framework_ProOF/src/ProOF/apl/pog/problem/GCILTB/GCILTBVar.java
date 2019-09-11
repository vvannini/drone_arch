/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILTB;

/**
 *
 * @author HossomiUser
 */
public class GCILTBVar {

	public GCILTBInstance inst;
	public int Zyt;
	public int Alyt[];
	public int Tluyt[][];
	public boolean free;

	public GCILTBVar(GCILTBInstance inst) {
		this.inst = inst;
		this.Alyt = new int[inst.L];
		this.Tluyt = new int[inst.L][inst.L];
		this.free = false;
	}

	public GCILTBVar(GCILTBVar src) {
		this(src.inst);
		this.Zyt = src.Zyt;
		this.free = src.free;
		
		// Essa copia garante que nao haja referencias
		// cruzadas indesejaveis
		System.arraycopy(src.Alyt, 0, this.Alyt, 0, inst.L);
		for (int l = 0; l < inst.L; l++) {
			System.arraycopy(src.Tluyt[l], 0, this.Tluyt[l], 0, inst.L);
		}
	}

	public int getStartColor() {
		for (int l = 0; l < inst.L; l++) {
			if (Alyt[l] == 1) {
				return l;
			}
		}

		return -1;
	}

	public int getLastColor() throws Exception {
		int start = getStartColor();
		int l = start;
		int u = getNextColor(l);

		while (u != -1) {
			//System.out.println("Next: " + u);
			l = u;
			u = getNextColor(l);
			if (u == start) {
				return start;
			}
		}
		//System.out.println("Next: " + l + " -> " + u);
		return l;
	}

	public int getNextColor(int l) throws Exception {
		if (l < 0 || l >= inst.L) {
			throw new Exception(String.format("l < 0 || l >= inst.L | l = %d", l));
		}
		
		for (int u = 0; u < inst.L; u++) {
			if (Tluyt[l][u] == 1) {
				return u;
			}
		}

		return -1;
	}

	public void setStartColor(int l) {
		for (int x = 0; x < inst.L; x++) {
			Alyt[x] = 0;
		}

		Alyt[l] = 1;
	}

	public void setNextColor(int l, int u) throws Exception {
		if (l < 0 || l >= inst.L) {
			throw new Exception(String.format("l < 0 || l >= inst.L | l = %d", l));
		}
		
		// Garantir que nao haja duas trocas para a mesma cor
		// e nem duas trocas a partir da mesma cor
		for (int x = 0; x < inst.L; x++) {
			Tluyt[l][x] = 0;
		}

		if (u >= 0 && u < inst.L) {
			for (int x = 0; x < inst.L; x++) {
				Tluyt[x][u] = 0;
			}

			Tluyt[l][u] = 1;
		}
	}

	public void clearSwaps() {
		for (int l = 0; l < inst.L; l++) {
			for (int u = 0; u < inst.L; u++) {
				Tluyt[l][u] = 0;
			}
		}
	}
}
