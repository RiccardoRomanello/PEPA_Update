package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

import uk.ac.ed.inf.common.ui.plotview.views.PlotView;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.ui.Activator;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;

public abstract class PerformanceMetricDialog extends TitleAreaDialog {

	private static final String DIALOG_SETTINGS_NAME = "PERFORMANCE_METRIC_DIALOG";

	protected Button okButton;

	protected IParametricDerivationGraph fDerivationGraph;

	/*
	 * It is not strictly necessary to have this field because it can be
	 * obtained by fPepaModel
	 */
	protected OptionMap fOptionMap;

	protected IPepaModel fPepaModel;

	protected SolverOptionsHandler fSolverOptionsHandler;

	protected Text filterText;

	private boolean isFluid;

	public PerformanceMetricDialog(boolean supportsTransient, boolean isFluid,
			Shell parentShell, IParametricDerivationGraph derivationGraph,
			IPepaModel model) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		this.fDerivationGraph = derivationGraph;
		this.fOptionMap = model.getOptionMap();
		this.fPepaModel = model;
		final IValidationCallback cb = new IValidationCallback() {

			public void validate() {
				enableOKButton();
			}
		};
		if (isFluid)
			this.fSolverOptionsHandler = new ODESolverOptionsHandler(
					supportsTransient, fOptionMap, cb);
		else
			this.fSolverOptionsHandler = new SimulationSolverOptionsHandler(
					supportsTransient, fOptionMap, cb);
		this.isFluid = isFluid;

	}

	public final boolean isFluid() {
		return isFluid;
	}

	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS_NAME);
		if (section == null) {
			section = settings.addNewSection(DIALOG_SETTINGS_NAME);
		}
		return section;
	}

	protected final Control createDialogArea(Composite parent) {
		Composite composite = (Composite) fSolverOptionsHandler
				.createDialogArea(parent);

		addOptions(composite);
		Label actions = new Label(composite, SWT.NONE);
		actions.setText(getViewerHeader());
		GridData actionsData = new GridData(GridData.FILL_HORIZONTAL);
		actionsData.horizontalSpan = 2;
		actions.setLayoutData(actionsData);

		filterText = new Text(composite, SWT.SEARCH | SWT.CANCEL);
		filterText.setMessage("Search");
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.horizontalSpan = 2;
		filterText.setLayoutData(textData);

		final StructuredViewer viewer = createViewer(composite);
		final ViewerFilter[] filter = new ViewerFilter[] { getViewerFilter() };

		filterText.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					viewer.resetFilters();
				}
			}

		});
		filterText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				viewer.setFilters(filter);
			}

		});
		parent.pack();
		return composite;
	}

	protected abstract void addOptions(Composite composite);

	protected abstract StructuredViewer createViewer(Composite composite);

	protected abstract String getDialogTitle();

	protected abstract AnalysisJob getAnalysisJob();

	protected abstract ViewerFilter getViewerFilter();

	protected abstract String getViewerHeader();

	/**
	 * This implementation saves the current option map. If super.okPressed() is
	 * called by implementors it means that the analysis has been carried out OK
	 */
	protected final void okPressed() {
		OptionMap map = fSolverOptionsHandler.updateOptionMap();
		this.fPepaModel.setOptionMap(map);
		AnalysisJob job = getAnalysisJob();
		job.setUser(true);
		job.schedule();
		super.okPressed();
	}

	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		okButton = getButton(IDialogConstants.OK_ID);
		okButton.setText("Analyse");
		enableOKButton();
		return control;
	}

	protected final void enableOKButton() {
		boolean okEnabled = isOKButtonEnabled();
		if (!okEnabled) {
			setErrorMessage("Configuration not valid");
		} else {
			setErrorMessage(null);
		}
		okButton.setEnabled(isOKButtonEnabled());
	}

	/**
	 * If it is enabled, it updates the option map with the current values
	 * 
	 * @return
	 */
	protected boolean isOKButtonEnabled() {
		return fSolverOptionsHandler.isConfigurationValid();

	}

	static boolean isModal(Job job) {
		Boolean isModal = (Boolean) job
				.getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
		if (isModal == null)
			return false;
		return isModal.booleanValue();
	}

	static void display(final uk.ac.ed.inf.common.ui.plotting.IChart chart) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				final IWorkbenchPage page = window.getActivePage();
				final Shell shell = window.getShell();
				try {
					PlotView plotView = (PlotView) page.showView(PlotView.ID);
					plotView.reveal(chart);

				} catch (PartInitException e) {
					ErrorDialog.openError(shell, "Error",
							"Error displaying graph", e.getStatus());
				}
			}

		});
	}

}
