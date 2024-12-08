public class Main {
   public static void main(String[] args) {
      try {
         Scheduler scheduler = new SRTF();
         scheduler.run();
         scheduler.plot();

      } catch(Exception e) {
         System.out.println("Error: " + e.getMessage());
      }
   }
}
