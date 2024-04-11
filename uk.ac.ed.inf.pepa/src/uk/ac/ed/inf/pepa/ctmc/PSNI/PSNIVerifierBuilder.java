/**
 * 
 */
package uk.ac.ed.inf.pepa.ctmc.PSNI;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.Compiler;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.StateExplorerBuilder;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

/**
 * Build a PSNI verifier
 */
public class PSNIVerifierBuilder
{
	
	public static PSNIVerifier createVerifier(ModelNode model) {

		Model cModel = new Compiler(false, model).getModel();
		
		StateExplorerBuilder seb = new StateExplorerBuilder(cModel);
		
		return new PSNIVerifier(seb.getExplorer(), seb.getSymbolGenerator());
	}
}
