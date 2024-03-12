package uk.ac.ed.inf.pepa.ctmc.modelchecking;

import java.io.IOException;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;

public interface IMRMCGenerator {

	public boolean print(String fileName) throws DerivationException, IOException;
	
}
