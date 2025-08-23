package in.canaris.cloud.utils;


import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import java.util.concurrent.TimeUnit;

public class PSMultipleCommands {
	
	
	public void timeee()
	{
		
		 //long hours = TimeUnit.SECONDS.toHours(up_second_day);
		 
		 
		long up_second_day=60000;
		 try {
             int day = (int)TimeUnit.SECONDS.toDays(up_second_day);
             long hours = TimeUnit.SECONDS.toHours(up_second_day) - (day * 24);
             long minute = TimeUnit.SECONDS.toMinutes(up_second_day) - TimeUnit.SECONDS.toHours(up_second_day) * 60L;
             long second = TimeUnit.SECONDS.toSeconds(up_second_day) - TimeUnit.SECONDS.toMinutes(up_second_day) * 60L;
            String var_uptime = day + " Days, " + hours + " Hours, " + minute + " Minutes, " + second + " Seconds";
           } catch (Exception ex) {
             System.out.println("excep 333 util:" + ex);
           } 
		 
		 
		 
	}
	  public static void main(String[] args) {
	        //Creates PowerShell session (we can execute several commands in the same session)
	        PowerShell powerShell = null;
	        try {
	            powerShell = PowerShell.openSession();
	            //Execute a command in PowerShell session
	         //   PowerShellResponse response = powerShell.executeCommand("Get-Process");

	            //Print results
	           // System.out.println("List Processes:" + response.getCommandOutput());

	            //Execute another command in the same PowerShell session
	            PowerShellResponse response = powerShell.executeCommand("Get-WmiObject Win32_BIOS");

	            //Print results
	            System.out.println("BIOS information:" + response.getCommandOutput());
	            //System.out.println("powerShell before:" + powerShell.);
	        } catch (PowerShellNotAvailableException ex) {
	            System.out.println("Exception:" + ex);
	            //Handle error when PowerShell is not available in the system
	            //Maybe try in another way?
	        } finally {
	            try {
	                if (powerShell != null) {
	                    powerShell.close();
	                }
	            } catch (Exception e) {
	                System.out.println("Exception:" + e);
	            }

	        }
	    }


}
