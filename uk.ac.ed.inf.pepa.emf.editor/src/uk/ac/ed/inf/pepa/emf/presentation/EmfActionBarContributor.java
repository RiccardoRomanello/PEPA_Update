/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.presentation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.ui.action.ControlAction;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.action.LoadResourceAction;
import org.eclipse.emf.edit.ui.action.ValidateAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaCore;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.core.ResourceUtilities;
import uk.ac.ed.inf.pepa.eclipse.ui.actions.ActionCommands;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;
import uk.ac.ed.inf.pepa.parsing.ASTSupport;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

/**
 * This is the action bar contributor for the Emf model editor. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class EmfActionBarContributor extends EditingDomainActionBarContributor
		implements ISelectionChangedListener {
	/**
	 * This keeps track of the active editor.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected IEditorPart activeEditorPart;

	/**
	 * This keeps track of the current selection provider.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ISelectionProvider selectionProvider;

	/**
	 * Action to convert to plain text
	 */
	protected IAction convertToTextAction = new Action(
			"Convert To PEPA...") {

		public boolean isEnabled() {
			if (activeEditorPart instanceof IProcessAlgebraEditor) {
				IPepaModel model = (IPepaModel) ((IProcessAlgebraEditor) activeEditorPart)
						.getProcessAlgebraModel();
				return model.isDerivable();
			}
			return false;
		}

		public void run() {
			IPepaModel model;
			Shell shell = activeEditorPart.getEditorSite().getShell();

			if (activeEditorPart instanceof IProcessAlgebraEditor) {
				model = (IPepaModel) ((IProcessAlgebraEditor) activeEditorPart).getProcessAlgebraModel();
			} else {
				MessageDialog.openError(shell, "Model Error", "No model");
				return;
			}

			IResource resource = model.getUnderlyingResource();
			if (resource.getType() != IResource.FILE) {

				MessageDialog.openError(shell, "Resource Error",
						"No underlying resource for this model");
				return;
			}
			SaveAsDialog dialog = new SaveAsDialog(shell);
			/* create a path for the pepa file */
			IPath outputFile = ResourceUtilities.changeExtension(
					(IFile) resource, PepaCore.PEPA_EXTENSION);

			dialog.setOriginalFile(ResourcesPlugin.getWorkspace().getRoot()
					.getFile(outputFile));
			dialog.open();
			IPath path = dialog.getResult();
			if (path == null) {
				// cancel was selected
				return;
			}
			ModelNode modelNode = model.getAST();
			if (modelNode == null)
				return;
			String sourceCode = ASTSupport.toString(modelNode);

			try {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
						path);
				ResourceUtilities.generate(path.toPortableString(), sourceCode,
						null);

				/* TIP RFRS 3.5.6 -- Open file in editor */
				org.eclipse.ui.ide.IDE.openEditor(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage(), file);
			} catch (Throwable t) {
				PepaLog.logError(t);
				MessageDialog.openError(shell, "Error while creating resource",
						"An exception was thrown while creating the resource:\n"
								+ "Reason:\n" + t.getMessage());

			}

		}
	};

	/**
	 * Action for state space derivation -Mirco
	 */
	protected IAction deriveAction = new Action("Derive...") {

		public boolean isEnabled() {
			if (activeEditorPart instanceof IProcessAlgebraEditor) {
				IPepaModel model = (IPepaModel) ((IProcessAlgebraEditor) activeEditorPart)
						.getProcessAlgebraModel();
				return model.isDerivable();
			}
			return false;
		}

		public void run() {
			if (activeEditorPart instanceof IProcessAlgebraEditor) {
				IPepaModel model = (IPepaModel) ((IProcessAlgebraEditor) activeEditorPart)
						.getProcessAlgebraModel();
				ActionCommands.derive(model);
			}
		}
	};

	/**
	 * Action for steady-state analysis of the underlying CTMC
	 */
	protected IAction ctmcAction = new Action("Steady State Analysis...") {

		public boolean isEnabled() {
			if (activeEditorPart instanceof IProcessAlgebraEditor) {
				IPepaModel model = (IPepaModel) ((IProcessAlgebraEditor) activeEditorPart)
						.getProcessAlgebraModel();
				return model.isSolvable();
			}
			return false;
		}

		public void run() {
			if (activeEditorPart instanceof IProcessAlgebraEditor) {
				IPepaModel model = (IPepaModel) ((IProcessAlgebraEditor) activeEditorPart)
						.getProcessAlgebraModel();
				ActionCommands.steadyState(model);
			}
		}

	};

	/**
	 * Action for experimentation
	 */
	protected IAction experimentationAction = new Action("Experimentation...") {

		public boolean isEnabled() {
			if (activeEditorPart instanceof IProcessAlgebraEditor) {
				IPepaModel model = (IPepaModel) ((IProcessAlgebraEditor) activeEditorPart)
						.getProcessAlgebraModel();
				return model.isDerivable();
			}
			return false;
		}

		public void run() {
			if (activeEditorPart instanceof IProcessAlgebraEditor) {
				IPepaModel model = (IPepaModel) ((IProcessAlgebraEditor) activeEditorPart)
						.getProcessAlgebraModel();
				ActionCommands.experiment(model);
			}
		}

	};

	/**
	 * Debug action for showing the editing domain -Mirco
	 */
	protected IAction showEditingDomainAction = new Action(
			"Show Editing Domain (DEBUG)") {
		public void run() {
			if (activeEditorPart instanceof IEditingDomainProvider) {
				IEditingDomainProvider provider = (IEditingDomainProvider) activeEditorPart;
				EditingDomain domain = provider.getEditingDomain();
				int no = domain.getResourceSet().getResources().size();
				StringBuffer info = new StringBuffer();
				info.append("Number of resources: " + no + "\n");
				for (int i = 0; i < no; i++) {
					Resource res = (Resource) domain.getResourceSet()
							.getResources().get(i);
					URI uri = res.getURI();
					String complete = uri.path();
					String newString = complete.substring(10);
					IPath pluginPath = new Path(newString);
					IFile file = ResourcesPlugin.getWorkspace().getRoot()
							.getFile(pluginPath);
					if (file != null)
						info.append("File exists: "
								+ file.getFullPath().toOSString() + "\n");

				}
				MessageDialog.openInformation(activeEditorPart.getSite()
						.getShell(), "Editing Domain", info.toString());
			}
		}
	};

	/**
	 * This action opens the Properties view.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected IAction showPropertiesViewAction =
		new Action(PepaEditorPlugin.INSTANCE.getString("_UI_ShowPropertiesView_menu_item")) {
			public void run() {
				try {
					getPage().showView("org.eclipse.ui.views.PropertySheet");
				}
				catch (PartInitException exception) {
					PepaEditorPlugin.INSTANCE.log(exception);
				}
			}
		};

	/**
	 * This action refreshes the viewer of the current editor if the editor
	 * implements {@link org.eclipse.emf.common.ui.viewer.IViewerProvider}.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected IAction refreshViewerAction =
		new Action(PepaEditorPlugin.INSTANCE.getString("_UI_RefreshViewer_menu_item")) {
			public boolean isEnabled() {
				return activeEditorPart instanceof IViewerProvider;
			}

			public void run() {
				if (activeEditorPart instanceof IViewerProvider) {
					Viewer viewer = ((IViewerProvider)activeEditorPart).getViewer();
					if (viewer != null) {
						viewer.refresh();
					}
				}
			}
		};

	/**
	 * This creates an instance of the contributor.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	public EmfActionBarContributor() {
		super(ADDITIONS_LAST_STYLE);
		loadResourceAction = new LoadResourceAction();
		validateAction = new ValidateAction();
		controlAction = new ControlAction();
	}

	/**
	 * This adds Separators for editor additions to the tool bar. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator("emf-settings"));
		toolBarManager.add(new Separator("emf-additions"));
	}

	/**
	 * This adds to the menu bar a menu and some separators for editor
	 * additions, as well as the sub-menus for object creation items. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);

		IMenuManager submenuManager = new MenuManager(PepaEditorPlugin.INSTANCE.getString("_UI_EmfEditor_menu"), "uk.ac.ed.inf.pepa.emfMenuID");
		menuManager.insertAfter("additions", submenuManager);
		submenuManager.add(new Separator("settings"));
		submenuManager.add(new Separator("actions"));
		submenuManager.add(new Separator("additions"));
		submenuManager.add(new Separator("additions-end"));

		// Add your contributions.
		// Ensure that you remove @generated or mark it @generated NOT

		addGlobalActions(submenuManager);
	}

	/**
	 * When the active editor changes, this remembers the change and registers with it as a selection provider.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		activeEditorPart = part;

		// Switch to the new selection provider.
		//
		if (selectionProvider != null) {
			selectionProvider.removeSelectionChangedListener(this);
		}
		if (part == null) {
			selectionProvider = null;
		}
		else {
			selectionProvider = part.getSite().getSelectionProvider();
			selectionProvider.addSelectionChangedListener(this);

			// Fake a selection changed event to update the menus.
			//
			if (selectionProvider.getSelection() != null) {
				selectionChanged(new SelectionChangedEvent(selectionProvider, selectionProvider.getSelection()));
			}
		}
	}

	/**
	 * This implements
	 * {@link org.eclipse.jface.viewers.ISelectionChangedListener}, handling
	 * {@link org.eclipse.jface.viewers.SelectionChangedEvent}s by querying for
	 * the children and siblings that can be added to the selected object and
	 * updating the menus accordingly. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		// Add your contributions.
		// Ensure that you remove @generated or mark it @generated NOT
	}

	/**
	 * This populates the pop-up menu before it appears.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void menuAboutToShow(IMenuManager menuManager) {
		super.menuAboutToShow(menuManager);
	}

	/**
	 * This inserts global actions before the "additions-end" separator. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	protected void addGlobalActions(IMenuManager menuManager) {
		
		menuManager.addMenuListener(new IMenuListener() {
			/* TIP Listener for enabling top-level menu bar actions
			 * This listener is necessary in order to set the actions
			 * of the top-level menu bar. For example, if the model is
			 * derived and the context menu is not invoked, then the
			 * top-level steady-state solution action is not enabled.
			 * The listener waits for events caused by clicking the
			 * top-level bar.
			 * 
			 */
			public void menuAboutToShow(IMenuManager manager) {
				setPepaActionStatus();
			}
			
		});
		
		menuManager.insertAfter("additions-end", new Separator("pepa-actions"));

		menuManager.insertAfter("pepa-actions", new Separator("sep-pepa"));
		
		// PEPA Section
		setPepaActionStatus();
		
		menuManager.insertAfter("sep-pepa", experimentationAction);
		menuManager.insertAfter("sep-pepa", ctmcAction);
		menuManager.insertAfter("sep-pepa", deriveAction);
		menuManager.insertAfter("sep-pepa", convertToTextAction);
		//menuManager.insertAfter("pepa-actions", showEditingDomainAction);
		menuManager.insertAfter("pepa-actions", new Separator("ui-actions"));

		// UI Section
		menuManager.insertAfter("ui-actions", showPropertiesViewAction);
		refreshViewerAction.setEnabled(refreshViewerAction.isEnabled());
		menuManager.insertAfter("ui-actions", refreshViewerAction);

		super.addGlobalActions(menuManager);
	}
	
	private void setPepaActionStatus() {
		experimentationAction.setEnabled(experimentationAction.isEnabled());
		deriveAction.setEnabled(deriveAction.isEnabled());
		ctmcAction.setEnabled(ctmcAction.isEnabled());
		convertToTextAction.setEnabled(convertToTextAction.isEnabled());
		
	}

	/**
	 * This ensures that a delete action will clean up all references to deleted objects.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected boolean removeAllReferencesOnDelete() {
		return true;
	}

}
