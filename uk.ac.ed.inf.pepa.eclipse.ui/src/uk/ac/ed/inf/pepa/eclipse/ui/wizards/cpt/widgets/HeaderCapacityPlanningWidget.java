package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class HeaderCapacityPlanningWidget {

	public HeaderCapacityPlanningWidget(String[] titles,
			Composite container, 
			int pad,
			int parentWidth) {
		
		int width = parentWidth;
		
		if(pad > 0)
			width = width - 50;
		
		width = (width/titles.length);
		
		//pad
		Label label = new Label(container, SWT.SINGLE | SWT.FILL);
		label.setText("");
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 16, 1);
		label.setLayoutData(data);
		
		if(pad > 0){
			label = new Label(container, SWT.SINGLE | SWT.FILL );
			label.setText("");
			data = new GridData(SWT.FILL, SWT.FILL, true, false, pad, 1);
			data.widthHint = 25;
			label.setLayoutData(data);
		}
		
		for(int i = 0; i < titles.length; i++){
			label = new Label(container, SWT.SINGLE | SWT.FILL | SWT.CENTER | SWT.BORDER );
			label.setText(titles[i]);
			data = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
			data.widthHint = width;
			label.setLayoutData(data);
			FontData labelFontData = new FontData("", 10, SWT.BOLD);
			label.setFont(new Font(container.getDisplay(), labelFontData));
		}
		
		
		if(pad > 0){
			label = new Label(container, SWT.SINGLE | SWT.FILL );
			label.setText("");
			data = new GridData(SWT.FILL, SWT.FILL, true, false, pad, 1);
			data.widthHint = 25;
			label.setLayoutData(data);
		}
		
		
	}


}
