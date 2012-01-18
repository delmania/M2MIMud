//******************************************************************************
//
// File:    MuH1MessageDigest.java
// Package: edu.rit.crypto
// Unit:    Class edu.rit.crypto.MuH1MessageDigest
//
// This Java source file is copyright (C) 2001-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the M2MI Library ("The Library"). The
// Library is free software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version.
//
// The Library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html or by writing to the Free
// Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA.
//
//******************************************************************************

package edu.rit.crypto;

/**
 * Class MuH1MessageDigest provides a message digest algorithm using the
 * MicroHash-One (&mu;H-1) 32-bit one-way hash function. The algorithm is:
 * <P>
 * <B>&mu;H-1 Message Digest Algorithm</B>
 * <OL TYPE=1>
 * <LI>
 * [Padding] Append zero or more <TT>0x00</TT> bytes to make the length of the
 * byte sequence equal to a multiple of four bytes.
 * <BR>&nbsp;
 * <LI>
 * [Initialize chaining variable] <TT>C &lt;- 0x045b9e58</TT>.
 * <BR>&nbsp;
 * <LI>
 * For each four-byte chunk of the byte sequence (including padding bytes if
 * any):
 * <BR>&nbsp;
 * <OL TYPE=a>
 * <LI>
 * <TT>M &lt;- </TT>The next four bytes of the byte sequence converted to an
 * integer in big-endian order.
 * <BR>&nbsp;
 * <LI>
 * <TT>C &lt;- MuH1.compress (C, M)</TT>.
 * <BR>&nbsp;
 * </OL>
 * <LI>
 * Return message digest value = <TT>C</TT>.
 * </OL>
 * <P>
 * The &mu;H-1 compression function used in Step (3b) is defined in class {@link
 * MuH1 </CODE>MuH1<CODE>}.
 * <P>
 * As with the &mu;H-1 compression function, the &mu;H-1 message digest
 * algorithm is intended to be used in applications where it only has to
 * withstand attack for a short time, a few milliseconds at most. See class
 * {@link MuH1 </CODE>MuH1<CODE>} for further information.
 * <P>
 * <B><I>Note:</I></B> Class MuH1MessageDigest is not multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 16-Jul-2003
 */
public class MuH1MessageDigest
	{

// Hidden data members.

	// Chaining variable.
	private int c = 0x045b9e58;

	// Partial message variable.
	private int m;

	// Number of bytes accumulated in partial message variable.
	private int count;

// Exported constructors.

	/**
	 * Construct a new message digest object for calculating the &mu;H-1 message
	 * digest of a sequence of bytes.
	 */
	public MuH1MessageDigest()
		{
		}

// Exported operations.

	/**
	 * Accumulate a four-byte integer into this message digest object.
	 *
	 * @param  i  Integer.
	 */
	public void accumulate
		(int i)
		{
		switch (count)
			{
			case 0:
				c = MuH1.compress (c, i);
				break;
			case 1:
				m |= i >>> 8;
				c = MuH1.compress (c, m);
				m = i << 24;
				break;
			case 2:
				m |= i >>> 16;
				c = MuH1.compress (c, m);
				m = i << 16;
				break;
			case 3:
				m |= i >>> 24;
				c = MuH1.compress (c, m);
				m = i << 8;
				break;
			}
		}

	/**
	 * Accumulate a byte into this message digest object.
	 *
	 * @param  b  Byte.
	 */
	public void accumulate
		(byte b)
		{
		switch (count)
			{
			case 0:
				m = b << 24;
				count = 1;
				break;
			case 1:
				m |= (b & 0xFF) << 16;
				count = 2;
				break;
			case 2:
				m |= (b & 0xFF) << 8;
				count = 3;
				break;
			case 3:
				m |= (b & 0xFF);
				c = MuH1.compress (c, m);
				count = 0;
				break;
			}
		}

	/**
	 * Accumulate a byte array into this message digest object.
	 *
	 * @param  buf  Byte array.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 */
	public void accumulate
		(byte[] buf)
		{
		accumulate (buf, 0, buf.length);
		}

	/**
	 * Accumulate a portion of a byte array into this message digest object.
	 *
	 * @param  buf  Byte array.
	 * @param  off  Index of first byte to accumulate.
	 * @param  len  Number of bytes to accumulate.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, or <TT>off+len</TT> &gt; <TT>buf.length</TT>.
	 */
	public void accumulate
		(byte[] buf,
		 int off,
		 int len)
		{
		if (off < 0 || len < 0 || off+len > buf.length)
			{
			throw new IndexOutOfBoundsException();
			}
		while (len > 0)
			{
			accumulate (buf[off]);
			++ off;
			-- len;
			}
		}

	/**
	 * Obtain this message digest object's one-way hash value. Padding bytes are
	 * accumulated if necessary, then the one-way hash value is computed and
	 * returned.
	 * <P>
	 * <B><I>Note:</I></B> After calling <TT>hash()</TT>, do not accumulate
	 * anything further into this message digest object. The results will be
	 * incorrect.
	 *
	 * @return  One-way hash value.
	 */
	public int hash()
		{
		if (count > 0)
			{
			c = MuH1.compress (c, m);
			count = 0;
			}
		return c;
		}

	}
