package org.DICOM.image;

import java.util.*;
import java.io.*;

public class DICOMDictionary
{

	public Hashtable getDictionary()
	{
		Hashtable ht = new Hashtable(40000, .99f);
		try
		{
			StreamTokenizer st = new StreamTokenizer(new BufferedReader(
					new InputStreamReader(this.getClass().getClassLoader()
							.getResourceAsStream("DICOM-dict.txt"))));
			String line;
			st.eolIsSignificant(true);
			st.quoteChar('"');
			String tag, version, vr, vm, keyword, name;
			while (true)
			{
				if (st.nextToken() == StreamTokenizer.TT_EOF)
					break;
				tag = st.sval.toUpperCase();
				st.nextToken();
				version = st.sval;
				st.nextToken();
				vr = st.sval;
				st.nextToken();
				vm = st.sval;
				st.nextToken();
				keyword = st.sval;
				st.nextToken();
				name = st.sval;
				st.nextToken(); // should be TT_EOL

				// System.out.println("0x" + tag + " vr: " +vr + " name: " +
				// name);

				ht.put(tag + "version", version);
				ht.put(tag + "vr", vr);
				ht.put(tag + "vm", vm);
				ht.put(tag + "keyword", keyword);
				ht.put(tag + "name", name);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return ht;
	}
}
