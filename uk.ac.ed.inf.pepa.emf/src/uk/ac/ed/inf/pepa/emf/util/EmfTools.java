/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.emf.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import uk.ac.ed.inf.pepa.emf.Model;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

/**
 * Services provided by the EMF plug-in are represented as static methods of
 * this class. Two kinds of services are supported: in-memory model
 * transformation and serialisation. The former is accomplished by
 * {@link #convertToAST(Model)} and
 * {@link #convertToEmfModel(uk.ac.ed.inf.pepa.parsing.ModelNode)} whereas the
 * latter is obtained via {@link #serialise(Model, File)} and
 * {@link #deserialise(File)}.
 * <p>
 * With regards to serialisation, two different representations of file can be
 * passed. Clients should use <code>IFile</code> when dealing with resources
 * within the Eclipse workbench and <code>java.io.File</code> for external
 * resources. In fact, when {@link #serialise(Model, IFile)} is called, the
 * Eclipse Resources Plugin is informed and the workbench is updated
 * automatically. If {@link #deserialise(File)} on workbench resources, that
 * file has to be updated manually by the user.
 * 
 * @author mtribast
 * 
 */
public class EmfTools {

	/**
	 * Provide an EMF model instance of the given PEPA model.
	 * <p>
	 * Instances of <code>uk.ac.ed.inf.pepa.model.Model</code> can be obtained
	 * by compiling the model. This process follows the creation of the model's
	 * AST and static analysis.
	 * 
	 * @param model
	 *            the model for which the EMF representation is requested
	 * @return the ECore instance for this model
	 * @throws EmfSupportException
	 */
	public static Model convertToEmfModel(ModelNode model)
			throws EmfSupportException {
		if (model == null)
			throw new EmfSupportException("Model invalid",
					new NullPointerException());
		return new EmfExporter(model).convert();
	}

	/**
	 * Creates an AST model from a EMF in-memory model representation
	 * 
	 * @param emfModel
	 *            the EMF model instance of a PEPA model
	 * @return the AST representation
	 * @throws EmfSupportException
	 */
	public static ModelNode convertToAST(Model emfModel)
			throws EmfSupportException {
		if (emfModel == null)
			throw new EmfSupportException("Model invalid",
					new NullPointerException());
		return new EmfImporter(emfModel).convert();
	}

	/**
	 * Write the model to a XML file. The file is within the workspace, hence
	 * the workspace gets updated when the file is created.
	 * <p>
	 * If the file exists already, then it is overwritten without warning
	 * 
	 * @param model
	 * @param outputFile
	 */
	public static void serialise(Model model, IFile outputFile)
			throws EmfSupportException {

		serialise(model, uriFromIFile(outputFile));
	}

	/**
	 * Write the model to a XML file. The file can be outside the workspace. If
	 * this method is called from an Eclipse plug-in, the workspace is not
	 * notified of the event for this new file. If a notification is neeeded,
	 * then the serialise(Model, IFile) method has to be used instead.
	 * <p>
	 * If the file exists already, then it is overwritten without warning.
	 * 
	 * @param model
	 *            the model to serialise
	 * @param outputFile
	 *            the output file
	 * @throws IOException
	 */
	public static void serialise(Model model, File outputFile)
			throws EmfSupportException {

		serialise(model, uriFromFile(outputFile));

	}

	private static URI uriFromFile(File file) throws EmfSupportException {
		URI uri = null;
		try {
			uri = URI.createFileURI(file.getAbsolutePath());
		} catch (IllegalArgumentException e) {
			throw new EmfSupportException(e.getMessage(), e);
		}
		return uri;
	}

	private static URI uriFromIFile(IFile file) throws EmfSupportException {
		URI uri = null;
		try {
			uri = URI.createPlatformResourceURI(file.getFullPath().toString());
		} catch (IllegalArgumentException e) {
			throw new EmfSupportException(e.getMessage(), e);
		}
		return uri;
	}

	private static void serialise(Model model, URI uri)
			throws EmfSupportException {
		Resource resource = new org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl(
				uri);
		resource.getContents().add(model);
		try {
			resource.save(null);
		} catch (IOException e) {
			throw new EmfSupportException(e.getMessage(), e);
		}
	}

	private static Model deserialise(URI uri) throws EmfSupportException {
		Resource resource = new org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl(
				uri);
		try {

			resource.load(null);

		} catch (IOException e) {
			throw new EmfSupportException(e.getMessage(), e);
		}
		if (resource.getContents() != null
				&& resource.getContents().size() != 0) {
			Object model = resource.getContents().get(0);
			if (model instanceof Model)
				return (Model) model;
		}
		throw new EmfSupportException("The root object is not a model", null);
	}

	/**
	 * Load a model from file
	 * 
	 * @param inputFile
	 *            the file containg the model
	 * @return the loaded model
	 */
	public static Model deserialise(IFile inputFile) throws EmfSupportException {
		return deserialise(uriFromIFile(inputFile));
	}

	public static Model deserialise(File inputFile) throws EmfSupportException {
		return deserialise(uriFromFile(inputFile));
	}

	private static void printAttributeValues(EObject object) {
		EClass eClass = object.eClass();
		System.out.println("Visiting: " + eClass.getName());
		for (Iterator iter = eClass.getEAllAttributes().iterator(); iter
				.hasNext();) {
			EAttribute attribute = (EAttribute) iter.next();
			Object value = object.eGet(attribute);
			System.out.print("attribute " + attribute.getName() + " : ");
			if (object.eIsSet(attribute))
				System.out.println(value);
			else
				System.out.println(value + " default");
		}
	}

	private static void walkModel(Model model) {
		EmfSwitch visitor = new EmfSwitch() {

			public Object defaultCase(EObject object) {
				printAttributeValues(object);
				return object;
			}

		};

		for (Iterator iter = EcoreUtil.getAllContents(Collections
				.singleton(model)); iter.hasNext();) {
			EObject eObject = (EObject) iter.next();
			visitor.doSwitch(eObject);
		}

	}

}
