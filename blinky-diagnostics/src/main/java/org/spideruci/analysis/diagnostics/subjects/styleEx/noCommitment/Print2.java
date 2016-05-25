/*
@author Charles.Y.Feng
@date May 12, 2016 5:02:52 PM
*/

package org.spideruci.analysis.diagnostics.subjects.styleEx.noCommitment;

import java.util.ArrayList;


public class Print2 implements Print {
	@SuppressWarnings("unchecked")
	public void print(Object counts){
		for(Pair cur : (ArrayList<Pair>) counts){
			System.out.println(cur.word + "  -  " + cur.count);
		}
	}
}

