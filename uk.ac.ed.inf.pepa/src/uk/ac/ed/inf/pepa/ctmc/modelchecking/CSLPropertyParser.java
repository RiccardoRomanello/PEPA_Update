package uk.ac.ed.inf.pepa.ctmc.modelchecking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.parsing.CSLLexer;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.parsing.CSLParser;

public class CSLPropertyParser {

	public static CSLAbstractStateProperty parse(String property) {
		try {
			//System.out.println("Parsing: " + property);
			InputStream input = new ByteArrayInputStream(property.getBytes());
			CSLLexer lex = new CSLLexer(new ANTLRInputStream(input));
			CommonTokenStream tokens = new CommonTokenStream(lex);
			CSLParser parser = new CSLParser(tokens);
			return parser.stateFormula();
		} catch (IOException e) {
			return null;
		} catch (RecognitionException e) {
			return null;
		}
	}
	
}
