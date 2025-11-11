import java.util.*;
import java.io.*;
public class Runner{
  public static void main(String[] args) {
    // create a schedule object based off of the csv with the data in it (test.csv in this case)
    Schedule jamPlan = new Schedule("test.csv", 26);
    jamPlan.prepare();

    // createTestCSV();


    // FIRST PASS - after first 5, move them to other class length --> check if their preference is less than 6 before adding them to pqueue
    for (int i = 1; i < 6; i++){
      // for each full class(classesF)
      for (Class c : jamPlan.classesF){
        PQueue pq = new PQueue(jamPlan.students.size(), c.ID);
        // add every student who prefers full classes to the priority queue
        for (Student s : jamPlan.students){
          if (!s.halfPreferred){
            pq.insert(s);
          }
        }
  
        // while class is not full and still students to add
        while (c.numStudents < c.capacity && !pq.isEmpty()){
          Student cur = pq.remove();

          // if the student has a preference too high to be added this early in the scheduling stage
          if (cur.getPreference(c.ID) >= i){
            break;
          }

          // add student to class and remove from arraylist of all students
          c.students[c.numStudents] = cur;
          c.numStudents++;
  
          jamPlan.students.remove(cur);
        }
      }
    }

    for (int i = 1; i < 6; i++){
      // for each half class(classesH)
      for (Class c : jamPlan.classesH){
        PQueue pq = new PQueue(jamPlan.students.size(), c.ID);

        // add every student that prefers half day classes to the priority queue
        for (Student s : jamPlan.students){
          if (s.halfPreferred){
            if (!c.studentInClass(s)){
              pq.insert(s);
            }
          }
        }
  
        while (c.numStudents < c.capacity && !pq.isEmpty()){
          Student cur = pq.remove();

          if (cur.getPreference(c.ID) >= i){
            break;
          }

          c.students[c.numStudents] = cur;
          c.numStudents++;

          // increment number of half day classes the student has
          cur.halfCount++;
  
          // take student out of arraylist only if they already have 2 half day classes
          if (cur.halfCount == 2){
            jamPlan.students.remove(cur);
          }
        }
      }
    }


    // SECOND PASS - if there are still students left who can't get their prefered day length because they don't fit into classes that 
    // they set as their top five of the preferred length
    if (!jamPlan.students.isEmpty()){
      // go through all classes
      for (Class c : jamPlan.allClasses){
        // if this is a halfDay class
        if (c.isHalfDay){
          PQueue pq = new PQueue(jamPlan.students.size(), c.ID);
          for (Student s : jamPlan.students){
            if (!c.studentInClass(s)){
              pq.insert(s);
            }
          }
    
          while (c.numStudents < c.capacity && !pq.isEmpty()){
            Student cur = pq.remove();

            // if student is already in this half day class, remove them
            for (Student s : c.students){
              if (cur == s){
                cur = pq.remove();
                break;
              }
            }

            c.students[c.numStudents] = cur;
            c.numStudents++;
            cur.halfCount++;
    
            if (cur.halfCount == 2){
              jamPlan.students.remove(cur);
            }
          }
        }
        // if this is a full day
        else {
          PQueue pq = new PQueue(jamPlan.students.size(), c.ID);
          for (Student s : jamPlan.students){
            // if student is not already in a half day class
            if (s.halfCount == 0){
              pq.insert(s);
            }
          }

          while (c.numStudents < c.capacity && !pq.isEmpty()){
            Student cur = pq.remove();
            c.students[c.numStudents] = cur;
            c.numStudents++;
    
            jamPlan.students.remove(cur);
          }
        }

      }
    }


    // FINAL PASS - put leftover people in random classes if nothing they have ranked has space
    if (!jamPlan.students.isEmpty()){
      
      Iterator<Student> iter = jamPlan.students.iterator();
      // for each student who needs a class
      while (iter.hasNext()){
        Student s = iter.next();

        // if in a full class already
        boolean chosen = false;

        // if they can be in a full class
        if (s.halfCount == 0){
          for (Class c : jamPlan.classesF){
            // if class is not at capacity, add student to class and remove them from the arraylist
            if (c.numStudents < c.capacity){
              c.students[c.numStudents] = s;
              c.numStudents++;

              iter.remove();
              chosen = true;
              break;
            }
          }
        }

        // if there are no open full classes, go to half classes
        if (!chosen){

          for (Class c : jamPlan.classesH){
            // if student is already in this half day class
            boolean inClass = false;
            for (Student cur : c.students){
              if (s == cur){
                inClass = true;
                break;
              }
            }
            // if not in the class, add them
            if (!inClass){
              if (c.numStudents < c.capacity){
                c.students[c.numStudents] = s;
                c.numStudents++;
                s.halfCount++;
        
                // if student 2 half classes, remove them from the arraylist
                if (s.halfCount == 2){
                  iter.remove();
                  break;
                }
              }
            }
          }
        }
      }
    }

    // createResultCSV(jamPlan);

    // print average 
    System.out.println("average preferences");
    System.out.println("full: " + averageScore(jamPlan, false));
    System.out.println("half: " + averageScore(jamPlan, true));

  }




  // the function createCSV will create a CSV file with testing data of the proper size
  public static void createTestCSV(){
    // desired file path
    String csvFile = "/Users/josephdavis/PDS/final-project-josephodavis/test.csv";

    // headers of the csv file
    String[] headers = new String[65];

    for (int i = 0; i < 65; i++){
      if (i == 0){
        headers[i] = "Student Last Name";
      }
      else if (i == 1){
        headers[i] = "Student First Name";
      }
      else if (i == 2){
        headers[i] = "Student ID";
      }
      else if (i == 3){
        headers[i] = "Student Grade Level";
      }
      else if (i == 4){
        headers[i] = "Preference for a full or half-day class";
      }
      // 26 full day courses
      else if (i > 4 && i < 31){
        headers[i] = "Course " + (char) (i+60);
      }
      // half day courses
      else {
        headers[i] = "Course " + (char) (i+34) + (char) (i+34);
      }
    }

    String[][] data = new String[800][65];

    // for each student
    for (int i = 0; i < 800; i++){
      // add their full preferences and their half preferences
      ArrayList<String> fullChoices = new ArrayList<String>();
      for (int k = 0; k < 26; k++){
        if (k < 10){
          fullChoices.add(Integer.toString(k+1));
        }
        else {
          fullChoices.add("");
        }
      }

      ArrayList<String> halfChoices = new ArrayList<String>();
      for (int k = 0; k < 34; k++){
        if (k < 10){
          halfChoices.add(Integer.toString(k+1));
        }
        else {
          halfChoices.add("");
        }
      }

      // add student characteristics
      for (int j = 0; j < 65; j++){
        if (j == 0){
          data[i][j] = Integer.toString(i);
        }
        else if (j == 1){
          data[i][j] = "Student";
        }
        else if (j == 2){
          data[i][j] = Integer.toString(i+1000);
        }
        else if (j == 3){
          if (i < 200){
            data[i][j] = "12";
          }
          else if (i >= 200 && i < 400){
            data[i][j] = "11";
          }
          else if (i >= 400 && i < 600){
            data[i][j] = "10";
          }
          else if (i >= 600 && i < 800){
            data[i][j] = "9";
          }
        }
        else if (j == 4){
          double num = Math.random();
          boolean pref = false;
          if (num > 0.5){
            pref = true;
          }

          if (pref){
            data[i][j] = "half";
          }
          else {
            data[i][j] = "full";
          }
        }
        else if (j > 4 && j < 31){
          int rand = (int) (Math.random()*fullChoices.size());
          data[i][j] = fullChoices.get(rand);
          fullChoices.remove(rand);
        }
        else {
          int rand = (int) (Math.random()*halfChoices.size());
          data[i][j] = halfChoices.get(rand);
          halfChoices.remove(rand);
        }
      }
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) { 
      // write headers 
      writer.write(String.join(",", headers)); 
      writer.newLine(); 

      // write data 
      for (String[] row : data) { 
        writer.write(String.join(",", row)); 
        writer.newLine(); 
      } 

      System.out.println("CSV file created successfully."); 
    } catch (IOException e) { 
      e.printStackTrace(); 
    }
  } 

  

  // the function createResultCSV takes the plan and creates the CSV containing the results for each class
  public static void createResultCSV(Schedule plan){
  
    // file path
    String csvFile = "/Users/josephdavis/PDS/final-project-josephodavis/res.csv";

    // headers of classes
    String[] headers = new String[plan.allClasses.size()];

    for (int i = 0; i < plan.allClasses.size(); i++){
      headers[i] = plan.allClasses.get(i).ID;
    }

    // half classes have 30 students at the most
    String[][] data = new String[30][plan.allClasses.size()];

    // add students to proper places
    for (int i = 0; i < 30; i++){
      for (int j = 0; j < plan.allClasses.size(); j++){
        if (!plan.allClasses.get(j).isHalfDay && i >= 15){
          continue;
        }
        if (plan.allClasses.get(j).students[i] != null){
          String name = plan.allClasses.get(j).students[i].name;
          String ID = plan.allClasses.get(j).students[i].ID;
          data[i][j] = ID + " " + name;
        }
      }
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) { 
      // write headers 
      writer.write(String.join(",", headers)); 
      writer.newLine(); 

      // write data 
      for (String[] row : data) { 
        writer.write(String.join(",", row)); 
        writer.newLine(); 
      } 

      System.out.println("CSV file created successfully."); 
    } catch (IOException e) { 
      e.printStackTrace(); 
    }
  }

  // the function averageScore takes the plan and returns the average preference for the classes of students
  public static double averageScore(Schedule plan, boolean half){
    double res = 0;
    int numStudents = 0;

    if (half){
      for (Class c : plan.classesH){
        for (Student s : c.students){
          if (s != null){
            // skip people who get a class they didn't rank --> Integer.MAX_VALUE
            if (s.getPreference(c.ID) > 10){
              continue;
            }
            res += s.getPreference(c.ID) + 1;
            numStudents++;
          }
        }
      }
    }
    else {
      for (Class c : plan.classesF){
        for (Student s : c.students){
          if (s != null){
            // skip people who get a class they didn't rank --> Integer.MAX_VALUE
            if (s.getPreference(c.ID) > 10){
              continue;
            }
            res += s.getPreference(c.ID) + 1;
            numStudents++;
          }
        }
      }
    }

    res /= numStudents;

    return res;
  }
}