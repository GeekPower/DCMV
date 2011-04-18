package org.DICOM.image;

import java.io.*;
import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.spi.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.*;
import java.nio.ByteOrder;

public class DICOMImageReader extends ImageReader
{
	public static final int TRANSFER_SYNTAX_UID = 0x00020010;
	public static final int NUM_FRAMES = 0x00280008;
	public static final int ROWS = 0x00280010;
	public static final int COLS = 0x00280011;
	public static final int BITS_ALLOCATED = 0x00280100;
	public static final int BITS_STORED = 0x00280101;
	public static final int PIXEL_REPRESENTATION = 0x00280103;
	public static final int SAMPLES_PER_PIXEL = 0x00280002;
	public static final int PHOTOMETRIC_INTERPRETATION = 0x00280004;
	public static final int PLANAR_CONFIGURATION = 0x00280006;
	public static final int PIXEL_PADDING_VALUE = 0x00280120;
	public static final int RED_PALETTE_COLOR_LOOKUP_TABLE_DESCRIPTOR = 0x00281101;
	public static final int GREEN_PALETTE_COLOR_LOOKUP_TABLE_DESCRIPTOR = 0x00281102;
	public static final int BLUE_PALETTE_COLOR_LOOKUP_TABLE_DESCRIPTOR = 0x00281103;
	public static final int RED_PALETTE_COLOR_LOOKUP_TABLE_DATA = 0x00281201;
	public static final int GREEN_PALETTE_COLOR_LOOKUP_TABLE_DATA = 0x00281202;
	public static final int BLUE_PALETTE_COLOR_LOOKUP_TABLE_DATA = 0x00281203;
	public static final int PIXEL_DATA = 0x7FE00010;
	public static final int META_GROUP_LENGTH = 0x00020000;

	private static final int AE = 0x4145, AS = 0x4153, AT = 0x4154,
			CS = 0x4353, DA = 0x4441, DS = 0x4453, DT = 0x4454, FD = 0x4644,
			FL = 0x464C, IS = 0x4953, LO = 0x4C4F, LT = 0x4C54, PN = 0x504E,
			SH = 0x5348, SL = 0x534C, SS = 0x5353, ST = 0x5354, TM = 0x544D,
			UI = 0x5549, UL = 0x554C, US = 0x5553, UT = 0x5554, OB = 0x4F42,
			OW = 0x4F57, SQ = 0x5351, UN = 0x554E, QQ = 0x3F3F, XS = 0x5853,
			OX = 0x4F58;

	DICOMMetadata metadata = null; // fix

	ImageInputStream stream = null;
	int width = 0, height = 0, nImages = 1, fileType;

	boolean gotHeader = false, forceByteOrder = false;

	// variables required for parsing dicom image
	private ByteOrder bo = ByteOrder.BIG_ENDIAN;
	private int planarConfiguration;
	private int vr;
	private long metaGroupLength = -1L, dataSetPosition = -1L,
			imageStartPosition = -1L, metaStart = -1;
	private String transferSyntaxUID;
	private String photometricInterpretation = new String("MONOCHORME2");
	private int bitsStored = 16, bitsAllocated = 16, pixelRepresentation = 1,
			samplesPerPixel = 1, pixelPaddingValue = -2000;
	private int dataType;
	private boolean implicit = false;
	private static Hashtable dh = (new DICOMDictionary()).getDictionary();
	private Hashtable DICOMHashtable = null;
	private byte[] redLUT, greenLUT, blueLUT, alphaLUT;
	private int redLUTsize, greenLUTsize, blueLUTsize, redLUToffset,
			greenLUToffset, blueLUToffset, redBits, greenBits, blueBits,
			lutSize;

	public DICOMImageReader(ImageReaderSpi originatingProvider)
	{
		super(originatingProvider);
	}

	public void setInput(Object input, boolean seekForwardOnly,
			boolean ignoreMetadata)
	{
		super.setInput(input, seekForwardOnly, ignoreMetadata);
		this.stream = (ImageInputStream) input;
		resetStreamSettings();
	}

	public int getNumImages(boolean allowSearch) throws IIOException
	{
		return nImages; // fix for now
	}

	private void checkIndex(int imageIndex)
	{
		if (imageIndex >= nImages)
			throw new IndexOutOfBoundsException("bad index");
	}

	public int getWidth(int imageIndex) throws IIOException
	{
		checkIndex(imageIndex);
		readHeader();
		return width;
	}

	public int getHeight(int imageIndex) throws IIOException
	{
		checkIndex(imageIndex);
		readHeader();
		return height;
	}

	public boolean validVR(int vr)
	{
		return (vr == AE || vr == AS || vr == AT || vr == CS || vr == DA
				|| vr == DS || vr == DT || vr == FD || vr == FL || vr == IS
				|| vr == LO || vr == LT || vr == PN || vr == SH || vr == SL
				|| vr == SS || vr == ST || vr == TM || vr == UI || vr == UL
				|| vr == UT || vr == OB || vr == OW || vr == SQ || vr == UN || vr == QQ);
	}

	public void readHeader() throws IIOException
	{
		if (gotHeader)
		{
			return;
		}

		if (stream == null)
			throw new IllegalStateException("No input stream");

		try
		{

			// try to read metadata if it exists
			bo = ByteOrder.LITTLE_ENDIAN;
			metaStart = 132;
			stream.setByteOrder(bo);
			stream.seek(0);
			stream.mark();
			stream.skipBytes(128);
			byte[] signature = new byte[4];

			stream.readFully(signature);

			if (signature[0] != (byte) 'D' || signature[1] != (byte) 'I'
					|| signature[2] != (byte) 'C' || signature[3] != (byte) 'M')
			{
				stream.seek(0);
				int tag = getNextTag();
				tag = tag & 0xffff0000;
				if (tag != 0x00080000 && tag != 0x08000000)
					throw new IIOException("Bad DICOM signature!");

				if (tag == 0x08000000)
					bo = ByteOrder.BIG_ENDIAN;
				else
					bo = ByteOrder.LITTLE_ENDIAN;
				int vr = (stream.readByte() << 8) + (stream.readByte());
				if (validVR(vr))
					implicit = false;
				else
					implicit = true; // fix could get confused if 0x554c happens
										// to be a length as well

				transferSyntaxUID = null;
				dataSetPosition = 0;
				stream.reset();
				metaStart = 0;
			} else
			{
				// has meta information ie part 10 (usually compliant unless you
				// are @%$*#%$#! efilm
				metaStart = stream.getStreamPosition();
				metaGroupLength = 0;

				while (true)
				{
					int tag = getNextTag();
					if ((tag & 0xffff0000) != 0x00020000
							|| stream.getStreamPosition() > 500)
						break;
					stream.mark();
					int b0 = (stream.readByte() & 0x000000ff);
					int b1 = (stream.readByte() & 0x000000ff);
					int vr = (b0 << 8) + b1;
					switch (vr)
					{
					case OB:
					case OW:
					case SQ:
					case UN:
						metaGroupLength += 12;
						break;
					default:
						metaGroupLength += 8;
						break;
					}
					stream.reset();
					long elementLength = getLength(tag);
					metaGroupLength += elementLength;
					stream.skipBytes(elementLength);
				}

				stream.seek(metaStart);
				dataSetPosition = metaStart + metaGroupLength;
			}

			stream.setByteOrder(bo);
			boolean stop = false;
			DICOMHashtable = new Hashtable();

			while (!stop)
			{
				if (transferSyntaxUID != null
						&& stream.getStreamPosition() == dataSetPosition)
				{
					if (transferSyntaxUID.trim().equals("1.2.840.10008.1.2"))
					{ // little endian implicit
						bo = ByteOrder.LITTLE_ENDIAN;
						stream.setByteOrder(bo);
						implicit = true;
					} else if (transferSyntaxUID.trim().equals(
							"1.2.840.10008.1.2.1"))
					{ // little endian explicit
						bo = ByteOrder.LITTLE_ENDIAN;
						stream.setByteOrder(bo);
						implicit = false;
					} else if (transferSyntaxUID.trim().equals(
							"1.2.840.10008.1.2.2"))
					{ // big endian explicit
						bo = ByteOrder.BIG_ENDIAN;
						stream.setByteOrder(bo);
						implicit = false;
					}
					if (forceByteOrder)
					{
						if (bo == ByteOrder.LITTLE_ENDIAN)
						{
							bo = ByteOrder.BIG_ENDIAN;
						} else
						{
							bo = ByteOrder.LITTLE_ENDIAN;
						}
						stream.setByteOrder(bo);
					}
				}

				// System.out.print("pos: " + stream.getStreamPosition() + " ");
				int tag = getNextTag();
				long elementLength = getLength(tag);
				// System.out.print("tag: " + Integer.toHexString(tag) +
				// " length: " + elementLength + " vr: " +
				// Integer.toHexString(vr));
				// System.out.flush();
				Object temp = null;
				if (tag != PIXEL_DATA)
				{
					temp = getValueAsObject(vr, elementLength);
					int vm = 1;
					if (temp instanceof Collection)
						vm = ((Collection) temp).size();
					if (temp instanceof String)
						if (temp.equals(""))
							temp = null;
					DICOMHashtable.put(Integer.toHexString(tag),
							new DICOMElement(tag, vr, vm, elementLength, temp));
				}
				// System.out.println(" " + temp);

				switch (tag)
				{
				case TRANSFER_SYNTAX_UID:
					// transferSyntaxUID = getString(elementLength);
					transferSyntaxUID = temp.toString();
					if (transferSyntaxUID.indexOf("1.2.4") > -1
							|| transferSyntaxUID.indexOf("1.2.5") > -1)
						throw new IOException(
								"Cannot open compressed DICOM images.\n Transfer Syntax UID = "
										+ transferSyntaxUID);
					break;
				case NUM_FRAMES:
					nImages = Integer.parseInt(temp.toString()
							.replace('+', ' ').trim());
					break;
				case ROWS:
					height = ((Integer) temp).intValue();
					break;
				case COLS:
					width = ((Integer) temp).intValue();
					break;
				case BITS_ALLOCATED:
					bitsAllocated = ((Integer) temp).intValue();
					break;
				case BITS_STORED:
					bitsAllocated = ((Integer) temp).intValue();
					break;
				case PIXEL_REPRESENTATION:
					pixelRepresentation = ((Integer) temp).intValue();
					break;
				case SAMPLES_PER_PIXEL:
					samplesPerPixel = ((Integer) temp).intValue();
					break;
				case PHOTOMETRIC_INTERPRETATION:
					photometricInterpretation = temp.toString();
					break;
				case PLANAR_CONFIGURATION:
					planarConfiguration = ((Integer) temp).intValue();
					break;
				case RED_PALETTE_COLOR_LOOKUP_TABLE_DESCRIPTOR:
					redLUTsize = ((Integer) temp).intValue();
					if (redLUTsize == 0)
						redLUTsize = 65536;
					if (pixelRepresentation == 1)
						redLUToffset = ((Short) temp).shortValue();
					else
						redLUToffset = ((Integer) temp).intValue();
					redBits = ((Integer) temp).intValue();
					break;
				case GREEN_PALETTE_COLOR_LOOKUP_TABLE_DESCRIPTOR:
					greenLUTsize = ((Integer) temp).intValue();
					if (greenLUTsize == 0)
						greenLUTsize = 65536;
					if (pixelRepresentation == 1)
						greenLUToffset = ((Short) temp).shortValue();
					else
						greenLUToffset = ((Integer) temp).intValue();
					greenBits = ((Integer) temp).intValue();
					break;
				case BLUE_PALETTE_COLOR_LOOKUP_TABLE_DESCRIPTOR:
					blueLUTsize = ((Integer) temp).intValue();
					if (blueLUTsize == 0)
						blueLUTsize = 65536;
					if (pixelRepresentation == 1)
						blueLUToffset = ((Short) temp).shortValue();
					else
						blueLUToffset = ((Integer) temp).intValue();
					blueBits = ((Integer) temp).intValue();
					break;
				case RED_PALETTE_COLOR_LOOKUP_TABLE_DATA:
					lutSize = redLUTsize;
					if (lutSize > greenLUTsize)
						lutSize = greenLUTsize;
					if (lutSize > blueLUTsize)
						lutSize = blueLUTsize;
					redLUT = new byte[256];
					alphaLUT = new byte[256];
					for (int i = 0; i < alphaLUT.length; i++)
						alphaLUT[i] = (byte) 127;
					if (redBits == 16)
					{
						for (int i = redLUToffset; i < lutSize; i++)
							redLUT[i] = (byte) (((Integer) temp).intValue() / 256.0);
					} else
					{
						for (int i = redLUToffset; i < lutSize; i++)
							redLUT[i] = (byte) ((Short) temp).shortValue();
					}
					for (int i = lutSize; i < 255; i++)
						redLUT[i] = (byte) 0;
					for (int i = 0; i < redLUToffset; i++)
						redLUT[i] = redLUT[0];
					break;
				case GREEN_PALETTE_COLOR_LOOKUP_TABLE_DATA:
					lutSize = redLUTsize;
					if (lutSize > greenLUTsize)
						lutSize = greenLUTsize;
					if (lutSize > blueLUTsize)
						lutSize = blueLUTsize;
					greenLUT = new byte[256];
					if (greenBits == 16)
					{
						for (int i = greenLUToffset; i < lutSize; i++)
							greenLUT[i] = (byte) (((Integer) temp).intValue() / 256.0);
					} else
					{
						for (int i = greenLUToffset; i < lutSize; i++)
							greenLUT[i] = (byte) ((Short) temp).shortValue();
					}
					for (int i = lutSize; i < 255; i++)
						greenLUT[i] = (byte) 0;
					for (int i = 0; i < greenLUToffset; i++)
						greenLUT[i] = greenLUT[0];
					break;
				case BLUE_PALETTE_COLOR_LOOKUP_TABLE_DATA:
					lutSize = redLUTsize;
					if (lutSize > greenLUTsize)
						lutSize = greenLUTsize;
					if (lutSize > blueLUTsize)
						lutSize = blueLUTsize;
					blueLUT = new byte[256];
					if (blueBits == 16)
					{
						for (int i = blueLUToffset; i < lutSize; i++)
							blueLUT[i] = (byte) (((Integer) temp).intValue() / 256.0);
					} else
					{
						for (int i = blueLUToffset; i < lutSize; i++)
							blueLUT[i] = (byte) ((Short) temp).shortValue();
					}
					for (int i = lutSize; i < 255; i++)
						blueLUT[i] = (byte) 0;
					for (int i = 0; i < blueLUToffset; i++)
						blueLUT[i] = blueLUT[0];
					break;
				case PIXEL_PADDING_VALUE:
					if ((pixelRepresentation == 0 && vr == XS) || vr == US)
						pixelPaddingValue = ((Integer) temp).intValue();
					else
						pixelPaddingValue = ((Short) temp).shortValue();
					break;
				case PIXEL_DATA:
					if (elementLength != 0)
					{
						imageStartPosition = stream.getStreamPosition();
						stop = true;
					}
					break;
				default:
					break;
				}
			}
			// fix put more error checking
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException();
		} catch (InterruptedIOException e)
		{
			gotHeader = false;
			return;
		} catch (Exception e)
		{
			// e.printStackTrace();
			if (!forceByteOrder)
			{
				forceByteOrder = true;
				gotHeader = false;
				readHeader();
			} else
			{
				throw new IIOException("Error parsing header", e.getCause());
			}
		}
		if (width == 0)
			throw new IIOException("Image width == 0");
		if (height == 0)
			throw new IIOException("Image height == 0");
		gotHeader = true;
	}

	private String getString(long length) throws IOException
	{
		byte[] buf = new byte[(int) length];
		int pos = 0;
		while (pos < length)
		{
			int count = stream.read(buf, pos, (int) length - pos);
			pos += count;
		}
		return new String(buf);
	}

	/**
	 * Gets length of the dataelement, and sets the vr internally.
	 */

	private long getLength(int tag) throws IOException
	{
		long length;
		stream.setByteOrder(bo);

		int b0 = (stream.readByte() & 0x000000ff);
		int b1 = (stream.readByte() & 0x000000ff);
		int b2 = (stream.readByte() & 0x000000ff);
		int b3 = (stream.readByte() & 0x000000ff);

		if (implicit)
		{
			String temp = (String) dh.get(tag2hex(tag) + "vr");
			if (temp != null)
				vr = (temp.charAt(0) << 8) + temp.charAt(1);
			else
				vr = OX;

			if (bo == ByteOrder.BIG_ENDIAN)
				length = ((b0 << 24) + (b1 << 16) + (b2 << 8) + b3);
			else
				length = ((b3 << 24) + (b2 << 16) + (b1 << 8) + b0);
		} else
		{
			vr = (b0 << 8) + b1;
			switch (vr)
			{
			case SQ:
				length = stream.readInt();
				break;
			case OB:
			case OW:
			case UN:
			case UT:
			case OX:
				length = stream.readInt();
				break;
			default:
				if (bo == ByteOrder.BIG_ENDIAN)
					length = ((b2 << 8) + b3);
				else
					length = ((b3 << 8) + b2);
				break;
			}
		}

		// hack needed to read some GE files
		// The element length must be even!
		if (length == 13)
			length = 10;

		// "Undefined" element length.
		// This is a sort of bracket that encloses a sequence of elements.
		// if (length == -1) length = 0;
		return length;
	}

	private int getNextTag() throws IOException
	{
		/*
		 * int groupWord = stream.readShort(); int elementWord =
		 * stream.readShort(); int tag = groupWord<<16 | elementWord;
		 */

		int groupWord = stream.readShort() & 0x0000ffff;
		int elementWord = stream.readShort() & 0x0000ffff;
		int tag = groupWord << 16 | elementWord;
		return tag;
	}

	private Object getValueAsObject(int vr, long elementLength)
			throws IOException
	{
		Object value = null;
		long pos = stream.getStreamPosition();
		switch (vr)
		{
		case AE:
		case AS:
		case CS:
		case DA:
		case DS:
		case DT:
		case IS:
		case LO:
		case LT:
		case PN:
		case SH:
		case ST:
		case TM:
		case UI:
		case UT:
			value = getString(elementLength).trim();
			StringTokenizer st = new StringTokenizer((String) value, "\\");
			if (st.countTokens() > 1)
			{
				Vector v = new Vector();
				while (st.hasMoreTokens())
					v.add(((String) st.nextToken()).trim());
				value = v;
			}
			break;
		case AT:
			value = new Integer(stream.readShort() << 16 | stream.readShort());
			break;
		case OB:
		case OX:
		case OW:
			byte[] temp = new byte[(int) elementLength];
			stream.readFully(temp);
			value = temp;
			break;
		case FL:
			if (elementLength != 4)
			{
				Vector v = new Vector();
				while ((pos + elementLength) != stream.getStreamPosition())
					v.add(new Float(stream.readFloat()));
				value = v;
			} else
			{
				value = new Float(stream.readFloat());
			}
			break;
		case FD:
			if (elementLength != 8)
			{
				Vector v = new Vector();
				while ((pos + elementLength) != stream.getStreamPosition())
					v.add(new Float(stream.readDouble()));
				value = v;
			} else
			{
				value = new Float(stream.readDouble());
			}
			break;
		case SL:
			if (elementLength != 4)
			{
				Vector v = new Vector();
				while ((pos + elementLength) != stream.getStreamPosition())
					v.add(new Integer(stream.readInt()));
				value = v;
			} else
			{
				value = new Integer(stream.readInt());
			}
			break;
		case SS:
			if (elementLength != 2)
			{
				Vector v = new Vector();
				while ((pos + elementLength) != stream.getStreamPosition())
					v.add(new Short(stream.readShort()));
				value = v;
			} else
			{
				value = new Short(stream.readShort());
			}
			break;
		case UL:
			if (elementLength != 4)
			{
				Vector v = new Vector();
				while ((pos + elementLength) != stream.getStreamPosition())
					v.add(new Long(stream.readInt() & 0xffffffff));
				value = v;
			} else
			{
				value = new Long(stream.readInt() & 0xffffffff);
			}
			break;
		case US:
			if (elementLength != 2)
			{
				Vector v = new Vector();
				while ((pos + elementLength) != stream.getStreamPosition())
					v.add(new Integer(stream.readUnsignedShort() & 0xffff));
				value = v;
			} else
			{
				value = new Integer(stream.readUnsignedShort() & 0xffff);
			}
			break;
		case XS:
			if (elementLength != 2)
			{
				Vector v = new Vector();
				while ((pos + elementLength) != stream.getStreamPosition())
				{
					if (pixelRepresentation == 0)
						v.add(new Integer(stream.readUnsignedShort() & 0xffff));
					else
						v.add(new Short(stream.readShort()));
				}
				value = v;
			} else
			{
				if (pixelRepresentation == 0)
					value = new Integer(stream.readUnsignedShort() & 0xffff);
				else
					value = new Short(stream.readShort());
			}
			break;
		case SQ:
			getSQvals(stream, elementLength);
			break;
		default:
			value = getString(elementLength).trim();
		}
		return value;
	}

	private void getSQvals(ImageInputStream i, long l) throws IOException
	{
		boolean stop = false;
		long pos = i.getStreamPosition();

		if (l == 0)
			return;

		do
		{
			int tag = getNextTag();
			long elementLength = getLength(tag);
			int vm = 1;
			// System.out.println("  tag: " + Integer.toHexString(tag) +
			// " length: " + elementLength + " vr: " + Integer.toHexString(vr));
			if (vr == SQ)
			{
				DICOMHashtable.put(Integer.toHexString(tag), new DICOMElement(
						tag, vr, vm, elementLength, ""));
				getSQvals(i, elementLength);
			} else if (tag == 0xfffee000 || tag == 0xfffee00d
					|| tag == 0xfffee0dd)
			{
				DICOMHashtable.put(Integer.toHexString(tag), new DICOMElement(
						tag, vr, vm, elementLength, ""));
				if (tag == 0xfffee0dd)
					stop = true;
			} else
			{
				Object temp = getValueAsObject(vr, elementLength);
				// System.out.println("   temp: " + temp);
				if (temp instanceof Collection)
					vm = ((Collection) temp).size();
				if (temp instanceof String)
					if (temp.equals(""))
						temp = null;
				DICOMHashtable.put(Integer.toHexString(tag), new DICOMElement(
						tag, vr, vm, elementLength, temp));
			}
			long now = i.getStreamPosition();
			if (l > -1 && (now - pos) >= l)
				stop = true;
			// System.out.println("DFSDFDSFSDFSD: " + l + " now-pos: " + (now -
			// pos));
		} while (stop == false);
	}

	private String getValueAsString(int vr, long elementLength)
			throws IOException
	{
		String value;
		switch (vr)
		{
		case AE:
		case AS:
		case AT:
		case CS:
		case DA:
		case DS:
		case DT:
		case IS:
		case LO:
		case LT:
		case PN:
		case SH:
		case ST:
		case TM:
		case UI:
			value = getString(elementLength);
			break;
		case US:
		case XS:
			value = Integer.toString(stream.readUnsignedShort());
			break;
		default:
			value = getString(elementLength).trim();
		}
		return value.trim();
	}

	public static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String tag2hex(int tag)
	{
		char[] buf8 = new char[8];
		int pos = 7;
		while (pos >= 0)
		{
			buf8[pos] = hexDigits[tag & 0xf];
			tag >>>= 4;
			pos--;
		}

		return (new String(buf8)).trim();
	}

	public Iterator getImageTypes(int imageIndex) throws IIOException
	{
		boolean isSigned;

		checkIndex(imageIndex);
		readHeader();

		if (pixelRepresentation == 0)
			isSigned = false;
		else
			isSigned = true;

		if (bitsAllocated <= 8)
		{
			dataType = DataBuffer.TYPE_BYTE;
		} else if (bitsAllocated <= 16 && bitsAllocated > 8 && !isSigned)
		{
			dataType = DataBuffer.TYPE_USHORT;
		} else if (bitsAllocated <= 16 && bitsAllocated > 8 && isSigned)
		{
			dataType = DataBuffer.TYPE_SHORT;
		} else
		{
			dataType = DataBuffer.TYPE_INT;
		}

		ArrayList l = new ArrayList(1);

		// System.out.println("photometricInterpretation :" +
		// photometricInterpretation);
		if (photometricInterpretation.equalsIgnoreCase("MONOCHROME2")
				|| photometricInterpretation.equalsIgnoreCase("MONOCHROME1"))
		{
			if (bitsAllocated <= 8)
			{
				l
						.add(ImageTypeSpecifier.createGrayscale(8, dataType,
								isSigned));
			} else
			{
				l.add(ImageTypeSpecifier
						.createGrayscale(16, dataType, isSigned));
			}
		} else if (photometricInterpretation.equalsIgnoreCase("PALETTE COLOR"))
		{
			l.add(ImageTypeSpecifier.createIndexed(redLUT, greenLUT, blueLUT,
					alphaLUT, 8, dataType));
		} else if (photometricInterpretation.equalsIgnoreCase("RGB"))
		{
			int[] bandOffsets = new int[samplesPerPixel];
			for (int i = 0; i < samplesPerPixel; i++)
				bandOffsets[i] = i;
			if (samplesPerPixel > 3)
			{
				l.add(ImageTypeSpecifier.createInterleaved(ColorSpace
						.getInstance(ColorSpace.CS_sRGB), bandOffsets,
						dataType, true, false));
			} else
			{
				l.add(ImageTypeSpecifier.createInterleaved(ColorSpace
						.getInstance(ColorSpace.CS_sRGB), bandOffsets,
						dataType, false, false));
			}
		} else
		{
			if (bitsAllocated <= 8)
			{
				l
						.add(ImageTypeSpecifier.createGrayscale(8, dataType,
								isSigned));
			} else
			{
				l.add(ImageTypeSpecifier
						.createGrayscale(16, dataType, isSigned));
			}
		}

		return l.iterator();
	}

	public BufferedImage read(int imageIndex, ImageReadParam param)
			throws IIOException
	{
		readHeader();

		// Init default values
		Rectangle sourceRegion = getSourceRegion(param, width, height);
		int sourceXSubsampling = 1;
		int sourceYSubsampling = 1;
		int[] sourceBands = null;
		int[] destinationBands = null;
		Point destinationOffset = new Point(0, 0);

		if (param != null)
		{
			sourceXSubsampling = param.getSourceXSubsampling();
			sourceYSubsampling = param.getSourceYSubsampling();
			sourceBands = param.getSourceBands();
			destinationBands = param.getDestinationBands();
			destinationOffset = param.getDestinationOffset();
		}

		BufferedImage dst = null;

		try
		{
			stream.seek(imageStartPosition);

			dst = getDestination(param, getImageTypes(0), width, height);
			int inputBands = samplesPerPixel;

			checkReadParamBandSettings(param, inputBands, dst.getSampleModel()
					.getNumBands());

			WritableRaster imRas = dst.getWritableTile(0, 0);

			int dstMinX = imRas.getMinX();
			int dstMaxX = dstMinX + imRas.getWidth() - 1;
			int dstMinY = imRas.getMinY();
			int dstMaxY = dstMinY + imRas.getHeight() - 1;

			int[] bandOffsets = new int[inputBands];
			for (int i = 0; i < inputBands; i++)
				bandOffsets[i] = i;

			int samplesPerRow = width * inputBands;
			int pixelsPerRow = width / sourceXSubsampling;

			byte[] byteData = null;
			short[] shortData = null;
			int[] intData = null;

			if (destinationBands != null)
				imRas = imRas.createWritableChild(0, 0, imRas.getWidth(), imRas
						.getHeight(), 0, 0, destinationBands);
			// fix update count of pixels read
			if (dataType == DataBuffer.TYPE_BYTE)
			{
				byteData = new byte[samplesPerRow * height];
				// if(bitsStored == 8)
				stream.readFully(byteData, 0, samplesPerRow * height);
				// else
				// for(int i = 0; i < samplesPerRow * height; i++) byteData[i] =
				// (byte)(stream.readBits(bitsStored)&0x00000000ffffffff);
				imRas.setDataElements(0, 0, pixelsPerRow, height, byteData);
			} else if (dataType == DataBuffer.TYPE_SHORT
					|| dataType == DataBuffer.TYPE_USHORT)
			{
				shortData = new short[samplesPerRow * height];
				// if(bitsStored == 16)
				stream.readFully(shortData, 0, samplesPerRow * height);
				// else
				// for(int i = 0; i < samplesPerRow * height; i++) shortData[i]
				// = (short)(stream.readBits(bitsStored)&0x00000000ffffffff);
				imRas.setDataElements(0, 0, pixelsPerRow, height, shortData);
			} else
			{
				intData = new int[samplesPerRow * height];
				// if(bitsStored == 32)
				stream.readFully(intData, 0, samplesPerRow * height);
				// else
				// for(int i = 0; i < samplesPerRow * height; i++) intData[i] =
				// (int)(stream.readBits(bitsStored)&0x00000000ffffffff);
				imRas.setDataElements(0, 0, pixelsPerRow, height, intData);
			}
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException();
		} catch (InterruptedIOException e)
		{
			dst = null;
			return dst;
		} catch (Exception e)
		{
			throw new IIOException("Error reading image", e.getCause());
		}
		return dst;
	}

	public Hashtable getDICOMHashtable() throws Exception
	{
		if (DICOMHashtable != null)
			return DICOMHashtable;
		readHeader();
		return DICOMHashtable;
	}

	public void readMetadata() throws IIOException
	{
		if (metadata != null)
			return;
		readHeader();
		this.metadata = new DICOMMetadata();

		try
		{
			stream.seek(dataSetPosition);
			int tag;
			long elementLength;
			while (stream.getStreamPosition() < imageStartPosition - 1
					&& (tag = getNextTag()) != 0x7FE00010)
			{
				elementLength = getLength(tag);
				String temp = getValueAsString(vr, elementLength);
				metadata.add(tag2hex(tag), temp);
				// System.out.println("metadata: " + tag2hex(tag) + " " + temp
				// );
			}
		} catch (Exception e)
		{
			throw new IIOException("Error reading metadata", e.getCause());
		}
	}

	public IIOMetadata getImageMetadata(int imageIndex) throws IIOException
	{
		readMetadata();
		return metadata;
	}

	public IIOMetadata getStreamMetadata() throws IIOException
	{
		return null;
	}

	public void reset()
	{
		super.reset();
		resetStreamSettings();
	}

	public void resetStreamSettings()
	{
		gotHeader = false;
		metadata = null;
	}

}
