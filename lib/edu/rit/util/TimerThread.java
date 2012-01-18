//******************************************************************************
//
// File:    TimerThread.java
// Package: edu.rit.util
// Unit:    Class edu.rit.util.TimerThread
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

/**
 * Class TimerThread encapsulates a thread that does the timing for {@link Timer
 * </CODE>Timer<CODE>}s and performs {@link TimerTask </CODE>TimerTask<CODE>}s'
 * actions when timeouts occur.
 * <P>
 * A timer is created by calling a timer thread's <TT>createTimer()</TT> method,
 * giving a timer task to associate with the timer. Multiple timers may be
 * created under the control of the same timer thread. The timer thread will
 * perform the actions of its timers' timer tasks one at a time at the proper
 * instants (by causing each timer to call its timer task's <TT>action()</TT>
 * method). Note that the timer tasks of a single timer thread are performed
 * sequentially, which may cause some timer tasks' actions to be delayed
 * depending on how long other timer tasks' actions take to execute. If
 * necessary, consider running separate timers in separate timer threads so the
 * timer tasks' actions run concurrently.
 * <P>
 * Class TimerThread does <I>not</I> offer real-time guarantees. It merely makes
 * a best-effort attempt to perform each timer task's actions as soon as
 * possible after the timeouts occur.
 * <P>
 * A TimerThread is just a {@link java.lang.Thread </CODE>Thread<CODE>}. After
 * constructing a timer thread, you can mark it as a daemon thread if you want.
 * You must also call the timer thread's <TT>start()</TT> method, or no timeouts
 * will occur. To gracefully stop a timer thread, call the <TT>shutdown()</TT>
 * method.
 * <P>
 * To simplify writing programs with multiple objects that all use the same
 * timer thread, the static <TT>TimerThread.getDefault()</TT> method returns a
 * single shared instance of class TimerThread. The default timer thread is
 * marked as a daemon thread and is started automatically. The default timer
 * thread is not created until the first call to
 * <TT>TimerThread.getDefault()</TT>.
 * <P>
 * Classes {@link Timer </CODE>Timer<CODE>}, {@link TimerTask
 * </CODE>TimerTask<CODE>}, and TimerThread provide capabilities similar to
 * classes java.util.Timer and java.util.TimerTask. Unlike the latter, they also
 * provide the ability to stop and restart a timer and the ability to deal with
 * race conditions in multithreaded programs.
 *
 * @author  Alan Kaminsky
 * @version 29-Jun-2004
 */
public class TimerThread
	extends Thread
	{

// Hidden data members.

	/**
	 * Circular doubly linked list of timers that are started, in ascending
	 * order of timeout. myTimerList points to a sentinel timer object with
	 * timeout = Long.MAX_VALUE at the end of the list.
	 */
	private Timer myTimerList;

	/**
	 * True if this timer thread is running, false if it's shut down.
	 */
	private boolean iamRunning = true;

// Hidden static data members.

	/**
	 * The default timer thread.
	 */
	private static TimerThread theDefaultTimerThread = null;

// Exported constructors.

	/**
	 * Construct a new timer thread. After constructing it, you must call the
	 * timer thread's <TT>start()</TT> method, or no timeouts will occur.
	 */
	public TimerThread()
		{
		super();
		myTimerList = new Timer();
		myTimerList.myPredecessor = myTimerList;
		myTimerList.mySuccessor = myTimerList;
		myTimerList.myTimeout = Long.MAX_VALUE;
		}

// Exported operations.

	/**
	 * Get the default timer thread, a single shared instance of class
	 * TimerThread. The default timer thread is marked as a daemon thread and is
	 * started automatically. The default timer thread is not created until the
	 * first call to <TT>TimerThread.getDefault()</TT>.
	 *
	 * @return  Default timer thread.
	 */
	public static synchronized TimerThread getDefault()
		{
		if (theDefaultTimerThread == null)
			{
			theDefaultTimerThread = new TimerThread();
			theDefaultTimerThread.setDaemon (true);
			theDefaultTimerThread.start();
			}
		return theDefaultTimerThread;
		}

	/**
	 * Create a new timer associated with the given timer task and under the
	 * control of this timer thread. When the timer is triggered, this timer
	 * thread will cause the timer to call the given timer task's
	 * <TT>action()</TT> method.
	 *
	 * @param  theTimerTask  Timer task.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTimerTask</TT> is null.
	 */
	public Timer createTimer
		(TimerTask theTimerTask)
		{
		return new Timer (this, theTimerTask);
		}

	/**
	 * Shut down this timer thread.
	 */
	public void shutdown()
		{
		synchronized (this)
			{
			iamRunning = false;
			notifyAll();
			}
		}

	/**
	 * Perform this timer thread's processing. (Never call the <TT>run()</TT>
	 * method yourself!)
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if some thread other than this timer
	 *     thread called the <TT>run()</TT> method.
	 */
	public void run()
		{
		// Only this timer thread itself can call the run() method.
		if (Thread.currentThread() != this)
			{
			throw new IllegalStateException
				("Wrong thread called the run() method");
			}

		try
			{
			while (iamRunning)
				{
				long now = System.currentTimeMillis();

				synchronized (this)
					{
					// If timer list is empty, wait until notified.
					if (myTimerList.mySuccessor.myTimeout == Long.MAX_VALUE)
						{
						wait();
						now = System.currentTimeMillis();
						}

					// If timer list is not empty and first timeout is in the
					// future, wait until timeout or until notified.
					else
						{
						long waitTime = myTimerList.mySuccessor.myTimeout - now;
						if (waitTime > 0L)
							{
							wait (waitTime);
							now = System.currentTimeMillis();
							}
						}
					}

				// Process all timers at the head of the timer list that are now
				// triggered.
				Timer theTimer;
				do
					{
					theTimer = null;

					// Pull a triggered timer off the head of the timer list.
					synchronized (this)
						{
						if (myTimerList.mySuccessor.myTimeout <= now)
							{
							theTimer = myTimerList.mySuccessor;
							theTimer.myPredecessor.mySuccessor =
								theTimer.mySuccessor;
							theTimer.mySuccessor.myPredecessor =
								theTimer.myPredecessor;
							theTimer.myPredecessor = null;
							theTimer.mySuccessor = null;
							}
						}

					// Perform the triggered timer's action. Do this outside the
					// synchronized block, or a deadlock may happen if a timer
					// is restarted.
					if (theTimer != null)
						{
						theTimer.trigger (now);
						}
					}
				while (theTimer != null);
				}
			}

		catch (InterruptedException exc)
			{
			System.err.println ("TimerThread interrupted");
			exc.printStackTrace (System.err);
			}
		}

// Hidden operations.

	/**
	 * Schedule the given timer.
	 *
	 * @param  theTimer    Timer.
	 */
	synchronized void schedule
		(Timer theTimer)
		{
		// Take theTimer out of the timer list if necessary.
		deschedule (theTimer);

		// Set p and q to the timers that will come before and after theTimer.
		Timer q = myTimerList.mySuccessor;
		while (theTimer.myTimeout > q.myTimeout)
			{
			q = q.mySuccessor;
			}
		Timer p = q.myPredecessor;

		// Insert the timer into the timer list.
		p.mySuccessor = theTimer;
		theTimer.myPredecessor = p;
		theTimer.mySuccessor = q;
		q.myPredecessor = theTimer;

		// Wake up the timer thread.
		notifyAll();
		}

	/**
	 * Deschedule the given timer.
	 *
	 * @param  theTimer    Timer.
	 */
	synchronized void deschedule
		(Timer theTimer)
		{
		// If theTimer is not in the timer list, do nothing.
		if (theTimer.myPredecessor == null) return;

		// Unlink theTimer from the timer list.
		theTimer.myPredecessor.mySuccessor = theTimer.mySuccessor;
		theTimer.mySuccessor.myPredecessor = theTimer.myPredecessor;
		theTimer.myPredecessor = null;
		theTimer.mySuccessor = null;

		// Wake up the timer thread.
		notifyAll();
		}

// Unit test main program.

//	/**
//	 * Helper class for unit test main program.
//	 */
//	private static class Message
//		implements TimerTask
//		{
//		private String myMessage;
//
//		public Message
//			(String theMessage)
//			{
//			myMessage = theMessage;
//			}
//
//		public void action
//			(Timer theTimer)
//			{
//			System.out.println (myMessage);
//			}
//		}
//
//	/**
//	 * Unit test main program.
//	 */
//	public static void main
//		(String[] args)
//		{
//		try
//			{
//			TimerThread theTimerThread = new TimerThread();
//			theTimerThread.start();
//
//			Timer timer1 =
//				theTimerThread.createTimer
//					(new Message ("Timer 1 triggered"));
//
//			Timer timer2 =
//				theTimerThread.createTimer
//					(new Message ("Timer 2 triggered"));
//
//			Timer timer3 =
//				theTimerThread.createTimer
//					(new Message ("Timer 3 triggered"));
//
//			timer1.start (1000L, 1000L);
//			timer2.start (5000L, 5000L);
//			timer3.start (10000L);
//			}
//		catch (Throwable exc)
//			{
//			System.err.println ("edu.rit.util.TimerThread: Uncaught exception");
//			exc.printStackTrace (System.err);
//			}
//		}

	}
