// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2019T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/** 
 * Represents an ER Patient at a Hospital
 * Each Patient has
 *     a name and
 *     initials,
 *     an arrival time,
 *     total waiting time
 *     total treatment time
 *  and has been examined by the triage team at reception who has assigned
 *     a priority (1 - 3)  and
 *     a list of treatments that Patient must have
 *       (scans, examinations, operations, etc)
 *     a list of the times that the patient will spend in each treatment
 *   NOTE: For the core, you can ignore all but the first treatment.
 * 
 *  Possible treatments:  ER, MRI, Surgery, X-ray Ultrasound

 * Constructor:
 *     Patient(arrivalTime, triagePriority)
 *         All the other fields are generated automatically on creation

 * Methods:
 *   compareTo(other)   -> int
 *   redraw(x, y)
 *   toString()         -> String
 *   getPriority()      -> int
 *   getWaitingTime()   -> int
 *   getTreatmentTime() -> int
 *
 *   waitForATick()
 *   advanceTreatmentByTick()
 *   completedCurrentTreatment() -> boolean
 *
 *   [THE FOLLOWING ARE NOT NEEDED FOR THE CORE]
 *   noMoreTreatments()    -> boolean  
 *   incrementTreatmentNumber()    
 *   getCurrentTreatment()       -> String (name of department for the treatment needed now)
 */

public class Patient implements Comparable<Patient>{

    private String name;
    private String initials;
    private int arrival;
    private int priority;  // 1 is highest priority, 3 is lowest priority
    private List<String> treatments = new ArrayList<String>();
    private List<Integer> treatmentTimes = new ArrayList<Integer>();
    private int currentTreatment;   // index of the treatment they are currently in or waiting for
    private int totalWaitTime;
    private int totalTreatmentTime;

    private Random random = new Random();  //used for generating the random values.

    /**
     * Construct a new Patient object
     * parameters are the arrival time and the priority.
     */
    public Patient(int time, int triPriority){
        arrival = time;
        priority = triPriority;
        makeRandomName();
        makeRandomTreatments();
        currentTreatment = 0; 
    }

    /** Return the priority */
    public int getPriority(){ return priority; }

    /** Return the total time spent waiting */
    public int getWaitingTime(){ return totalWaitTime; }

    /** Return the total time treatment in treatment */
    public int getTreatmentTime(){ return totalTreatmentTime; }

    /**
     * Make Patient wait for one tick.
     * added to patient's totalWaitTime
     */
    public void waitForATick(){
        totalWaitTime++;
    }

    /**
     * Reduce the remaining time of the current treatment by 1 tick. 
     * Throws an exception if the patient has already completed this treatment.
     *  or if the treatment at the head of the queue is finished
     *  (Ie, always ensure that the patient is not yet completed before calling) 
     */
    public void advanceTreatmentByTick(){
        if (completedCurrentTreatment()){
            throw new RuntimeException("patient has finished this treatment: "+this);
        }
        totalTreatmentTime++;
        treatmentTimes.set(currentTreatment, treatmentTimes.get(currentTreatment)-1);
    }

    /**
     * Return true if patient has completed their current treatment
     */
    public boolean completedCurrentTreatment(){
        if (currentTreatment >= treatments.size()){
            throw new RuntimeException("patient already completed all treatments: "+this);
        }
        return (treatmentTimes.get(currentTreatment)==0);
    }

    /**
     * Return true if the patient has completed all their treatments.
     */
    public boolean noMoreTreatments(){
        return (currentTreatment >= treatments.size());

    }

    /**
     * Return the name of the patient's current treatment
     * Throws an exception if the patient is all done.
     */
    public String getCurrentTreatment(){
        if (noMoreTreatments()){
            throw new RuntimeException("patient already completed: "+this);
        }
        return treatments.get(currentTreatment);
    }

    /**
     * Advance the index to the next treatment they need
     * Only changes the index in the Patient;
     * Doesn't move them to the next department!!
     * Throws an exception if the patient has no more treatments to visit
     */
    public void incrementTreatmentNumber(){
        if (noMoreTreatments()){
            throw new RuntimeException("patient already completed: "+this);
        }
        currentTreatment++;
    }

    /** 
     * Compare this Patient with another Patient to determine who should
     *  be treated first.
     * A patient should be earlier in the ordering if they should be treated first.
     * The ordering depends on the triage priority and the arrival time.
     */
    public int compareTo(Patient other){
        /*# YOUR CODE HERE */

        return 0;
    }

    /** toString: Descriptive string of most of the information in the patient*/
    public String toString(){
        StringBuilder ans = new StringBuilder(name);
        ans.append(", pri:").append(priority).append(", Ar:").append(arrival).append(", ").
        append("treat: ").append(totalTreatmentTime).append(" wait: ").append(totalWaitTime).append("\n    ");
        ans.append(treatments.size()).append(" treatments:");
        for (int i=0; i<treatments.size(); i++){
            if (i==currentTreatment){ans.append("*");}
            ans.append(treatments.get(i));
            if (treatmentTimes.get(i)>0){
                ans.append("(").append(treatmentTimes.get(i)).append(")");
            }
            ans.append(", ");
        }
        return ans.toString();
    }

    /**
     * Draw the patient:
     * 6 units wide, 28 units high
     * x,y specifies center of the base
     */
    public void redraw(double x, double y){
        if (priority == 1) UI.setColor(Color.red);
        else if (priority == 2) UI.setColor(Color.orange);
        else UI.setColor(Color.green);
        UI.fillOval(x-3, y-28, 6, 8);
        UI.fillRect(x-3, y-20, 6, 20);
        UI.setColor(Color.black);
        UI.drawOval(x-3, y-28, 6, 8);
        UI.drawRect(x-3, y-20, 6, 20);
        UI.setFontSize(10);
        UI.drawString(""+initials.charAt(0), x-3,y-10);
        UI.drawString(""+initials.charAt(1), x-3,y-1);
    }

    // Creating random names and treatments
    /**
     * Create a sequence of random treatments in trts and times
     * The sequence is influenced by priority of the patient:
     *  - high priority patients are more likely to need the operating
     *    theatre first, and a more complicated treatment sequence.
     *  low priority patients are more likely to just need an ER bed treatment.
     */
    private void makeRandomTreatments(){
        //choose number of treatments.
        //choose location and length of each treatment
        //
        // always start with ER
        // if (random.nextDouble()<0.8 ||    
        //     (priority==1 && random.nextDouble()<0.4)){
        treatments.add("ER");
        treatmentTimes.add(randomTreatmentTime(20));

        //many high priority patients need surgery.
        if ((priority==1  && random.nextDouble()<0.4) ||  
        (priority==2 && random.nextDouble()<0.1)){
            treatments.add("Surgery");
            treatmentTimes.add(randomTreatmentTime(60));
        }
        int n = (random.nextInt(5));   // number of additional treatments
        for (int i=0; i<n; i++){
            double num = random.nextDouble();
            if (num<0.05){
                treatments.add("MRI"); 
                treatmentTimes.add(randomTreatmentTime(200));
            }
            else if (num<0.1){
                treatments.add("Surgery"); 
                treatmentTimes.add(randomTreatmentTime(200));
            }
            else if (num<0.35){
                treatments.add("X-ray");
                treatmentTimes.add(randomTreatmentTime(20));
            }
            else if (num<0.6){
                treatments.add("Ultrasound");
                treatmentTimes.add(randomTreatmentTime(20));
            }
            else {
                treatments.add("ER");
                treatmentTimes.add(randomTreatmentTime(10));
            }
        }
        //remove immediate repetitions
        for (int i=0; i<treatments.size()-1; i++){
            if (treatments.get(i).equals(treatments.get(i+1))){
                treatments.remove(i+1);
                treatmentTimes.remove(i+1);
                i--;
            }
        }
    }

    /**
     * Returns a random treatment time with the given median
     * (half the treatment times will be below the median; half above).
     * Always at least 1 tick; but some take a long time.
     * (Based on a log-normal distribution, mu=0, sigma=0.6.
     *  increase sigma to spread it out more)
     */
    private int randomTreatmentTime(int median){
        double sigma = 0.6;
        double logNorm = Math.exp(sigma*random.nextGaussian());
        int m = Math.max(0, median-1);
        return (int)(1 + m*logNorm);
    }

    /**
     * Create a random name using the lists below
     */
    private void makeRandomName(){
        String name1 = names1[random.nextInt(names1.length)];
        String name2 = names2[random.nextInt(names2.length)];
        name = name1+" "+name2;
        initials = name1.substring(0,1)+name2.substring(0,1);
    }

    private String[] names1 =
        {"Lisa","Ramon","Janet","Catherine","Chris","Wokje","Thuong","Andrea",
            "Manjeet","Toby","Philip","Bing","Renee","Derek","David","John",
            "Christian","Yongxin","Charles","Michael","Colin","Helen","Mansoor","Rod",
            "Todd","Dan","Colin","Shirley","Alex","John","Michael","Peter",
            "Paul","Ian","Jenny","Bob","Jeffrey","Joanna","Kathryn","Andy",
            "Inge","Maree","Rosie","Joanne","Yau","Rebecca","Robyn","Christine",
            "Guy","Christina","Tirta","Ruiping","Victoria","Bernadette","Catherine","Mo",
            "Tom","Natalie","Harold","Dimitrios","Alexander","James","Michael",
            "Yu-Wei","Emily","Christian","Alia","Zohar","Kimberly","Ocean","Yi",
            "Jamy","Travis","Deborah","Kim","Linda","Gillian","Bronwyn","Bruce",
            "Miriam","Gillian","Jenny"};

    private String[] names2 =
        {"Alcock","Ansell","Armstrong","Bai","Bates","Biddle","Bradley","Brunt","Calvert",
            "Chawner","Cho","Clark","Coxhead","Cullen","Daubs","Day","Dinica","Downey",
            "Dunbar","Elinoff","Fortune","Gabrakova","Geng","Goreham","Groves","Hall",
            "Harris","Hodis","Horgan","Hunt","Jackson","Jones","Keane-Tuala","Khaled",
            "Kidman","Krtalic","Laufer","Levi","Locke","Mackay","Marquez","Maskill",
            "Maxwell","McCrudden","McGuinness","McMillan","Mei","Millington","Moore",
            "Murphy","Nelson","Niemetz","O'Hare","Owen","Pearce","Perris","Pirini",
            "Pratt","Randal","Reilly","Rimoni","Robinson","Ruck","Schipper",
            "Servetto","Shuker","Skinner","Speedy","Stevens","Sweet","Taylor",
            "Terreni","Timperley","Turetsky","Vignaux","Wallace","Welch","Wilson",
            "Ackerley","Adds","Anderson","Anslow","Antunes","Armstrong","Arnedo-Gomez",
            "Bacharach","Bai","Barrett","Baskerville","Bennett","Berman","Boniface",
            "Boston","Brady","Bridgman","Brunt","Buettner","Calhoun","Calvert",
            "Capie","Carmel","Chiaroni","Chicca","Chu","Chu","Clark",
            "Clayton","Coxhead","Craig","Cuffe","Cullen","Dalli","Das",
            "Davidson","Davies","Desai","Devue","Dinneen","Dmochowski","Downey",
            "Doyle","Dumitrescu","Dunbar","Elgort","Elias","Faamanatu-Eteuati","Feld",
            "Fraser","Frean","Galvosas","Gamble","Geng","George","Goh",
            "Goreham","Gregory","Grener","Guy","Haggerty","Hammond","Hannah",
            "Harvey","Haywood","Hodis","Hogg","Horgan","Horgan","Hubbard",
            "Hui","Ingham","Jack","Johnston","Johnston","Jordan","Joyce",
            "Keane-Tuala","Kebbell","Keyzers","Khaled","Kiddle","Kiddle","Kirkby",
            "Knewstubb","Kuehne","Lacey","Leah","Leggott","Levi","Lindsay",
            "Loader","Locke","Lynch","Ma","Mallett","Mares","Marriott",
            "Marshall","Maslen","Mason","Maxwell","May","McCarthy","McCrudden",
            "McDonald","McGregor","McKee","McKinnon","McNeill","McRae","Mercier",
            "Metuarau","Millington","Mitsotakis","Molloy","Moore","Muaiava","Muckle",
            "Natali","Neha","Newton","Nguyen","Nisa","Noakes-Duncan",
            "Ok","Overton","Park","Parkinson","Penetito","Perkins","Petkov",
            "Pham","Pivac","Plank","Price","Raman","Rees","Reichenberger",
            "Riad","Rice-Davis","Ritchie","Robb","Rofe","Rook","Ruegg",
            "Schick","Scott","Seals","Sheffield","Shewan","Sim","Simpson",
            "Smaill","Smith","Spencer","Stern","Susilo","Sutherland","Tariquzzaman",
            "Tatum","Te Huia","Te Morenga","Thirkell-White","Thomas","Tokeley","Trundle",
            "Van Belle","Van Rij","Vowles","Vry","Ward","Warren","White",
            "Whittle","Wilson","Wilson","Wood","Yao","Yu","Zareei",
            "de Saxe","de Sylva","van der Meer", "Woods","Yates","Zhang","van Zijl"
        };

}
