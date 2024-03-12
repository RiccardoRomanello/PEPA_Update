package org.systemsbiology.util;
/*
 * Copyright (C) 2003 by Institute for Systems Biology,
 * Seattle, Washington, USA.  All rights reserved.
 * 
 * This source code is distributed under the GNU Lesser 
 * General Public License, the text of which is available at:
 *   http://www.gnu.org/copyleft/lesser.html
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class for manipulating file names 
 *
 * @author Stephen Ramsey
 */
public class FileUtils
{
	public static void fileCopy(String fromPath, String toPath) throws IOException {
	    File inputFile = new File(fromPath);
	    File outputFile = new File(toPath);

	    FileReader in = new FileReader(inputFile);
	    FileWriter out = new FileWriter(outputFile);
	    int c;

	    while ((c = in.read()) != -1)
	      out.write(c);

	    in.close();
	    out.close();
	}		
	
    public static String getFileName(String pathToFile) {
    	int fileNameStartPos = pathToFile.lastIndexOf("/");
    	if (fileNameStartPos == -1) {
    		fileNameStartPos = pathToFile.lastIndexOf(String.valueOf((char)92));
    	}
    	String fileName = pathToFile.substring(fileNameStartPos);
    	return fileName;
    }
	
	public static String addSuffixToFilename(String pFileName, String pSuffix)
    {
        int lastIndex = pFileName.lastIndexOf('.');
        String retVal = null;
        if(-1 != lastIndex)
        {
            retVal = pFileName.substring(0, lastIndex) + pSuffix;
        }
        else
        {
            retVal = pFileName + pSuffix;
        }
        return retVal;
    }
    
    public static String getExtension(String pFileName)
    {
        int lastIndex = pFileName.lastIndexOf('.');
        String retExtension = null;
        if(-1 != lastIndex)
        {
            retExtension = pFileName.substring(lastIndex+1, pFileName.length());
        }
        return(retExtension);
    }

    public static String removeExtension(String pFileName)
    {
        int lastIndex = pFileName.lastIndexOf('.');
        String retFileName = pFileName;
        if(-1 != lastIndex)
        {
            retFileName = pFileName.substring(0, lastIndex);
        }
        return(retFileName);
    }
    
    /**
     * The Windows command-line sometimes passes a file name
     * terminating in a double-quote character.  This method
     * strips out the double-quote character and appends a
     * directory separator character, if necessary.
     */
    public static String fixWindowsCommandLineDirectoryNameMangling(String pDirName)
    {
        String retString = pDirName;
        if(retString.endsWith("\""))
        {
            retString = retString.substring(0, retString.length() - 1);
            if(! retString.endsWith(File.separator) &&
               ! retString.endsWith("\\") &&
               ! retString.endsWith("/"))
            {
                retString += File.separator;
            }
        }
        return(retString);
    }
    
    public static String createFileURL(File pFile)
    {
        String fileName = pFile.getAbsolutePath();

        // for non-Unix operating systems, convert path separator first
        fileName = fileName.replace(File.separatorChar, '/');

        if(fileName.charAt(0) != '/')
        {
            fileName = "/" + fileName;
        }

        fileName = fileName.replaceAll("%", "%25");
        fileName = fileName.replaceAll(" ", "%20");
        fileName = fileName.replaceAll("\\:", "%3A");
        fileName = fileName.replaceAll("#", "%23");
        fileName = fileName.replaceAll("\\$", "%24");
        fileName = fileName.replaceAll("&", "%26");
        fileName = fileName.replaceAll("\\?", "%3F");
        fileName = fileName.replaceAll("@", "%40");
        fileName = fileName.replaceAll(";", "%3B");
        fileName = fileName.replaceAll(",", "%2C");
        fileName = fileName.replaceAll("\\+", "%2C");
        fileName = fileName.replaceAll("<", "%3C");
        fileName = fileName.replaceAll(">", "%3E");
        fileName = fileName.replaceAll("\"", "%34");

        return("file:" + fileName);
    }
}
