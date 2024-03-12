package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.jface.action.Action;

public  class RunnableWithAction implements Runnable {

		protected Action action;

		public RunnableWithAction(Action action) {
			this.action = action;
		}

		public void run() {
			action.run();
		}

	}