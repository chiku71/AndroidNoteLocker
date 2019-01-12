package com.apps71.notelocker;

class InactivityWatcherThread extends Thread
{
    private long pauseStartTime; // in milliseconds
    private long timeOutPeriod; // In milliseconds

    // Below variable is marked as volatile as it is going to be changed from another Thread
    private volatile boolean keepRunning = true;

    public InactivityWatcherThread(long timeOutPeriod)
    {
        this.pauseStartTime = System.currentTimeMillis();
        this.timeOutPeriod = timeOutPeriod;
    }

    @Override
    public void run()
    {
        do
        {
            try
            {
                Thread.sleep(2000); //check every 2 seconds
            }
            catch (InterruptedException ex)
            {
                // InactivityWatcherThread has ben interrupted
            }

            long idleTime = System.currentTimeMillis() - this.pauseStartTime;

            if (idleTime > this.timeOutPeriod)
            {
                // Time out occurred ...
                this.keepRunning = false;

                NoteLockerAppActivity.lockCurrentScreen = true;
            }
        }
        while (this.keepRunning);

        // Thread running completed ...
    }


    protected void terminate()
    {
        // Terminate/Stop this running thread.
        this.keepRunning = false;
    }

}
