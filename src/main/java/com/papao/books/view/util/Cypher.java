package com.papao.books.view.util;

import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public final class Cypher {

	private static Logger logger = Logger.getLogger(Cypher.class);

    public static final String DEFAULT_ENCODING = "UTF-8";
    private static BASE64Encoder enc = new BASE64Encoder();
    private static BASE64Decoder dec = new BASE64Decoder();

    private Cypher() {}

    public static byte[] computeHash(final String x) throws NoSuchAlgorithmException {
        java.security.MessageDigest md;
        md = java.security.MessageDigest.getInstance("SHA-1");
        md.reset();
        md.update(x.getBytes());
        return md.digest();
    }

    private static String byteArrayToHexString(final byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    public static boolean checkPassword(final String userPass, final String sHAComputedPass) {
		if (StringUtils.isEmpty(userPass)) {
            return false;
        }
        String hash;
        try {
            hash = Cypher.byteArrayToHexString(Cypher.computeHash(userPass));
        } catch (NoSuchAlgorithmException exc) {
			logger.error(exc, exc);
            return false;
        }
        return hash.equals(sHAComputedPass);
    }

    public static String getSHACryptedValue(final String string) {
        try {
            return Cypher.byteArrayToHexString(Cypher.computeHash(string));
        } catch (NoSuchAlgorithmException exc) {
			logger.error(exc, exc);
            return null;
        }
    }

    public static String xorMessage(final String message, final String key) {
        try {
            if ((message == null) || (key == null)) {
                return null;
            }

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            if (kl == 0) {
                kl = 1;
                ml = 1;
                keys = new char[] {
                    0 };
                mesg = new char[] {
                    0 };
            }
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }
            return new String(newmsg);
        } catch (Exception exc) {
			logger.error(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
            return null;
        }
    }

    public static String base64encode(final String password) throws UnsupportedEncodingException {
        return Cypher.enc.encode(password.getBytes(Cypher.DEFAULT_ENCODING));
    }

    public static String base64decode(final String password) throws UnsupportedEncodingException, IOException {
        return new String(Cypher.dec.decodeBuffer(password), Cypher.DEFAULT_ENCODING);
    }

    public static String encode(final String userName, final String userPassword) throws UnsupportedEncodingException {
        String dataParam = userName;
        dataParam = Cypher.xorMessage(userName, userPassword);
        return Cypher.base64encode(dataParam);
    }

    public static String decode(final String encodedUserPassword, final String correctPass) throws IOException {
        String dataParam = encodedUserPassword;
        dataParam = Cypher.base64decode(dataParam);
        return Cypher.xorMessage(dataParam, correctPass);
    }

    public static boolean checkPassword2(final String userName, final String userPass, final String realPass) {
        try {
            return Cypher.decode(Cypher.encode(userName, userPass), realPass).equals(userName);
        } catch (UnsupportedEncodingException exc) {
			logger.error(exc, exc);
            return false;
        } catch (IOException exc) {
			logger.error(exc, exc);
            return false;
        }
    }
}
