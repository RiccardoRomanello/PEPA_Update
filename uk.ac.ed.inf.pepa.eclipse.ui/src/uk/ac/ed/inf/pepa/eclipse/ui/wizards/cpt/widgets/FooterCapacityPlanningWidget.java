package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FooterCapacityPlanningWidget {

	public FooterCapacityPlanningWidget(Composite container, int fixedWidth) {
		
		//pad
		Label label = new Label(container, SWT.SINGLE | SWT.FILL);
		label.setText("");
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 16, 1);
		data.widthHint = fixedWidth;
		label.setLayoutData(data);
		
		
	}


}
