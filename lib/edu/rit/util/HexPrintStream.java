//******************************************************************************
//
// File:    HexPrintStream.java
// Package: edu.rit.util
// Unit:    Class edu.rit.util.HexPrintStream
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

package edu.rit.util;

import java.io.PrintStream;

/**
 * Class HexPrintStream provides an object that does everything a {@link
 * java.io.PrintStream </CODE>PrintStream<CODE>} does, and that can print
 * integer values and byte arrays in hexadecimal. The hex print stream prints
 * into some underlying print stream.
 * <P>
 * Two predefined hex print streams are provided. <TT>HexPrintStream.out</TT>
 * prints into <TT>System.out</TT>. <TT>HexPrintStream.err</TT> prints into
 * <TT>System.err</TT>.
 *
 * @author  Alan Kaminsky
 * @version 28-Jul-2003
 */
public class HexPrintStream
	{

// Hidden data members.

	private static final char[] hexdigit = new char[]
		{'0', '1', '2', '3', '4', '5', '6', '7',
		 '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	private PrintStream ps;

// Exported instances.

	/**
	 * A hex print stream that prints into <TT>System.out</TT>.
	 */
	public static final HexPrintStream out = new HexPrintStream (System.out);

	/**
	 * A hex print stream that prints into <TT>System.err</TT>.
	 */
	public static final HexPrintStream err = new HexPrintStream (System.err);

// Exported constructors.

	/**
	 * Construct a new hex print stream that will print into the given
	 * underlying print stream.
	 *
	 * @param  ps  Underlying print stream.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>ps</TT> is null.
	 */
	public HexPrintStream
		(PrintStream ps)
		{
		if (ps == null)
			{
			throw new NullPointerException();
			}
		this.ps = ps;
		}

// Exported operations.

	/**
	 * Returns this hex print stream's underlying print stream.
	 */
	public PrintStream getPrintStream()
		{
		return ps;
		}

	/**
	 * Flush this hex print stream. The underlying print stream is flushed.
	 */
	public void flush()
		{
		ps.flush();
		}

	/**
	 * Close this hex print stream. The underlying print stream is closed.
	 */
	public void close()
		{
		ps.close();
		}

	/**
	 * Write the given byte to this hex print stream.
	 *
	 * @param  b  Byte.
	 */
	public void write
		(int b)
		{
		ps.write (b);
		}

	/**
	 * Write the given byte array to this hex print stream.
	 *
	 * @param  buf  Byte array.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 */
	public void write
		(byte[] buf)
		{
		ps.write (buf, 0, buf.length);
		}

	/**
	 * Write a portion of the given byte array to this hex print stream.
	 *
	 * @param  buf  Byte array.
	 * @param  off  Index of first byte to write.
	 * @param  len  Number of bytes to write.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, or <TT>off+len</TT> &gt; <TT>buf.length</TT>.
	 */
	public void write
		(byte[] buf,
		 int off,
		 int len)
		{
		ps.write (buf, off, len);
		}

	/**
	 * Print a boolean value to this hex print stream.
	 *
	 * @param  b  Boolean value.
	 */
	public void print
		(boolean b)
		{
		ps.print (b);
		}

	/**
	 * Print a character to this hex print stream.
	 *
	 * @param  c  Character.
	 */
	public void print
		(char c)
		{
		ps.print (c);
		}

	/**
	 * Print an integer value to this hex print stream.
	 *
	 * @param  i  Integer value.
	 */
	public void print
		(int i)
		{
		ps.print (i);
		}

	/**
	 * Print a long integer value to this hex print stream.
	 *
	 * @param  l  Long integer value.
	 */
	public void print
		(long l)
		{
		ps.print (l);
		}

	/**
	 * Print a single-precision floating-point value to this hex print stream.
	 *
	 * @param  f  Single-precision floating-point value.
	 */
	public void print
		(float f)
		{
		ps.print (f);
		}

	/**
	 * Print a double-precision floating-point value to this hex print stream.
	 *
	 * @param  d  Double-precision floating-point value.
	 */
	public void print
		(double d)
		{
		ps.print (d);
		}

	/**
	 * Print an array of characters to this hex print stream.
	 *
	 * @param  s  Array of characters.
	 */
	public void print
		(char[] s)
		{
		ps.print (s);
		}

	/**
	 * Print a string to this hex print stream.
	 *
	 * @param  s  String.
	 */
	public void print
		(String s)
		{
		ps.print (s);
		}

	/**
	 * Print an object to this hex print stream.
	 *
	 * @param  obj  Object.
	 */
	public void print
		(Object obj)
		{
		ps.print (obj);
		}

	/**
	 * Print a line separator to this hex print stream.
	 */
	public void println()
		{
		ps.println();
		}

	/**
	 * Print a boolean value plus a line separator to this hex print stream.
	 *
	 * @param  b  Boolean value.
	 */
	public void println
		(boolean b)
		{
		ps.println (b);
		}

	/**
	 * Print a character plus a line separator to this hex print stream.
	 *
	 * @param  c  Character.
	 */
	public void println
		(char c)
		{
		ps.println (c);
		}

	/**
	 * Print an integer value plus a line separator to this hex print stream.
	 *
	 * @param  i  Integer value.
	 */
	public void println
		(int i)
		{
		ps.println (i);
		}

	/**
	 * Print a long integer value plus a line separator to this hex print
	 * stream.
	 *
	 * @param  l  Long integer value.
	 */
	public void println
		(long l)
		{
		ps.println (l);
		}

	/**
	 * Print a single-precision floating-point value plus a line separator to
	 * this hex print stream.
	 *
	 * @param  f  Single-precision floating-point value.
	 */
	public void println
		(float f)
		{
		ps.println (f);
		}

	/**
	 * Print a double-precision floating-point value plus a line separator to
	 * this hex print stream.
	 *
	 * @param  d  Double-precision floating-point value.
	 */
	public void println
		(double d)
		{
		ps.println (d);
		}

	/**
	 * Print an array of characters plus a line separator to this hex print
	 * stream.
	 *
	 * @param  s  Array of characters.
	 */
	public void println
		(char[] s)
		{
		ps.println (s);
		}

	/**
	 * Print a string plus a line separator to this hex print stream.
	 *
	 * @param  s  String.
	 */
	public void println
		(String s)
		{
		ps.println (s);
		}

	/**
	 * Print an object plus a line separator to this hex print stream.
	 *
	 * @param  obj  Object.
	 */
	public void println
		(Object obj)
		{
		ps.println (obj);
		}

	/**
	 * Print a byte value in hexadecimal to this hex print stream. Two
	 * hexadecimal digits are printed.
	 *
	 * @param  b  Byte value.
	 */
	public void printhex
		(byte b)
		{
		ps.print (hexdigit [(b >> 4) & 0xF]);
		ps.print (hexdigit [(b     ) & 0xF]);
		}

	/**
	 * Print a character value in hexadecimal to this hex print stream. Four
	 * hexadecimal digits are printed.
	 *
	 * @param  c  Character value.
	 */
	public void printhex
		(char c)
		{
		ps.print (hexdigit [(c >> 12) & 0xF]);
		ps.print (hexdigit [(c >>  8) & 0xF]);
		ps.print (hexdigit [(c >>  4) & 0xF]);
		ps.print (hexdigit [(c      ) & 0xF]);
		}

	/**
	 * Print an integer value in hexadecimal to this hex print stream. Eight
	 * hexadecimal digits are printed.
	 *
	 * @param  i  Integer value.
	 */
	public void printhex
		(int i)
		{
		ps.print (hexdigit [(i >> 28) & 0xF]);
		ps.print (hexdigit [(i >> 24) & 0xF]);
		ps.print (hexdigit [(i >> 20) & 0xF]);
		ps.print (hexdigit [(i >> 16) & 0xF]);
		ps.print (hexdigit [(i >> 12) & 0xF]);
		ps.print (hexdigit [(i >>  8) & 0xF]);
		ps.print (hexdigit [(i >>  4) & 0xF]);
		ps.print (hexdigit [(i      ) & 0xF]);
		}

	/**
	 * Print a long integer value in hexadecimal to this hex print stream.
	 * Sixteen hexadecimal digits are printed.
	 *
	 * @param  l  Long integer value.
	 */
	public void printhex
		(long l)
		{
		ps.print (hexdigit [(int) (l >> 60) & 0xF]);
		ps.print (hexdigit [(int) (l >> 56) & 0xF]);
		ps.print (hexdigit [(int) (l >> 52) & 0xF]);
		ps.print (hexdigit [(int) (l >> 48) & 0xF]);
		ps.print (hexdigit [(int) (l >> 44) & 0xF]);
		ps.print (hexdigit [(int) (l >> 40) & 0xF]);
		ps.print (hexdigit [(int) (l >> 36) & 0xF]);
		ps.print (hexdigit [(int) (l >> 32) & 0xF]);
		ps.print (hexdigit [(int) (l >> 28) & 0xF]);
		ps.print (hexdigit [(int) (l >> 24) & 0xF]);
		ps.print (hexdigit [(int) (l >> 20) & 0xF]);
		ps.print (hexdigit [(int) (l >> 16) & 0xF]);
		ps.print (hexdigit [(int) (l >> 12) & 0xF]);
		ps.print (hexdigit [(int) (l >>  8) & 0xF]);
		ps.print (hexdigit [(int) (l >>  4) & 0xF]);
		ps.print (hexdigit [(int) (l      ) & 0xF]);
		}

	/**
	 * Print a byte array in hexadecimal to this hex print stream. Two
	 * hexadecimal digits are printed for each byte, with a space between each
	 * byte and the next.
	 *
	 * @param  buf  Byte array.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 */
	public void printhex
		(byte[] buf)
		{
		printhex (buf, 0, buf.length);
		}

	/**
	 * Print a portion of a byte array in hexadecimal to this hex print stream.
	 * Two hexadecimal digits are printed for each byte, with a space between
	 * each byte and the next.
	 *
	 * @param  buf  Byte array.
	 * @param  off  Index of first byte to write.
	 * @param  len  Number of bytes to write.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, or <TT>off+len</TT> &gt; <TT>buf.length</TT>.
	 */
	public void printhex
		(byte[] buf,
		 int off,
		 int len)
		{
		if (off < 0 || len < 0 || off+len > buf.length)
			{
			throw new IndexOutOfBoundsException();
			}
		if (len > 0)
			{
			printhex (buf[off]);
			++ off;
			-- len;
			while (len > 0)
				{
				ps.print (' ');
				printhex (buf[off]);
				++ off;
				-- len;
				}
			}
		}

	/**
	 * Print a byte value in hexadecimal plus a line separator to this hex print
	 * stream. Two hexadecimal digits are printed.
	 *
	 * @param  b  Byte value.
	 */
	public void printlnhex
		(byte b)
		{
		printhex (b);
		ps.println();
		}

	/**
	 * Print a character value in hexadecimal plus a line separator to this hex
	 * print stream. Four hexadecimal digits are printed.
	 *
	 * @param  c  Character value.
	 */
	public void printlnhex
		(char c)
		{
		printhex (c);
		ps.println();
		}

	/**
	 * Print an integer value in hexadecimal plus a line separator to this hex
	 * print stream. Eight hexadecimal digits are printed.
	 *
	 * @param  i  Integer value.
	 */
	public void printlnhex
		(int i)
		{
		printhex (i);
		ps.println();
		}

	/**
	 * Print a long integer value in hexadecimal plus a line separator to this
	 * hex print stream. Sixteen hexadecimal digits are printed.
	 *
	 * @param  l  Long integer value.
	 */
	public void printlnhex
		(long l)
		{
		printhex (l);
		ps.println();
		}

	/**
	 * Print a byte array in hexadecimal with line separators to this hex print
	 * stream. Two hexadecimal digits are printed for each byte, with a space
	 * between each byte and the next. A line separator is printed before the
	 * first byte, after every <TT>n</TT>-th byte, and after the last byte.
	 *
	 * @param  buf  Byte array.
	 * @param  n    Number of bytes per line.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>n</TT> &lt; 1.
	 */
	public void printlnhex
		(byte[] buf,
		 int n)
		{
		printlnhex (buf, 0, buf.length, n);
		}

	/**
	 * Print a portion of a byte array in hexadecimal with line separators to
	 * this hex print stream. Two hexadecimal digits are printed for each byte,
	 * with a space between each byte and the next. A line separator is printed
	 * before the first byte, after every <TT>n</TT>-th byte, and after the last
	 * byte.
	 *
	 * @param  buf  Byte array.
	 * @param  off  Index of first byte to write.
	 * @param  len  Number of bytes to write.
	 * @param  n    Number of bytes per line.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, or <TT>off+len</TT> &gt; <TT>buf.length</TT>.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>n</TT> &lt; 1.
	 */
	public void printlnhex
		(byte[] buf,
		 int off,
		 int len,
		 int n)
		{
		if (off < 0 || len < 0 || off+len > buf.length)
			{
			throw new IndexOutOfBoundsException();
			}
		if (n <= 0)
			{
			throw new IllegalArgumentException();
			}
		if (len > 0)
			{
			int count = 0;
			ps.println();
			while (len > 0)
				{
				printhex (buf[off]);
				++ off;
				-- len;
				++ count;
				if (count == n)
					{
					ps.println();
					count = 0;
					}
				else
					{
					ps.print (' ');
					}
				}
			if (count > 0)
				{
				ps.println();
				}
			}
		}

	/**
	 * Print the given exception's stack trace to this hex print stream.
	 *
	 * @param  exc  Exception.
	 */
	public void printStackTrace
		(Throwable exc)
		{
		exc.printStackTrace (ps);
		}

	}
