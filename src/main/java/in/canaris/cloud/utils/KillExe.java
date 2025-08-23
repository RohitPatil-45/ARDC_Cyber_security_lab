package in.canaris.cloud.utils;

public class KillExe {


    
    public void killKVMExe() {
        
        String execommand="taskkill /f /im KVMMonitor.exe";
       System.out.println("Command:" + execommand);
       int exitVal = 0;
       Process p = null;
       try {
           p = Runtime.getRuntime().exec(execommand);
           p.waitFor();
           exitVal = p.exitValue();
           System.out.println(execommand+"exit val kill health:"+exitVal);
       } catch (Exception ex) {
          // ex.printStackTrace();
           System.out.println("cmdException kill:" + ex);
           exitVal = -1;
       } finally {
           try {
               if (p != null) {
                   p.destroy();
               }
           } catch (Exception ee) {
               System.out.println("Exceptiom2:" + ee);
           }
       }
       
   }
}
