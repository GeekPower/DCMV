package org.DICOM.image;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class DICOMMetadataFormat extends IIOMetadataFormatImpl
{
	private static DICOMMetadataFormat defaultInstance = new DICOMMetadataFormat();

	private DICOMMetadataFormat()
	{
		super("org.DICOM.image.DICOMMetadata_1.0", CHILD_POLICY_REPEAT);

		addElement("KeywordValuePair", "org.DICOM.image.DICOMMetadata_1.0",
				CHILD_POLICY_EMPTY);

		// addAttribute("KeywordValuePair", "keyword", DATATYPE_INTEGER, true,
		// null);
		addAttribute("KeywordValuePair", "keyword", DATATYPE_STRING, true, null);
		addAttribute("KeywordValuePair", "value", DATATYPE_STRING, true, null);
	}

	public boolean canNodeAppear(String elementName,
			ImageTypeSpecifier imageType)
	{
		return elementName.equals("KeywordValuePair");
	}

	public static DICOMMetadataFormat getDefaultInstance()
	{
		return defaultInstance;
	}
}
