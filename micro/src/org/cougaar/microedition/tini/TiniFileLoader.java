

package cougaar.microedition.tini;

import cougaar.microedition.io.*;
import java.net.*;
import java.io.*;

public class TiniFileLoader implements FileLoader {

	String pnp;

	public TiniFileLoader() {}

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
		URL path;
		InputStream in = null;

		path = new URL(pnp + fileName);
		in = path.openStream();

		while ((count = in.read(b, 0, 512)) > 0)
			for (i=0; i<count; i++)
				content.append((char)b[i]);

		in.close();
		return content.toString();
	}

	public static void main(String[] args) {

    String content = "";
		String file = "/node.properties";

    System.out.println("Node starting name = " + args[0] +
                       " name server = " + args[1] +
                       " port = " + args[2]);

		FileLoader fl = new TiniFileLoader();
    fl.configure("http", args[1], Short.decode(args[2]).shortValue());

		try {
		    content = fl.getFile(args[0] + file);
		} catch(Exception e) {
			System.out.println("getFile exception: " + e);
			System.exit(1);
		}
		System.out.println(content);

		System.exit(0);
	}
}
