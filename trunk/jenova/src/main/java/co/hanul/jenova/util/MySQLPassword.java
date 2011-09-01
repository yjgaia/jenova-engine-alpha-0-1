package co.hanul.jenova.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MySQL에서 사용되는 단방향 암호화 알고리즘을 Java로 구현한 클래스.
 */
public class MySQLPassword
{
	private static final Charset charset = Charset.forName( "UTF-8" );
	private static MessageDigest MD5;
	private static MessageDigest SHA1;

	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
			SHA1 = MessageDigest.getInstance("SHA1");
		}
		catch( NoSuchAlgorithmException ex ) {
			ex.printStackTrace();
		}
	}

	/**
	 * 바이트 배열을 hex string으로 변환한다.
	 * <p>
	 * null이면 null, 길이가 0이면 길이가 0인 문자열을 반환한다.
	 *
	 * @return 변환된 hex string.
	 */
	private static String toHexString( byte[] b ) {
		if( b == null )
			return null;
		if( b.length == 0 )
			return "";

		StringBuffer r = new StringBuffer(b.length * 2);
		for (int i=0; i < b.length; i++) {
			String x = Integer.toHexString(b[i] & 0xFF).toLowerCase();
			if( x.length() < 2 )
				r.append('0');
			r.append(x);
		}
		return r.toString();
	}

	/**
	 * Calculate the md5 hash of a string
	 *
	 * @param str The string.
	 * @return Returns the hash as a 32-character hexadecimal numbered string.
	 */
	public static String md5( String str ) {
		if( str == null )
			return null;
		byte[] b = str.getBytes(charset);
		byte[] r = MD5.digest(b);
		return toHexString(r);
	}

	/**
	 * MySQL 4.1.1 password hashing: SHA conversion (see RFC 2289, 3174) twice
	 * applied to the password string, and then produced octet sequence is
	 * converted to hex string.
	 * <p>
	 * The result of this function is used as return value from PASSWORD() and
	 * is stored in the database.
	 *
	 * @param password NULL-terminated password string
	 * @return scrambled password
	 */
	public static String password( String password ) {
		if( password == null )
			return null;
		if( password.isEmpty() )
			return "";

		byte[] b = password.getBytes(charset);
		byte[] s = SHA1.digest(b); // stage1
		s = SHA1.digest(s); // stage2

		String r = toHexString(s);
		if( r == null )
			return r;
		return "*"+ r.toUpperCase();
	}

	/**
	 * Create password to be stored in user database from raw string
	 * Used for pre-4.1 password handling
	 *
	 * @param password user-supplied password
	 * @return scrambled password
	 */
	public static String oldPassword( String password ) {
		if( password == null )
			return null;
		if( password.isEmpty() )
			return "";

		byte[] b = password.getBytes(charset);
		int[] r = hash_password( b );

		return String.format( "%08x%08x", r[0], r[1] );
	}

	/**
	 * Generate binary hash from raw text string
	 * Used for Pre-4.1 password handling
	 *
	 * @param password plain text password to build hash (password may be not null-terminated)
	 * @return hash value
	 */
	private static int[] hash_password( byte[] password ) {
		int nr[] = { 1345345333, 0x12345671 };
		int ladd = 7;
		int tmp;

		for (int i=0; i < password.length; i++) {
			// skip space in password
			if( password[i] == ' ' || password[i] == '\t' )
				continue;
			tmp = (int)(0xFF & password[i]);
			nr[0] ^= (((nr[0] & 63) + ladd) * tmp) + (nr[0] << 8);
			nr[1] += (nr[1] << 8) ^ nr[0];
			ladd += tmp;
		}

		// Don't use sign bit (str2int)
		nr[0] &= 0x7fffffff;
		nr[1] &= 0x7fffffff;

		return nr;
	}
}
