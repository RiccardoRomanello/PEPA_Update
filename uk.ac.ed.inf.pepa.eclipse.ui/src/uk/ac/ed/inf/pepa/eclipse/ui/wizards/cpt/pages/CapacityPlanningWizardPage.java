package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.pages;

import java.util.ArrayList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.CapacityPlanningWidget;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.FooterCapacityPlanningWidget;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.HeaderCapacityPlanningWidget;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets.HeaderTargetCapacityPlanningWidget;

public abstract class CapacityPlanningWizardPage extends WizardPage {
	
	protected ArrayList<CapacityPlanningWidget> widgets;
	protected int fixedWidth = 600;
	protected Composite parent;
	
	protected final IValidationCallback parentCallBack = new IValidationCallback() {

		public void validate() {
			completePage();
		}
	};

	public CapacityPlanningWizardPage() {
		super(CPTAPI.getEvaluationControls().getValue());
		
		this.widgets = new ArrayList<CapacityPlanningWidget>();
		
		setOwnTitle();
	}
	
	public abstract void setOwnTitle();
	
	@Override
	public void createControl(Composite parent) {
		
		this.parent = parent;
		
		Composite container = new Composite(parent, SWT.NONE);
		
		constructPage(this.parentCallBack,container);
		
		Layout layout = new GridLayout(3,false);
		container.setLayout(layout);
		
		GridData data = new GridData();
		data.widthHint = fixedWidth;
		container.setLayoutData(data);
		
		setControl(container);
		
		setHelp();
		
	}
	
	protected abstract void setHelp();
	
	protected abstract void constructPage(IValidationCallback cb, Composite container);
	
	public void completePage() {
		
		String inputError = "";
		
		boolean bool = true;
		for (CapacityPlanningWidget w : widgets){
			bool = bool & w.isValid().valid;
			String temp = w.isValid().complaint;
			if(temp.length() > 0)
				inputError = temp;
		}
		
		if(inputError.length() > 0){
			setErrorMessage(inputError);
		} else {
			setErrorMessage(null);
		}
		
		setPageComplete(bool);
	}
	
	public void pad(Composite container){
		Label label = new Label(container, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		label.setLayoutData(data);
	}
	
	public Composite center(Composite container){
		
		Composite child = new Composite(container, SWT.NONE );
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		child.setLayoutData(data);
		GridLayout layout = new GridLayout(16,false);
		child.setLayout(layout);
		
		return child;
	}
	
	public Composite centerH(Composite container){
		
		Composite child = new Composite(container, SWT.NONE );
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 16, 1);
		child.setLayoutData(data);
		GridLayout layout = new GridLayout(16,false);
		child.setLayout(layout);
		
		return child;
	}
	
	public Composite centerVertical(Composite container){
		
		Composite child = new Composite(container, SWT.NONE );
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		child.setLayoutData(data);
		GridLayout layout = new GridLayout(16,false);
		child.setLayout(layout);
		
		return child;
	}
	
	public void header(String[] titles, Composite child, int padding){
		new HeaderCapacityPlanningWidget(titles, child, padding, fixedWidth);
	}
	
	public void headerTarget(String[] titles, Composite child){
		new HeaderTargetCapacityPlanningWidget(titles, child, fixedWidth);
	}
	
	public void footer(Composite child){
		new FooterCapacityPlanningWidget(child, fixedWidth);
	}
	
	
	public int getMiddle(){
		return this.fixedWidth - (getPadding() * 2);
	}
	
	public int getPadding(){
		return 25;
	}
	

}
