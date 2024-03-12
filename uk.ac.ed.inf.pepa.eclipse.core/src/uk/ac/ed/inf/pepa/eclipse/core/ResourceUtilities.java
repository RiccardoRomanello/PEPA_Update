/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import uk.ac.ed.inf.pepa.eclipse.core.internal.OptionHandler;

public class ResourceUtilities {

	/**
	 * Creates an option handler for the given resource. This is responsible of
	 * making the managed option map persistent across workbench sessions.
	 * 
	 * @param resource
	 * @return
	 */
	public static IOptionHandler createOptionHandler(IResource resource) {
		return new OptionHandler(resource);
	}

	/**
	 * Generates, if doesn't exist, the path for the given file. The method
	 * recursively creates the provided directories. It works with resources
	 * held by a project as well as the workspace root
	 * 
	 * @param file
	 *            whose path has to be created
	 * @throws CoreException
	 */
	public static void createPathForFile(IFile file) throws CoreException {
		IProject project = file.getProject();
		IPath path = file.getProjectRelativePath();
		IPath copy = Path.EMPTY;
		IFolder folder;
		for (int i = 0; i < path.segmentCount() - 1; i++) {
			copy = copy.append(path.segment(i));
			if (project != null) {
				folder = project.getFolder(copy);
			} else {
				folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(
						copy);
			}
			if (!folder.exists())
				folder.create(true, true, null);
		}
	}

	/**
	 * Where the actual job of converting takes place. It involves managing file
	 * handles, opening output and input streams, closing them gracefully, and
	 * so forth.
	 * 
	 * @param path
	 *            the string determining the file location
	 * @param content
	 *            the content of the file
	 * @throws IOException
	 * @throws CoreException
	 */
	public static void generate(String path, String content,
			IProgressMonitor monitor) throws IOException, CoreException {

		IFile file = getIFileFromText(path);

		if (monitor != null)
			monitor.beginTask("Generating " + file.getFullPath(), 3);

		InputStream source = new ByteArrayInputStream(content.getBytes());
		if (file.exists() == true) {
			file.setContents(source, true, false, null);
			if (monitor != null)
				monitor.worked(2);
		} else {

			ResourceUtilities.createPathForFile(file);
			if (monitor != null)
				monitor.worked(1);
			file.create(source, true, null);
			if (monitor != null)
				monitor.worked(1);
		}
		file.refreshLocal(0, null);

		if (monitor != null)
			monitor.done();
	}

	/**
	 * Useful when applied to text boxes
	 * 
	 * @param text
	 *            the absolute (relative to the workspace root) path
	 * @return the <code>IFile</code> representing that file, or
	 *         <code>null</code> if no file can be given
	 */
	public static IFile getIFileFromText(String text) {
		IPath path = new Path(text);
		if (path.segmentCount() > 1)
			/* The path must have at least two segments */
			return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		else
			return null;
	}

	/**
	 * Create the path of the given resource with the new extension. If the new
	 * extension is an empty string, no separating dot is added. This way,
	 * calling this method is equal to determining the path of the file with no
	 * extension
	 * 
	 * @param resource
	 * @param newExtension
	 *            extension <b>without</b> the separating dot
	 * @return the new Path, on <code>null</code> if no path can be determined
	 */
	public static IPath changeExtension(IResource resource, String newExtension) {
		if (resource == null || newExtension == null)
			return null;

		IPath path = resource.getFullPath().removeFileExtension();
		String filename = path.lastSegment(); // file name without extension
		path = path.removeLastSegments(1);
		path = path.makeRelative();
		if (newExtension.equals(""))
			return path.append(filename);
		else
			return path.append(filename + "." + newExtension);

	}

}
