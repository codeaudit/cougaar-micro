

package org.cougaar.microedition.kvm;

import org.cougaar.microedition.io.*;
import javax.microedition.io.*;
import java.io.DataInputStream;

public class KvmFileLoader implements FileLoader {

	String pnp = "";

	public KvmFileLoader() {}

  public void configure(String protocol, String hostName, short hostPort) {

		if (hostPort == 0)
		    pnp = protocol + "://" + hostName + "/";
		else
		    pnp = protocol + "://" + hostName + ":" + hostPort + "/";
	}

	public String showConfig() {

		return(pnp);
	}

	public String getFile(String fileName) throws Exception {

		int i;
		int count = 0;
		byte [] b = new byte[512];
		StringBuffer content = new StringBuffer(512);
		StreamConnection con = null;
		DataInputStream in = null;

		con = (StreamConnection)Connector.open(pnp + fileName, Connector.READ_WRITE, false);
		in = con.openDataInputStream();

		while ((count = in.read(b, 0, 512)) > 0)
			for (i=0; i<count; i++)
				content.append((char)b[i]);

		in.close();
		return content.toString();
	}

/*
	public static void main(String[] args) {

    String content = "";
		String file = "/node.properties";

    System.out.println("Node starting name = " + args[0] +
                       " name server = " + args[1] +
                       " port = " + args[2]);

		FileLoader fl = new KvmFileLoader();
    fl.configure("http", args[1], Short.parseShort(args[2]));

		try {
		    content = fl.getFile(args[0] + file);
		} catch(Exception e) {
			System.out.println("getFile exception: " + e);
			System.exit(1);
		}
		System.out.println(content);

		System.exit(0);
	}
*/
}
