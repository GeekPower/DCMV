package org.DICOM.image;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class DICOMImageReaderSpi extends ImageReaderSpi
{
	static final String vendorName = "DicomR2";
	static final String version = "0.11";
	static final String readerClassName = "org.DICOM.image.DICOMImageReader";
	static final String[] names = { "dicom", "dicm", "dcm" };
	static final String[] suffixes = { "dicom", "dicm", "dcm" };
	static final String[] MIMETypes = { "image/dicom", "image/x-dicom" };
	static final String[] writerSpiNames = { "org.DICOM.image.DICOMImageWriterSpi" };

	// metadata stuff
	static final boolean supportsStandardStreamMetadataFormat = false;
	static final String nativeStreamMetadataFormatName = null;
	static final String nativeStreamMetadataFormatClassName = null;
	static final String[] extraStreamMetadataFormatNames = null;
	static final String[] extraStreamMetadataFormatClassNames = null;
	static final boolean supportsStandardImageMetadataFormat = false;
	static final String nativeImageMetadataFormatName = "org.DICOM.image.DICOMMetadata_1.0";
	static final String nativeImageMetadataFormatClassName = "org.DICOM.image.DICOMMetadata";
	static final String[] extraImageMetadataFormatNames = null;
	static final String[] extraImageMetadataFormatClassNames = null;

	public DICOMImageReaderSpi()
	{
		super(vendorName, version, names, suffixes, MIMETypes, readerClassName,
				STANDARD_INPUT_TYPE, writerSpiNames,
				supportsStandardStreamMetadataFormat,
				nativeStreamMetadataFormatName,
				nativeStreamMetadataFormatClassName,
				extraStreamMetadataFormatNames,
				extraStreamMetadataFormatClassNames,
				supportsStandardImageMetadataFormat,
				nativeImageMetadataFormatName,
				nativeImageMetadataFormatClassName,
				extraImageMetadataFormatNames,
				extraImageMetadataFormatClassNames);
	}

	public String getDescription(Locale locale)
	{
		return "DICOM parser";
	}

	public boolean canDecodeInput(Object input) throws IOException
	{
		if (!(input instanceof ImageInputStream))
			return false;
		boolean canRead = false;

		ImageInputStream stream = (ImageInputStream) input;
		stream.mark();
		byte[] b = new byte[132];
		try
		{
			stream.seek(0);
			stream.readFully(b);
		} catch (IOException e)
		{
			e.printStackTrace();
			stream.reset();
			return false;
		}
		if (b[128] == (byte) 'D' && b[129] == (byte) 'I'
				&& b[130] == (byte) 'C' && b[131] == (byte) 'M')
		{
			canRead = true;
		} else
		{ // for non-part 10 dicom files, seems to work
			stream.seek(0);
			int groupWord = stream.readShort();
			int elementWord = stream.readShort();
			int tag = groupWord << 16 | elementWord;
			if ((tag & 0xffff0000) == 0x00080000
					|| (tag & 0xffff0000) == 0x08000000)
				canRead = true;
			else
				canRead = false;
		}
		stream.reset();
		return canRead;
	}

	public ImageReader createReaderInstance(Object extension)
	{
		return new DICOMImageReader(this);
	}
}
