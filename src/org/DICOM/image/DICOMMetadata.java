package org.DICOM.image;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataNode;
import java.util.Hashtable;

public class DICOMMetadata extends IIOMetadata
{
	static final boolean standardMetadataFormatSupported = false;
	static final String nativeMetadataFormatName = "org.DICOM.image.DICOMMetadata_1.0";
	static final String nativeMetadataFormatClassName = "org.DICOM.image.DICOMMetadata";
	static final String[] extraMetadataFormatNames = null;
	static final String[] extraMetadataFormatClassNames = null;

	Hashtable dh = new Hashtable();

	public DICOMMetadata()
	{
		super(standardMetadataFormatSupported, nativeMetadataFormatName,
				nativeMetadataFormatClassName, extraMetadataFormatNames,
				extraMetadataFormatClassNames);
	}

	public Node getAsTree(String formatName)
	{
		if (!formatName.equals(nativeMetadataFormatName))
			throw new IllegalArgumentException("Bad format name!");

		IIOMetadataNode root = new IIOMetadataNode(nativeMetadataFormatName);
		return root;
	}

	public void add(String key, String value)
	{
		dh.put(key, value);
		// System.out.println("key: " + key + " value: " + value);
	}

	public boolean isReadOnly()
	{
		return false;
	}

	public void reset()
	{
	}

	public void mergeTree(String formatName, Node root)
			throws IIOInvalidTreeException
	{
		if (!formatName.equals(nativeMetadataFormatName))
			throw new IllegalArgumentException("Bad format name!");

		Node node = root;
		if (!node.getNodeName().equals(nativeMetadataFormatName))
			fatal(node, "Root must be " + nativeMetadataFormatName);
	}

	private void fatal(Node node, String reason) throws IIOInvalidTreeException
	{
		throw new IIOInvalidTreeException(reason, node);
	}

}
