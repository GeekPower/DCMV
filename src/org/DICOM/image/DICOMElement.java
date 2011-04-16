package org.DICOM.image;
import java.util.Hashtable;


public class DICOMElement implements Comparable {
  Object value = null;
  int tag, vr = 0x4F58, vm = 1;
  long length;

  public int compareTo(Object o) {
    if(o instanceof DICOMElement) {
      if(((DICOMElement)o).getTag() == this.getTag()) return 0;
      if(((DICOMElement)o).getTag() < this.getTag()) return 1;
      if(((DICOMElement)o).getTag() > this.getTag()) return -1;
      return 0;
    } else {
      return 0;
    }
  }

  public DICOMElement(int tag, int vr, int vm, long length, Object value) {
    this.tag = tag;
    this.vr = vr;
    this.vm = vm;
    this.length = length;
    this.value = value; // fix - get a new copy of the data somehow
  }

  public long getLength() {
    return length;
  }

  public int getVR() {
    return vr;
  }

  public int getVM() {
    return vm;
  }

  public int getTag() {
    return tag;
  }

  /**
   *  Needs to by typecasted.
   */

  public Object getValue() {
    return value;
  }

  public void setValue(Object o) {
    value = o;
  }
  
  /**
   * Utility method to get value from a DICOM hashtable
   */

  public static Object getValueFromHash(Hashtable ht, int tag) {
     DICOMElement de = (DICOMElement)ht.get(Integer.toHexString(tag));
     if(de == null) return null; else return de.getValue();
  }
}
