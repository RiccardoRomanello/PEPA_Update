grammar CSL;

options {
  language = Java;
}

@header {
package uk.ac.ed.inf.pepa.ctmc.modelchecking.parsing;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.*;
}

@lexer::header {
package uk.ac.ed.inf.pepa.ctmc.modelchecking.parsing;

import java.util.HashMap;
}

cslProperty returns [CSLAbstractStateProperty value]
    : s=stateFormula { $value = s; }
    ;

stateFormula returns [CSLAbstractStateProperty value]
    : s=orStateFormula { $value = s; }
      ( '=>' s=orStateFormula  { $value = new CSLImpliesNode($value,s); } )*
    ;

orStateFormula returns [CSLAbstractStateProperty value]
    : s=andStateFormula { $value = s; }
      ( '&' s=andStateFormula { $value = new CSLAndNode($value,s); } )*
    ;

andStateFormula returns [CSLAbstractStateProperty value]
    : s=notStateFormula { $value = s; }
      ( '|' s=notStateFormula { $value = new CSLOrNode($value,s); } )*
    ;

notStateFormula returns [CSLAbstractStateProperty value]
    : (n='!')? s=baseStateFormula { if (n!=null) { $value = new CSLNotNode(s); } else { $value = s; } }
    ;


baseStateFormula returns [CSLAbstractStateProperty value]
    : 'true'                                  { $value = new CSLBooleanNode(true); }
    | 'false'                                 { $value = new CSLBooleanNode(false); }
    | id=identifier                           { $value = new CSLAtomicNode(id); }
    | 'P' c=comparator '[' p=pathFormula ']'  { $value = new CSLPathPropertyNode(p,c); }
    | 'S' c=comparator '[' s=stateFormula ']' { $value = new CSLSteadyStateNode(s,c); }
    | 'L' c=comparator '[' s=stateFormula ']' { $value = new CSLLongRunNode(s,c); }
    | '(' s=stateFormula ')'                  { $value = s; }
    ;

pathFormula returns [CSLAbstractPathProperty value]
    : 'X' ( i=interval s=stateFormula { $value = new CSLNextNode(s,i); }
          | s=stateFormula            { $value = new CSLNextNode(s,new CSLTimeInterval()); }
          )
    | 'F' ( i=interval s=stateFormula { $value = new CSLEventuallyNode(s,i); }
          | s=stateFormula            { $value = new CSLEventuallyNode(s,new CSLTimeInterval()); }
          )
    | 'G' ( i=interval s=stateFormula { $value = new CSLGloballyNode(s,i); }
          | s=stateFormula            { $value = new CSLGloballyNode(s,new CSLTimeInterval()); }
          )
    | s1=stateFormula 'U' ( i=interval s2=stateFormula { $value = new CSLUntilNode(s1,s2,i); }
                          | s2=stateFormula            { $value = new CSLUntilNode(s1,s2,new CSLTimeInterval()); }
                          )
    ;

comparator returns [CSLAbstractProbability value]
    : '=?'     { $value = new CSLProbabilityTest(); }
    | '<=' NUM { double prob = Double.parseDouble($NUM.getText());
                 $value = new CSLProbabilityComparator(true, new CSLDouble(true, prob)); }
    | '>=' NUM { double prob = Double.parseDouble($NUM.getText());
                 $value = new CSLProbabilityComparator(false, new CSLDouble(true, prob)); }
    ;

interval returns [CSLTimeInterval value]
    : '[' n1=NUM ',' n2=NUM ']' { double lower = Double.parseDouble($n1.getText());
                                  double upper = Double.parseDouble($n2.getText()); 
                                  $value = new CSLTimeInterval(new CSLDouble(false, lower), new CSLDouble(false, upper)); }
    | '<=' NUM                  { double upper = Double.parseDouble($NUM.getText());
                                  $value = new CSLTimeInterval(new CSLDouble(false, upper),false); }
    | '>=' NUM                  { double lower = Double.parseDouble($NUM.getText());
                                  $value = new CSLTimeInterval(new CSLDouble(false, lower),true); }
    ;

identifier returns [String value]
    : ID { String id = $ID.getText();
           $value = id.substring(1,id.length()-1); }
    ;

INT : '0'..'9'+ ;
NUM : INT ('.' INT)? ;
ID  : '"' (~'"')+ '"' ;

WS  :   (' '|'\t'|'\r'|'\n')+ { $channel = HIDDEN; } ; 


