//******************************************************************************
//
// File:    MuH1Random.java
// Package: edu.rit.crypto
// Unit:    Class edu.rit.crypto.MuH1Random
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
 * Class MuH1Random provides a pseudorandom number generator (PRNG) based on the
 * MicroHash-One (&mu;H-1) 32-bit one-way hash function. To use the &mu;H1 PRNG,
 * construct a new instance of class MuH1Random or re-initialize an existing
 * instance by calling the <TT>initialize()</TT> method; seed the PRNG by
 * calling the <TT>accumulateSeed()</TT> method one or more times; and
 * repeatedly call <TT>next()</TT> to generate a series of pseudorandom numbers.
 * <P>
 * The &mu;H-1 PRNG does not have the goal of generating a highly random
 * sequence of values. While the &mu;H-1 PRNG does okay on statistical
 * randomness tests, other PRNGs do a lot better. Rather, the &mu;H-1 PRNG's
 * goals are (a) to run quickly, and yet (b) to make it difficult for an
 * attacker to predict the next value in the sequence, having observed the
 * previous values in the sequence.
 * <P>
 * As with the &mu;H-1 compression function, the &mu;H-1 PRNG is intended to be
 * used in applications where it only has to withstand attack for a short time,
 * a few milliseconds at most. See class {@link MuH1 </CODE>MuH1<CODE>} for
 * further information.
 * <P>
 * <I>Note:</I> Class MuH1Random is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 27-Aug-2003
 */
public class MuH1Random
	{

// Hidden data members.

	// Chaining variable.
	private int c;

	// Counter.
	private int n;

// Exported constructors.

	/**
	 * Construct a new &mu;H-1 PRNG. The PRNG is initialized but not seeded. The
	 * PRNG should be seeded before generating any pseudorandom numbers.
	 */
	public MuH1Random()
		{
		doInitialize();
		}

	/**
	 * Construct a new &mu;H-1 PRNG. The PRNG is initialized and seeded with the
	 * given <TT>int</TT> value. To defend against attack, the seed should be a
	 * random value not known to the attacker.
	 *
	 * @param  seed  Seed.
	 */
	public MuH1Random
		(int seed)
		{
		doInitialize();
		doAccumulateSeed (seed);
		}

	/**
	 * Construct a new &mu;H-1 PRNG. The PRNG is initialized and seeded with the
	 * given <TT>long</TT> value. To defend against attack, the seed should be a
	 * random value not known to the attacker.
	 *
	 * @param  seed  Seed.
	 */
	public MuH1Random
		(long seed)
		{
		doInitialize();
		doAccumulateSeed (seed);
		}

// Exported operations.

	/**
	 * Set this &mu;H-1 PRNG to the initial state. The PRNG should then be
	 * seeded before generating any pseudorandom numbers.
	 */
	public synchronized void initialize()
		{
		doInitialize();
		}

	/**
	 * Accumulate the given <TT>int</TT> seed value into this &mu;H-1 PRNG. To
	 * defend against attack, the seed should be a random value not known to the
	 * attacker.
	 *
	 * @param  seed  Seed.
	 */
	public synchronized void accumulateSeed
		(int seed)
		{
		doAccumulateSeed (seed);
		}

	/**
	 * Accumulate the given <TT>long</TT> seed value into this &mu;H-1 PRNG. To
	 * defend against attack, the seed should be a random value not known to the
	 * attacker.
	 *
	 * @param  seed  Seed.
	 */
	public synchronized void accumulateSeed
		(long seed)
		{
		doAccumulateSeed (seed);
		}

	/**
	 * Generate this &mu;H-1 PRNG's next pseudorandom number.
	 *
	 * @return  Pseudorandom number in the range -2<SUP>31</SUP> ..
	 *          2<SUP>31</SUP> - 1.
	 */
	public synchronized int next()
		{
		c = MuH1.compress (c, n);
		++ n;
		return c;
		}

	/**
	 * Generate this &mu;H-1 PRNG's next pseudorandom number in the range 0
	 * through <I>n</I>-1.
	 *
	 * @param  n  Upper bound. Must be greater than zero.
	 *
	 * @return  Pseudorandom number in the range 0 .. <I>n</I>-1.
	 */
	public int next
		(int n)
		{
		int q = 0x40000000 / n;
		int nq = n * q;
		int r;
		do
			{
			r = next() & 0x7FFFFFFF;
			}
		while (r >= nq);
		return r % n;
		}

// Hidden operations.

	/**
	 * Set this &mu;H-1 PRNG to the initial state.
	 */
	private void doInitialize()
		{
		c = 0x045b9e58;
		n = 0x7f4ab521;
		}

	/**
	 * Accumulate the given <TT>int</TT> seed value into this &mu;H-1 PRNG.
	 *
	 * @param  seed  Seed.
	 */
	private void doAccumulateSeed
		(int seed)
		{
		n = MuH1.compress (n, seed);
		}

	/**
	 * Accumulate the given <TT>long</TT> seed value into this &mu;H-1 PRNG.
	 *
	 * @param  seed  Seed.
	 */
	private void doAccumulateSeed
		(long seed)
		{
		doAccumulateSeed ((int)(seed >> 32));
		doAccumulateSeed ((int)(seed      ));
		}

// Unit test main program.

//*DBG*/	/**
//*DBG*/	 * Unit test main program.
//*DBG*/	 */
//*DBG*/	public static void main
//*DBG*/		(String[] args)
//*DBG*/		{
//*DBG*/		int i;
//*DBG*/		MuH1Random prng;
//*DBG*/
//*DBG*/		if (args.length != 3)
//*DBG*/			{
//*DBG*/			System.err.println ("Usage: java edu.rit.crypto.MuH1Random <seed> <klog2> <n>");
//*DBG*/			System.exit (1);
//*DBG*/			}
//*DBG*/		long seed = Long.parseLong (args[0]);
//*DBG*/		int klog2 = Integer.parseInt (args[1]);
//*DBG*/		int n = Integer.parseInt (args[2]);
//*DBG*/		double dn = (double) n;
//*DBG*/
//*DBG*/		// Chi-Square Test
//*DBG*/		prng = new MuH1Random (seed);
//*DBG*/		int k = 1 << klog2;
//*DBG*/		int[] count = new int [k];
//*DBG*/		int nk = n * k;
//*DBG*/		for (i = 0; i < nk; ++ i)
//*DBG*/			{
//*DBG*/			++ count [prng.next() >>> (32 - klog2)];
//*DBG*/			}
//*DBG*/		double chisq = 0.0;
//*DBG*/		for (i = 0; i < k; ++ i)
//*DBG*/			{
//*DBG*/			System.out.println ("count[" + i + "] = " + count[i]);
//*DBG*/			chisq += (count[i] - dn) * (count[i] - dn) / dn;
//*DBG*/			}
//*DBG*/		System.out.println ("Chi squared = " + chisq);
//*DBG*/
//*DBG*/		// Serial Correlation Test
//*DBG*/		prng = new MuH1Random (seed);
//*DBG*/		double dnk = dn * k;
//*DBG*/		double x0 = (((double) prng.next()) + 2147483648.0) / 4294967296.0;
//*DBG*/		double sumx = x0;
//*DBG*/		double sumxsq = x0 * x0;
//*DBG*/		double sumxy = 0.0;
//*DBG*/		double oldx = x0;
//*DBG*/		double x;
//*DBG*/		for (i = 1; i < nk; ++ i)
//*DBG*/			{
//*DBG*/			x = (((double) prng.next()) + 2147483648.0) / 4294967296.0;
//*DBG*/			sumx += x;
//*DBG*/			sumxsq += x * x;
//*DBG*/			sumxy += oldx * x;
//*DBG*/			oldx = x;
//*DBG*/			}
//*DBG*/		sumxy += oldx * x0;
//*DBG*/		System.out.println ("Serial correlation coefficient = " +
//*DBG*/			(dnk * sumxy - sumx * sumx) / (dnk * sumxsq - sumx * sumx));
//*DBG*/		}

	}
