/**
 * 
 */
package uk.ac.ed.inf.pepa.eclipse.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.core.IOptionHandler;

/**
 * @author Giacomo Alzetta
 *
 */
public class AggregationWizard extends Wizard {

	private final static String SETTINGS_PAGE_NAME = "settings_page!";
	
	SettingsPage setPage = null;


	private OptionMap options;
	private IOptionHandler handler;
	
	public AggregationWizard(IOptionHandler handler) {
		super();
		this.options = handler.getOptionMap();
		this.handler = handler;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (setPage.isPageComplete()) {
			setPage.applySettings();
		}
		
		handler.setOptionMap(options);
		
		return true;
	}
	
	@Override
	public void addPages() {
		setPage = new SettingsPage(SETTINGS_PAGE_NAME);
		addPage(setPage);
	}

	/**
	 * @author Giacomo Alzetta
	 *
	 */
    private class SettingsPage extends WizardPage {
    	    	
    	private Button aggregationEnabled;
    	
    	private Button aggregateArrays;
    	
    	private Combo aggregationAlgorithm;
    	
    	private Button useLinkedPartition;
    	
    	
    	private static final String AGGREGATION_NONE = "No aggregation";
    	private static final String AGGREGATION_CONTEXTUAL_LUMPABILITY = "Contextual Lumpability";
    	private static final String AGGREGATION_EXACT_EQUIVALENCE = "Exact Equivalence";
    	private static final String AGGREGATION_STRONG_EQUIVALENCE = "Strong Equivalence";
    	private static final String AGGREGATION_PROPORTIONAL_LUMPABILITY = "Proportional Lumpability";
    	
    	private int aggregationChosen = OptionMap.AGGREGATION_NONE;
    	private int partitionType = OptionMap.USE_ARRAY_PARTITION;

    	protected SettingsPage(String pageName) {
			super(pageName);
			this.setTitle("Aggregation Settings");
			this.setDescription("Choose aggregation options.");
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createControl(Composite parent) {			
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(1, true));
			setControl(composite);
			
			aggregationEnabled = new Button(composite, SWT.CHECK);
			aggregationEnabled.setText("Enable aggregation");
			aggregationEnabled.setLayoutData(createDefaultGridData());
			aggregationEnabled.addListener(SWT.Selection,
					new Listener() {
						public void handleEvent(Event event) {
							boolean enabled = aggregationEnabled.getSelection();
							aggregateArrays.setEnabled(enabled);
							aggregationAlgorithm.setEnabled(enabled);
							validate();
						}
			});
			
			aggregateArrays = new Button(composite, SWT.CHECK);
			aggregateArrays.setText("Use aggregate arrays");
			aggregateArrays.setLayoutData(createDefaultGridData());
			aggregateArrays.addListener(SWT.Selection, 
					new Listener() {
						public void handleEvent(Event event) {
							validate();
						}
			});
			
			aggregationAlgorithm = new Combo(composite, SWT.READ_ONLY);
			aggregationAlgorithm.setLayoutData(createDefaultGridData());
			
			String[] items = {
					AGGREGATION_NONE,
					AGGREGATION_CONTEXTUAL_LUMPABILITY,
					AGGREGATION_EXACT_EQUIVALENCE,
					AGGREGATION_STRONG_EQUIVALENCE,
					AGGREGATION_PROPORTIONAL_LUMPABILITY
			};
			aggregationAlgorithm.setItems(items);
			aggregationAlgorithm.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String text = aggregationAlgorithm.getText();
					if (text == null || text.equals(AGGREGATION_NONE)) {
						aggregationChosen = OptionMap.AGGREGATION_NONE;
						useLinkedPartition.setVisible(false);
					} else { 
						if (text.equals(AGGREGATION_CONTEXTUAL_LUMPABILITY)) {
							aggregationChosen = OptionMap.AGGREGATION_CONTEXTUAL_LUMPABILITY;
						} else if (text.equals(AGGREGATION_EXACT_EQUIVALENCE)) {
							aggregationChosen = OptionMap.AGGREGATION_EXACT_EQUIVALENCE;
						} else if (text.equals(AGGREGATION_STRONG_EQUIVALENCE)) {
							aggregationChosen = OptionMap.AGGREGATION_STRONG_EQUIVALENCE;
						} else if (text.equals(AGGREGATION_PROPORTIONAL_LUMPABILITY)) {
							aggregationChosen = OptionMap.AGGREGATION_PROPORTIONAL_LUMPABILITY;
						}
						useLinkedPartition.setVisible(true);
					}
				}
			});
			
			useLinkedPartition = new Button(composite, SWT.CHECK);
			useLinkedPartition.setText("Use linked-list based partition refinement data structure.");
			useLinkedPartition.setLayoutData(createDefaultGridData());
			useLinkedPartition.addListener(SWT.Selection,
					new Listener() {
						public void handleEvent(Event event) {
							validate();
						}
			});
			
			initContents();
			validate();
		}
		
		private void initContents() {
			/* Should init from default settings */

			boolean enabled = (boolean)options.get(OptionMap.AGGREGATION_ENABLED);
			aggregationEnabled.setSelection(enabled);
			
			aggregateArrays.setSelection(
					(boolean)options.get(OptionMap.AGGREGATE_ARRAYS));
			aggregateArrays.setEnabled(enabled);
			
			int algIndex = (int)options.get(OptionMap.AGGREGATION);
			aggregationAlgorithm.select(algIndex);
			aggregationAlgorithm.setEnabled(enabled);
			
			if (algIndex != OptionMap.AGGREGATION_NONE) {
				useLinkedPartition.setVisible(true);
			} else {
				useLinkedPartition.setVisible(false);
			}
			useLinkedPartition.setSelection(
					((Integer)options.get(OptionMap.PARTITION_TYPE)).equals(
							(Integer)OptionMap.USE_LINKED_PARTITION));
		}
		
		private boolean isAggregationEnabled() {
			return aggregationEnabled.getSelection();
		}
		
		private boolean areAggregateArraysEnabled() {
			return aggregateArrays.getSelection();
		}
		
		protected void applySettings() {
			
			options.put(OptionMap.AGGREGATION_ENABLED, isAggregationEnabled());
			options.put(OptionMap.AGGREGATION, aggregationChosen);
			options.put(OptionMap.AGGREGATE_ARRAYS, areAggregateArraysEnabled());
			int parType = useLinkedPartition.getSelection()
					? OptionMap.USE_LINKED_PARTITION
					: OptionMap.USE_ARRAY_PARTITION;
			options.put(OptionMap.PARTITION_TYPE, parType);
		}
		
		private GridData createDefaultGridData() {
			/* ...with grabbing horizontal space */
			return new GridData(SWT.FILL, SWT.CENTER, true, false);
		}
	
		public void validate() {
			// If we will ever need to add error validation,
			// do so here.
			
			setPageComplete(true);
		}
    }

}
