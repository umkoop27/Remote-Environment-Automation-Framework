package cooltimer;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cooltimer.gen.CustomDescriptor;

public class CoolTimer extends CustomDescriptor
{	
		
	///////////////////////
	// INSTANCE VARIABLES
	///////////////////////

	private Timer secondCounterTimer;
	private boolean timerCounting;
	private int timerValue;
	private static final boolean DEBUG = true;
	
	@Override
	public void setTimerAction(String maxTime)
	{
		timerValue = Integer.parseInt(maxTime);;
		if (!timerCounting) 
		{
			timerCounting = true;

			TimerTask secondCounterTask = new TimerTask() {

				@Override
				public void run() {
					didTrigger(TIMER_EXPIRED);	
				}
			};

			secondCounterTimer = new Timer();
			secondCounterTimer.schedule(secondCounterTask, 1000 * timerValue, 1000 * timerValue);
		}

	}

	@Override
	public void stopTimerAction()
	{
		if (timerCounting) 
		{
			cancelTimer();
			didTrigger(TIMER_EXPIRED);
		}
	}

	public void cancelTimer()
	{
		if (timerCounting) 
		{
			timerCounting = false;
			secondCounterTimer.cancel();
			if (DEBUG)
			{
				System.out.println("Cancelled " + mPeripheral.getName());
			}
		}
	}
	
	public void waitUntil(final Date date, final String[] arguments) {
        
		Thread waitThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				if (DEBUG) {
					DateFormat dFormat = DateFormat.getInstance();
					System.out.println("waiting for date: " + dFormat.format(date));
				}
				
				final Object o = new Object();
		        TimerTask tt = new TimerTask() {
		            public void run() {
		                synchronized (o) {
		                    o.notify();
		                }
		            }
		        };
		        Timer t = new Timer();
		        t.schedule(tt, date);
		        synchronized(o) {
		            try {
		                o.wait();		                
		                didTrigger(CURRENT_DATE, arguments);
		            } catch (InterruptedException ie) {}
		        }
		        t.cancel();
		        t.purge();
			}
		});
		
		waitThread.start();		
    }

	@Override
	protected boolean validateAction(int actionID, String... arguments) 
	{
		if (SET_TIMER == actionID) 
		{
			if (arguments.length != 1) 
			{
				return false;
			} else 
			{				
				try 
				{
					Integer.parseInt(arguments[0]);
				} catch (NumberFormatException nfe)
				{
					return false;
				}				
			}
		}
		
		return true;
	}

	@Override
	protected boolean validateTrigger(int triggerID, String... arguments) 
	{
		if (CURRENT_DATE == triggerID) 
		{
			if (arguments.length != 1)
			{
				return false;
			} else {
				try {
					String dateString = arguments[0];
					if (DEBUG) {
						System.out.println("validate date: " + dateString);
					}
					DateFormat dFormat = DateFormat.getInstance(); // not sure how you want to format dates shawn
					Date delayUntilDate = dFormat.parse(dateString);
					waitUntil(delayUntilDate, arguments);			
					
				} catch (ParseException pe) {
					pe.printStackTrace();
					return false;
				} catch (ClassCastException cce) {
					cce.printStackTrace();
					return false;
				}
				
			}
		}
		
		return true;
	}

	@Override
	public String getTimerValueStatus() {
		// TODO Auto-generated method stub
		return "this is the timer status!!";
	}

	@Override
	public String getPauliscoolStatus() {
		// TODO Auto-generated method stub
		return "Paul Is Cool";
	}
}
