package uk.ac.ed.inf.pepa.jhydra.driver.passagetimesolver;

import java.io.FileWriter;
import java.io.IOException;

public class PassageTimeResults {
	
	private double [] timePoints ;
	private double [] cdfPoints ;
	private double [] pdfPoints ;
	private String cdfName = "cdf";
	private String pdfName = "pdf";
	
	public PassageTimeResults (int numberTimePoints) {
		timePoints = new double [numberTimePoints];
		cdfPoints  = new double [numberTimePoints];
		pdfPoints  = new double [numberTimePoints];
	}
	
	public PassageTimeResults (int numberTimePoints, String cdfName, String pdfName){
		timePoints  = new double [numberTimePoints];
		cdfPoints   = new double [numberTimePoints];
		pdfPoints   = new double [numberTimePoints];
		this.cdfName = cdfName ;
		this.pdfName = pdfName ;
	}
	
	public double[] getTimePoints (){ return timePoints ; }
	public double[] getCdfPoints  () { return cdfPoints ; }
	public double[] getPdfPoints  () { return pdfPoints ; }
    public String getCdfName () { return this.cdfName ; }
    public String getPdfName () { return this.pdfName ; }

    
	public void updateTimePoint (int i, double t, double cdf, double pdf){
		timePoints[i] = t ;
		cdfPoints[i] = cdf ;
		pdfPoints[i] = pdf ;
	}
	
	public int printOutResults(String basename){
        basename = basename.replace(".mod", "");
		
		String pdfResultsFileName = basename.concat(".PDF_RESULTS") ;
		String cdfResultsFileName = basename.concat(".CDF_RESULTS") ;
		try {
			FileWriter pdf_result_output = new FileWriter(pdfResultsFileName);
			FileWriter cdf_result_output = new FileWriter(cdfResultsFileName);

			// Now output the results both to the screen and to the files
			for (int i = 0; i < timePoints.length; i++){
				pdf_result_output.write(timePoints[i] + "    " + pdfPoints[i] + "\n");
				cdf_result_output.write(timePoints[i] + "    " + cdfPoints[i] + "\n");
				System.out.println(timePoints[i] + "    " + pdfPoints[i] +  "    PDF_DATA0");
				System.out.println(timePoints[i] + "    " + cdfPoints[i] +  "    CDF_DATA0");	
			}
            pdf_result_output.close();
			cdf_result_output.close();
		}
		catch (IOException e) {
			System.out.println(e.toString());
			return -1;
		}
		return 0 ;
	}
}
