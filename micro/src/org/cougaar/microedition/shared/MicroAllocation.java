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

/**
 * Describes the disposition of a MicroTask to an Asset.
 */
public class MicroAllocation implements Encodable {

  public MicroAllocation(MicroAsset asset, MicroTask task) {
    setAsset(asset);
    setTask(task);
    task.setAllocation(this);
  }
  private cougaar.microedition.shared.MicroAllocationResult reportedResult;

  /**
   * Get the results (status) of this allocation.
   */
  public cougaar.microedition.shared.MicroAllocationResult getReportedResult() {
    return reportedResult;
  }

  /**
   * Set the results (status) of this allocation.
   */
  public void setReportedResult(cougaar.microedition.shared.MicroAllocationResult newReportedResult) {
    reportedResult = newReportedResult;
  }

  protected static String tag = "MicroAllocation";
  private cougaar.microedition.shared.MicroAsset asset;
  private cougaar.microedition.shared.MicroTask task;
  /**
   * XML encode this object and all sub-objects.
   */
  public void encode(StringBuffer str) {
    str.append("<");
    str.append(tag);
    str.append(">");
    if (getReportedResult() != null)
      getReportedResult().encode(str);
    str.append("</");
    str.append(tag);
    str.append(">");
  }

  /**
   * Set the asset associated with this allocation.
   */
  public void setAsset(cougaar.microedition.shared.MicroAsset newAsset) {
    asset = newAsset;
  }
  /**
   * Get the asset associated with this allocation.
   */
  public cougaar.microedition.shared.MicroAsset getAsset() {
    return asset;
  }

  /**
   * Set the MicroTask associated with this allocation.
   */
  public void setTask(cougaar.microedition.shared.MicroTask newTask) {
    task = newTask;
  }
  /**
   * Get the MicroTask associated with this allocation.
   */
  public cougaar.microedition.shared.MicroTask getTask() {
    return task;
  }
}