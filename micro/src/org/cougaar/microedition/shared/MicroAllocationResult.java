/*
 * <copyright>
 * Copyright 1997-2000 Defense Advanced Research Projects Agency (DARPA)
 * and ALPINE (A BBN Technologies (BBN) and Raytheon Systems Company
 * (RSC) Consortium). This software to be used in accordance with the
 * COUGAAR license agreement.  The license agreement and other
 * information on the Cognitive Agent Architecture (COUGAAR) Project can
 * be found at http://www.cougaar.org or email: info@cougaar.org.
 * </copyright>
 */
package cougaar.microedition.shared;

public class MicroAllocationResult implements Encodable {

  public MicroAllocationResult() {
  }

  protected static String tag = "MicroAllocationResult";
  protected static String aspectTag = "MicroAllocationResultAspect";
  private boolean success;
  private double risk;
  private String auxData;
  private double confidenceRating;
  private double[] values = new double[0];
  private int[] aspects = new int[0];
  public void encode(StringBuffer str) {
    str.append("<");
    str.append(tag);
    str.append(" success=\""+success+"\" ");
    str.append("risk=\""+risk+"\" ");
    str.append("confidenceRating=\""+confidenceRating+"\" ");
    str.append("numAspects=\""+aspects.length+"\" ");
    str.append(">");
    for (int i=0; i<aspects.length; i++) {
      str.append("<"+aspectTag+" aspect=\""+aspects[i]+"\" value=\""+values[i]+"\"/>");
    }
    str.append("</");
    str.append(tag);
    str.append(">");
  }

  public void setSuccess(boolean newSuccess) {
    success = newSuccess;
  }

  public boolean isSuccess() {
    return success;
  }

  public double[] getValues() {
    return values;
  }

  public void setValues(double[] newValues) {
    values = newValues;
  }

  public void setRisk(double newRisk) {
    risk = newRisk;
  }

  public double getRisk() {
    return risk;
  }

  public void setAuxData(String newAuxData) {
    auxData = newAuxData;
  }

  public String getAuxData() {
    return auxData;

  }

  public void setConfidenceRating(double newConfidenceRating) {
    confidenceRating = newConfidenceRating;
  }

  public double getConfidenceRating() {
    return confidenceRating;
  }

  public void setAspects(int[] newAspects) {
    aspects = newAspects;
  }

  public int[] getAspects() {
    return aspects;
  }

  public void addAspectValuePair(int aspect, double value) {
    int [] newAspects = new int[aspects.length + 1];
    double [] newValues = new double[aspects.length + 1];

    System.arraycopy(aspects, 0, newAspects, 0, aspects.length);
    System.arraycopy(values, 0, newValues, 0, aspects.length);

    newAspects[aspects.length] = aspect;
    newValues[aspects.length] = value;

    aspects = newAspects;
    values = newValues;

  }

}
