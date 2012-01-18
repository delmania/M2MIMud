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
	unsigned long int msg;
	unsigned long int chain;
	unsigned long int chainprime;

	if (argc != 3)
		{
		fprintf (stderr, "Usage: test01 <msg> <chain>\n");
		return 1;
		}

	sscanf (argv[1], "%lx", &msg);
	sscanf (argv[2], "%lx", &chain);

	chainprime = update (msg, chain);

	printf ("message = 0x%08x\n", msg);
	printf ("chain   = 0x%08x\n", chain);
	printf ("chain'  = 0x%08x\n", chainprime);

	printf ("Brute force search for message ...\n");
	msg = 0;
	do
		{
		if ((msg & 0xFFFFFF) == 0)
			{
			printf ("\rTrying 0x%08x", msg);
			fflush (stdout);
			}
		if (update (msg, chain) == chainprime)
			{
			printf ("\r\nMATCH! message = %08x\n", msg);
			}
		++ msg;
		}
	while (msg != 0);
	printf ("\n");

	return 0;
	}
