package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class HeaderTargetCapacityPlanningWidget {

	public HeaderTargetCapacityPlanningWidget(String[] titles,
			Composite container, 
			int parentWidth) {
		
		//pad
		Label label = new Label(container, SWT.SINGLE | SWT.FILL);
		label.setText("");
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 16, 1);
		label.setLayoutData(data);
		
		label = new Label(container, SWT.SINGLE | SWT.FILL | SWT.CENTER | SWT.BORDER );
		label.setText(titles[0]);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		data.widthHint = 300;
		label.setLayoutData(data);
		FontData labelFontData = new FontData("", 10, SWT.BOLD);
		label.setFont(new Font(container.getDisplay(), labelFontData));
		
		for(int i = 1; i < titles.length; i++){
			label = new Label(container, SWT.SINGLE | SWT.FILL | SWT.CENTER | SWT.BORDER );
			label.setText(titles[i]);
			data = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
			data.widthHint = 150;
			label.setLayoutData(data);
			labelFontData = new FontData("", 10, SWT.BOLD);
			label.setFont(new Font(container.getDisplay(), labelFontData));
		}
		
		
	}


}
