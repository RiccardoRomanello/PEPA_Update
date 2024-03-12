package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.Config;
import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.KeyTripleValueWidgetForMinimumMaximumAndWeight;

public class PopulationCapacityPlanningWizardPage extends
		CapacityPlanningWizardPage {
	
	private IValidationCallback cb;

	public PopulationCapacityPlanningWizardPage(String pageName) {
		super();
		this.setDescription(pageName);
	}


	@Override
	protected void constructPage(IValidationCallback cb, Composite container) {
		
		this.cb = cb;
		
		//left pad
		pad(container);
		
		centerScroll(container);
		
		//left pad
		pad(container);

	}
	
	public void centerScroll(Composite container){
		
		Control control = CPTAPI.getPopulationControls();
		String[] keys = control.getKeys();
		
		ScrolledComposite sc = new ScrolledComposite(container, SWT.V_SCROLL | SWT.H_SCROLL );
		
		final Composite child = new Composite(sc, SWT.NONE);
		sc.setContent(child);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		child.setLayoutData(data);
		GridLayout layout = new GridLayout(16,false);
		child.setLayout(layout);
		
		String[] titles = {"Component","Minimum","Maximum", "Weight"};
		
		header(titles,child,0);
		
		for(int i = 0; i < keys.length; i++){
			String value1 = control.getValue(keys[i], Config.LABMIN);
			String value2 = control.getValue(keys[i], Config.LABMAX);
			String value3 = control.getValue(keys[i], Config.LABWEI);
			widgets.add(new KeyTripleValueWidgetForMinimumMaximumAndWeight(this.cb, child, keys[i],
					value1,
					value2,
					value3,
					control));
		}
		
		
		Point size = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		sc.setMinSize(size);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		sc.setLayoutData(data);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

	}
	
	@Override
	protected void setHelp() {
		String root = "uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt";
		String context = root + ".population";
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),context);;
		
	}
	
	@Override
	public void setOwnTitle() {
		String title = "Component populations";
			
		setTitle(title);
		
	}

}
