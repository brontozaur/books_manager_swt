package com.papao.books.ui.util;

import org.apache.log4j.Logger;

import java.io.*;

/*
  Utility for making deep copies (vs. clone()'s shallow copies) of objects. Objects are first
  serialized and then deserialized. Error checking is fairly minimal in this implementation. If an
  object is encountered that cannot be serialized (or that references an object that cannot be
  serialized) an error is printed to System.err and null is returned. Depending on your specific
  application, it might make more sense to have copy(...) re-throw the exception. A later version
  of this class includes some minor optimizations.
 */

/**
 * with respect for the source : http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
 */
public final class ObjectCloner {

	private static Logger logger = Logger.getLogger(ObjectCloner.class);

    private ObjectCloner() {}

    /**
     * Returns a copy of the object, or null if the object cannot be serialized.
     */
    public static Object copy(final Object orig) {
        Object obj = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            /*
              Write the object out to a byte array
             */
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.close();

            /*
              Make an input stream from the byte array and read a copy of the object back in.
             */
            in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
			logger.error(e, e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException exc) {
					logger.error(exc, exc);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException exc) {
					logger.error(exc, exc);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException exc) {
					logger.error(exc, exc);
                }
            }
        }
        return obj;
    }

}
