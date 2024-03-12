/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import uk.ac.ed.inf.common.ui.wizards.SaveAsPage;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.core.ResourceUtilities;
import uk.ac.ed.inf.pepa.tools.Latexifier;

/**
 * This wizard guides the user through the process of creating a LaTeX snippet
 * for a PEPA model.
 * 
 * @author mtribast
 * 
 */
public class LatexifierWizard extends Wizard {

	/** Extension for TeX files */
	private static final String EXTENSION = "tex";


	/**
	 * Page for Latexifier settings
	 * 
	 * @author mtribast
	 * 
	 */
	private class SettingPage extends WizardPage {

		private static final String SECTION_NAME = "latexifier.settingPage";

		/* Latexifier settings */
		private Button setsOnSeparateLinesButton;

		private static final String SEPARATE_LINES_BUTTON = "latexifier.settingPage.separateLines";

		private Text separateLinesName;

		private static final String SEPARATE_LINES_NAME = "latexifier.settingPage.separateLinesLabel";

		private Button showPreviewButton;

		private static final String SHOW_PREVIEW_BUTTON = "latexifier.settingPage.showPreviewButton";

		private Button allowExtraSpaceButton;

		private static final String ALLOW_EXTRA_SPACE_BUTTON = "latexifier.settingPage.allowExtraSpaceButton";

		private Text allowExtraSpaceText;

		private static final String ALLOW_EXTRA_SPACE_TEXT = "latexifier.settingPage.allowExtraSpaceText";

		private Latexifier latexifier;

		private IDialogSettings settings;

		protected SettingPage(String pageName) {
			super(pageName);
			this.setTitle("Latexify");
			this.setDescription("Select options for the converter");

			latexifier = new Latexifier(model.getAST());
		}

		public void createControl(Composite parent) {
			int textStyle = SWT.SINGLE | SWT.LEFT | SWT.BORDER;
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			setControl(composite);

			/* First line, separate lines */
			setsOnSeparateLinesButton = new Button(composite, SWT.CHECK);
			setsOnSeparateLinesButton
					.setText("Synchronisation and hiding set definitions on separate lines");
			setsOnSeparateLinesButton.setLayoutData(createDefaultGridData());
			setsOnSeparateLinesButton.addListener(SWT.Selection,
					new Listener() {

						public void handleEvent(Event event) {

							validate();
						}
					});

			separateLinesName = new Text(composite, textStyle);
			separateLinesName.setLayoutData(createDefaultGridData());
			separateLinesName.addListener(SWT.Modify, new Listener() {

				public void handleEvent(Event event) {
					validate();
				}
			});

			/* Second line, extra space */
			allowExtraSpaceButton = new Button(composite, SWT.CHECK);
			allowExtraSpaceButton.setText("Allow extra space between sections");
			allowExtraSpaceButton
					.setToolTipText("Extra space (LaTeX command [ex]) allowed between rate definitions, process definitions and system equation");
			allowExtraSpaceButton.setLayoutData(createDefaultGridData());
			allowExtraSpaceButton.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event event) {

					validate();
				}

			});

			allowExtraSpaceText = new Text(composite, textStyle);
			allowExtraSpaceText.setLayoutData(createDefaultGridData());
			allowExtraSpaceText.addListener(SWT.Modify, new Listener() {

				public void handleEvent(Event event) {
					validate();

				}
			});

			showPreviewButton = new Button(composite, SWT.CHECK);
			showPreviewButton.setText("Show preview");
			GridData previewData = createDefaultGridData();
			previewData.horizontalSpan = 2;
			showPreviewButton.setLayoutData(previewData);

			showPreviewButton.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event event) {
					validate();
				}

			});

			initContents();
			validate();

		}

		private void initContents() {
			/* Should init from default settings */
			IDialogSettings uiSettings = uk.ac.ed.inf.pepa.eclipse.ui.Activator
					.getDefault().getDialogSettings();
			settings = uiSettings.getSection(SECTION_NAME);
			if (settings == null)
				settings = uiSettings.addNewSection(SECTION_NAME);

			setsOnSeparateLinesButton.setSelection(settings
					.getBoolean(SEPARATE_LINES_BUTTON));
			String value = settings.get(SEPARATE_LINES_NAME);
			separateLinesName.setText(value != null ? value : latexifier
					.getLabel());
			allowExtraSpaceButton.setSelection(settings
					.getBoolean(ALLOW_EXTRA_SPACE_BUTTON));
			value = settings.get(ALLOW_EXTRA_SPACE_TEXT);
			allowExtraSpaceText.setText(value != null ? value : latexifier
					.getExtraSpace()
					+ "");
			showPreviewButton.setSelection(settings
					.getBoolean(SHOW_PREVIEW_BUTTON));

		}

		public void validate() {
			this.setErrorMessage(null);
			this.setPageComplete(false);
			separateLinesName.setEnabled(setsOnSeparateLinesButton
					.getSelection());
			allowExtraSpaceText
					.setEnabled(allowExtraSpaceButton.getSelection());
			if (allowExtraSpaceButton.getSelection()) {
				boolean extraSpaceOk = true;
				try {
					double extraSpace = Double.valueOf(allowExtraSpaceText
							.getText());
					if (extraSpace < 0)
						extraSpaceOk = false;
				} catch (NumberFormatException e) {
					extraSpaceOk = false;
				} finally {
					if (!extraSpaceOk) {
						setErrorMessage("Value not allowed");
						return;
					}
				}
			}
			if (setsOnSeparateLinesButton.getSelection()) {
				if (separateLinesName.getText().equals("")) {
					setErrorMessage("Insert a valid label");
					return;
				}

			}
			this.setPageComplete(true);
		}

		/**
		 * Can be called only when the page is complete
		 * 
		 * @return the latexifier with the given parameters
		 */
		public Latexifier getLatexifier() {
			if (!this.isPageComplete())
				throw new IllegalStateException(
						"Cannot get the latexifier when the page is not complete");
			setLatexifierOptions();
			return latexifier;
		}

		public boolean canFlipToNextPage() {
			if (super.canFlipToNextPage())
				if (isControlCreated())
					return showPreviewButton.getSelection();
				else
					return true;
			return false;
		}

		/**
		 * Set the options according to the parameters given by the user. They
		 * have been successfully validated already. The latexifier and the
		 * dialog settings will be updated and the page settings refreshed
		 */
		private void setLatexifierOptions() {
			/* -- */
			boolean setSetsOnSeparateLines = this.setsOnSeparateLinesButton
					.getSelection();
			latexifier.setSetsOnSeparateLines(setSetsOnSeparateLines);
			settings.put(SEPARATE_LINES_BUTTON, setSetsOnSeparateLines);
			if (setSetsOnSeparateLines) {
				String label = this.separateLinesName.getText().trim();
				latexifier.setLabel(label);
				settings.put(SEPARATE_LINES_NAME, label);
			}

			/* -- */
			double extraSpace;
			if (this.allowExtraSpaceButton.getSelection()) {
				extraSpace = Double.parseDouble(allowExtraSpaceText.getText());
				settings
						.put(ALLOW_EXTRA_SPACE_TEXT, String.valueOf(extraSpace));
			} else
				extraSpace = 0;
			latexifier.setExtraSpace(extraSpace);
			settings.put(ALLOW_EXTRA_SPACE_BUTTON, this.allowExtraSpaceButton
					.getSelection());

			/* -- */
			settings.put(SHOW_PREVIEW_BUTTON, this.showPreviewButton
					.getSelection());
		}

		private GridData createDefaultGridData() {
			/* ...with grabbing horizontal space */
			return new GridData(SWT.FILL, SWT.CENTER, true, false);
		}

	}

	/**
	 * Page displaying a read-only editor to show how the snippet looks like
	 * 
	 * @author mtribast
	 * 
	 */
	private class PreviewPage extends WizardPage {

		private Text snippetText;

		protected PreviewPage(String pageName) {
			super(pageName);
			this.setTitle("Preview");
			this
					.setDescription("You can copy and paste this snippet into your LaTeX source file");
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new FillLayout());
			setControl(composite);
			snippetText = new Text(composite, SWT.MULTI | SWT.H_SCROLL
					| SWT.V_SCROLL);
			snippetText.setText(settingPage.getLatexifier().getLatexSource());

		}

		public void setVisible(boolean visible) {
			super.setVisible(visible);
			if (visible)
				snippetText.setText(settingPage.getLatexifier()
						.getLatexSource());
		}
	}

	private IPepaModel model;

	private WizardNewFileCreationPage newFilePage;

	private WizardPage previewPage;

	private SettingPage settingPage;

	/**
	 * The underlying resource for this model.
	 * 
	 * @param modelResource
	 *            the underlying resource for this model
	 * @throws NullPointerException
	 *             if model in <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the model's AST is not correct
	 */
	public LatexifierWizard(IPepaModel model) {
		if (model == null)
			throw new NullPointerException();
		if (!model.isDerivable())
			throw new IllegalArgumentException("The model is not derivable");
		this.model = model;
		this.setForcePreviousAndNextButtons(true);
		this.setNeedsProgressMonitor(true);

	}

	public void createPageControls(Composite parent) {
		super.createPageControls(parent);
		// this.getShell().setSize(400, 400);
	}

	public void addPages() {
		addSaveAsPage();
		settingPage = new SettingPage("PEPA2Latex Settings");
		addPage(settingPage);
		previewPage = new PreviewPage("PreviewPage");
		addPage(previewPage);

	}

	private void addSaveAsPage() {
		IFile handle = ResourcesPlugin.getWorkspace().getRoot().getFile(
				ResourceUtilities.changeExtension(
						model.getUnderlyingResource(), EXTENSION));

		newFilePage = new SaveAsPage("newFilePage", new StructuredSelection(
				handle), EXTENSION);
		newFilePage.setTitle("Latexify");
		newFilePage.setDescription("Save LaTeX snippet to");
		newFilePage.setFileName(handle.getName());
		this.addPage(newFilePage);
	}

	@Override
	public boolean performFinish() {
		String source = settingPage.getLatexifier().getLatexSource();
		byte currentBytes[] = source.getBytes();
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				currentBytes);
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						IFile file = newFilePage.createNewFile();
						file.setContents(byteArrayInputStream, true, false,
								monitor);
						/* TIP RFRS 3.5.6 -- Open file in editor */
						org.eclipse.ui.ide.IDE.openEditor(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage(), file);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}

				}
			});
		} catch (InvocationTargetException e) {
			PepaLog.logError(e);
			MessageDialog.openError(this.getShell(),
					"Error while creating resource", e.getCause().getMessage());
			return false;
		} catch (InterruptedException e) {
			PepaLog.logError(e);
			return false;
		}
		return true;
	}
}
