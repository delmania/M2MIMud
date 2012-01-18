#include <stdio.h>

static unsigned long int update
	(unsigned long int msg,
	 unsigned long int chain)
	{
	unsigned long long int m, c, w, x, y, z;

	// Compute MMMMCCCC^2, where MMMM is the message and CCCC is the chaining
	// variable. Then keep the middle 4 bytes.
	//             MMMM CCCC
	//           x MMMM CCCC
	//             ---------
	//             WWWW WWWW = CCCC x CCCC
	//        XXXX XXXX      = MMMM x CCCC
	//        XXXX XXXX      = CCCC x MMMM
	// + YYYY YYYY           = MMMM x MMMM
	//   -------------------
	//   ZZZZ ZZZZ ZZZZ ZZZZ
	//          ^^ ^^
	//     Keep these bytes
	m = msg;
	c = chain;
	w = c * c;
	x = (m * c) << 1;
	y = m * m;
	z = (w >> 32) + x + (y << 32);
	return z >> 16;
	}

int main
	(int argc,
	 char *argv[])
	{
	unsigned long int m;
	unsigned long int c;
	unsigned long int cprime;
	unsigned long int b;
	unsigned long int bprime;

	if (argc != 3)
		{
		fprintf (stderr, "Usage: test02 <m> <c>\n");
		return 1;
		}

	sscanf (argv[1], "%lx", &m);
	sscanf (argv[2], "%lx", &c);
	cprime = update (m, c);

	printf ("M         C         C'\n");
	printf ("--------  --------  --------\n");
	printf ("%08x  %08x  %08x\n", m, c, cprime);

	for (b = 0x80000000; b != 0; b >>= 1)
		{
		bprime = update (m^b, c);
		printf
			("%08x  %08x  %08x%s\n",
			 m^b, c, bprime, bprime == cprime ? "  ***" : "");
		}

	for (b = 0x80000000; b != 0; b >>= 1)
		{
		bprime = update (m, c^b);
		printf
			("%08x  %08x  %08x%s\n",
			 m, c^b, bprime, bprime == cprime ? "  ***" : "");
		}

	return 0;
	}
