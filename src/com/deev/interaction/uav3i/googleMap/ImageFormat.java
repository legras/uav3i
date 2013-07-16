package com.deev.interaction.uav3i.googleMap;
/**
 * @author Philippe TANGUY
 */
public enum ImageFormat
{
  //-----------------------------------------------------------------------------
  PNG8("png8"),
  PNG32("png32"),
  GIF("gif"),
  JPG("jpg"),
  JPG_BASELINE("jpg-baseline");
  //-----------------------------------------------------------------------------
  public final String value;
  //-----------------------------------------------------------------------------
  ImageFormat(String value)
  {
    this.value = value;
  }
  //-----------------------------------------------------------------------------
  public String getFileExtension()
  {
    String ext = null;
    switch (this)
    {
      case PNG8:
      case PNG32:
        ext = "png";
      case JPG:
      case JPG_BASELINE:
        ext = "jpg";
      case GIF:
        ext = "gif";
    }
    return ext;
  }
  //-----------------------------------------------------------------------------
}
