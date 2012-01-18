//******************************************************************************
//
// File:    MuH1.java
// Package: edu.rit.crypto
// Unit:    Class edu.rit.crypto.MuH1
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
 * Class MuH1 provides the compression function for the MicroHash-One (&mu;H-1)
 * 32-bit one-way hash function. The compression function is used as a building
 * block for message digests (class {@link MuH1MessageDigest
 * </CODE>MuH1MessageDigest<CODE>}) and pseudorandom number generators (class
 * {@link MuH1Random </CODE>MuH1Random<CODE>}) based on the &mu;H-1 one-way hash
 * function.
 * <P>
 * The compression function's inputs are two 32-bit values A and B. The
 * compression function's output is a 32-bit value C. The compression function
 * is defined as follows:
 * <P>
 * <B>Compress (A, B)</B>
 * <BR><TT>X &lt;- (A &lt;&lt; 32) + B</TT>
 * <BR><TT>Return ((X * X) &gt;&gt; 48) &amp; 0xFFFFFFFF</TT>
 * <P>
 * The &mu;H-1 compression function is designed to run quickly, even on small
 * devices with limited memory and CPU power.
 * <P>
 * The &mu;H-1 compression function is intended to be used in applications where
 * it only has to withstand attack for a short time, a few milliseconds at most.
 * Typically, an attacker knows the output C and one of the input values, say A,
 * and the attacker needs to find a second preimage for the other input value B
 * -- that is, a value B' such that Compress (A, B) = Compress (A, B') = C. A
 * brute force search would require trying all 2<SUP>32</SUP> possible values
 * for B'. In 2003, such a search takes several minutes. If the attack's window
 * of opportunity is over in a few milliseconds, the brute force search will not
 * finish in time and the attack will fail. It is not known whether there is a
 * shortcut that would let the attacker find a second preimage with less work
 * than an exhaustive search.
 * <P>
 * An example of such an application -- and the application for which the
 * &mu;H-1 compression function was originally designed -- is preventing packet
 * insertion attacks in messages sent over an insecure network using the
 * Many-to-Many Protocol (M2MP). See package {@link edu.rit.m2mp
 * </CODE>edu.rit.m2mp<CODE>} for further information.
 *
 * @author  Alan Kaminsky
 * @version 16-Jun-2003
 */
public class MuH1
	{

// Prevent construction.

	private MuH1()
		{
		}

// Exported operations.

	/**
	 * The &mu;H-1 compression function.
	 */
	public static int compress
		(int m,
		 int c)
		{
		// MMMM = 32-bit M value, CCCC = 32-bit C value. We concatenate M with C
		// and square that:
		//           MMMMCCCC
		//         x MMMMCCCC
		//           --------
		//           WWWWWWWW = CCCC x CCCC
		//       XXXXXXXX     = MMMM x CCCC
		//       XXXXXXXX     = CCCC x MMMM
		// + YYYYYYYY         = MMMM x MMMM
		//   ----------------
		//   ZZZZZZZZZZZZZZZZ
		//         ^^^^
		// Then we keep the middle 32 bits of the result.
		long lm = m & 0xFFFFFFFFL;
		long lc = c & 0xFFFFFFFFL;
		long w = lc * lc;
		long x = lm * lc;
		long y = lm * lm;
		long z = (w >>> 32) + (x << 1) + (y << 32);
		return (int) ((z >> 16) & 0xFFFFFFFFL);
		}

	}
