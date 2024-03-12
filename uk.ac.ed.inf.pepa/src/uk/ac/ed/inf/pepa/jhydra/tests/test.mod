
% comment 1

\model{

% comment 2
 % comment 3

%	\header{#define ipcmin(a,b) (a < b) ? (a) : (b)}
	
	
	%% Alter the rate constant(s) below as desired:


\constant{LAMBDA}{1.0}
\constant{NN}{3}

\statevector{
  \type{int}{p1, p2, p3}
}

\initial{
  p1 = NN;
  p2 = 0;
  p3 = 0;
}

\transition{t1}{
  \condition{p1 > 0}
  \action{
    next->p1 = p1 - 1; 
    next->p2 = p2 + 1;
  }  
  \rate{ LAMBDA*2.0 }
}

\transition{t2}{
  \condition{p2 > 0}
  \action{
    next->p2 = p2 - 1;
    next->p3 = p3 + 1;
  }
  \rate{ 2.0 }
}

\transition{t3}{
  \condition{p3 > 0}
  \action{
    next->p3 = p3 - 1; 
    next->p1 = p1 + 1;
  }  
  \rate{ 3.0 }
}

}

\solution{
  \method{gauss}
}

\passage{
  \sourcecondition{p3==NN}
  \targetcondition{p1==NN}

  \t_start{0.1}
  \t_stop{10.0}
  \t_step{0.1}
}




