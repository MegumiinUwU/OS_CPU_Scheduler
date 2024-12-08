public class Process {
   private String name;
   private int arrivalTime;
   private int burstTime;
   private int priority;
   private int age;
   private int turnAroundTime;
   private int waitingTime;

   //priority scheduling
   public Process(String name, int arrivalTime, int burstTime, int priority) {
      this.name=  name;
      this.arrivalTime = arrivalTime;
      this.burstTime = burstTime;
      this.priority = priority;
      this.age = 0;
      this.turnAroundTime = -1;
      this.waitingTime = -1;
   }


   //SJF and SRTF
   public Process(String name, int arrivalTime, int burstTime) {
      this.name=  name;
      this.arrivalTime = arrivalTime;
      this.burstTime = burstTime;
      this.priority = 0;
      this.age = 0;
      this.turnAroundTime = -1;
      this.waitingTime = -1;
   }

   public Process(Process p) {
      this.name=  new String(p.getName());
      this.arrivalTime = p.getArrivalTime();
      this.burstTime = p.getBurstTime();
      this.priority = p.getPriority();
      this.age = p.getAge();
      this.turnAroundTime =p.getTurnAroundTime();
      this.waitingTime = p.getWaitingTime();
   }



   //getters and setters
   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getArrivalTime() {
      return this.arrivalTime;
   }

   public void setArrivalTime(int arrivalTime) {
      this.arrivalTime = arrivalTime;
   }

   public int getBurstTime() {
      return this.burstTime;
   }

   public void setBurstTime(int burstTime) {
      this.burstTime = burstTime;
   }

   public int getPriority() {
      return this.priority;
   }

   public void setPriority(int priority) {
      this.priority = priority;
   }

   public int getAge() {
      return this.age;
   }

   public void setAge(int age) {
      this.age = age;
   }

   public int getTurnAroundTime() {
      return this.turnAroundTime;
   }

   public void setTurnAroundTime(int turnAroundTime) {
      this.turnAroundTime = turnAroundTime;
   }

   public int getWaitingTime() {
      return this.waitingTime;
   }

   public void setWaitingTime(int waitingTime) {
      this.waitingTime = waitingTime;
   }


   //priority for sjf and srtf
   public int getCustomPriority() {
      return this.getBurstTime() - this.getAge();
   }

}
