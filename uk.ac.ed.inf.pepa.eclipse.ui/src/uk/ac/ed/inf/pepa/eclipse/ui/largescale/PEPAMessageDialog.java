package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class PEPAMessageDialog extends MessageDialog {

	public PEPAMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
	}
	
    /**
     * Convenience method to open a standard information dialog.
     *
     * @param parent
     *            the parent shell of the dialog, or <code>null</code> if none
     * @param title
     *            the dialog's title, or <code>null</code> if none
     * @param message
     *            the message
     */
    public static void openInformation(Shell parent, String title,
            String message) {
        openPEPA(INFORMATION, parent, title, message, SWT.NONE);
    }
    
	/**
	 * Convenience method to open a simple dialog as specified by the
	 * <code>kind</code> flag.
	 *
	 * @param kind
	 *            the kind of dialog to open, one of {@link #ERROR},
	 *            {@link #INFORMATION}, {@link #QUESTION}, {@link #WARNING},
	 *            {@link #CONFIRM}, or {@link #QUESTION_WITH_CANCEL}.
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 * @param style
	 *            {@link SWT#NONE} for a default dialog, or {@link SWT#SHEET} for
	 *            a dialog with sheet behavior
	 * @return <code>true</code> if the user presses the OK or Yes button,
	 *         <code>false</code> otherwise
	 * @since 3.5
	 */
	public static boolean openPEPA(int kind, Shell parent, String title,
			String message, int style) {
		PEPAMessageDialog dialog = new PEPAMessageDialog(parent, title, null, message,
				kind, getButtonLabels(kind), 0);
		style &= SWT.SHEET;
		dialog.setShellStyle(dialog.getShellStyle() | style);
		return dialog.open() == 0;
	}
	
	static String[] getButtonLabels(int kind) {
		String[] dialogButtonLabels;
		switch (kind) {
		case ERROR:
		case INFORMATION:
		case WARNING: {
			dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
			break;
		}
		case CONFIRM: {
			dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL,
					IDialogConstants.CANCEL_LABEL };
			break;
		}
		case QUESTION: {
			dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL,
					IDialogConstants.NO_LABEL };
			break;
		}
		case QUESTION_WITH_CANCEL: {
			dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL,
                    IDialogConstants.NO_LABEL,
                    IDialogConstants.CANCEL_LABEL };
			break;
		}
		default: {
			throw new IllegalArgumentException(
					"Illegal value for kind in MessageDialog.open()"); //$NON-NLS-1$
		}
		}
		return dialogButtonLabels;
	}
	
    /**
     * This implementation of the <code>Dialog</code> framework method creates
     * and lays out a composite and calls <code>createMessageArea</code> and
     * <code>createCustomArea</code> to populate it. Subclasses should
     * override <code>createCustomArea</code> to add contents below the
     * message.
     */
    @Override
	protected Control createDialogArea(Composite parent) {
    	
    	ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL );
    	final Composite child = new Composite(sc, SWT.NONE);
    	sc.setContent(child);
    	

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		child.setLayoutData(data);
		GridLayout layout = new GridLayout(16,false);
		child.setLayout(layout);
    	
		Label label = new Label(child, SWT.SINGLE | SWT.LEFT);
		label.setText(message);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		label.setLayoutData(data);
        
		Point size = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		sc.setMinSize(size);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		sc.setLayoutData(data);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
        
        return sc;
    }

}
