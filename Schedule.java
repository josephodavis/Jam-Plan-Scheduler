import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Schedule {
    public ArrayList<Student> students;
    // full classes
    public ArrayList<Class> classesF;
    // half classes
    public ArrayList<Class> classesH;
    // all classes
    public ArrayList<Class> allClasses;

    public String csv;
    public int fullClasses;

    public Schedule(String path, int numFullClasses){
        csv = path;
        fullClasses = numFullClasses;
        students = new ArrayList<Student>();
        classesF = new ArrayList<Class>();
        classesH = new ArrayList<Class>();
        allClasses = new ArrayList<Class>();
    }

    // the function prepare adds all of the classes in their proper places and initializes all of the students with their preferences
    public void prepare(){
        int numCols = countCols(",");

        // going through each column, initializing people's priorities
        for (int i = 0; i < numCols; i++){
            String[] col = readCol(i, ",");

            // last names
            if (i == 0){
                for (int j = 1; j < col.length; j++){
                    String lastName = col[j];
                    // first pass, add all students to array list
                    students.add(new Student());
                    students.get(j-1).name = lastName;
                }
            }
            // first names
            else if (i == 1){
                for (int j = 1; j < col.length; j++){
                    String firstName = col[j];
                    students.get(j-1).name = firstName + " " + students.get(j-1).name;
                }
            }
            // IDs
            else if (i == 2){
                for (int j = 1; j < col.length; j++){
                    String ID = col[j];
                    students.get(j-1).ID = ID;
                }
            }
            // grades
            else if (i == 3){
                for (int j = 1; j < col.length; j++){
                    int grade = -1;
                    if (col[j].equals("9")){
                        grade = 9;
                    }
                    else if (col[j].equals("10")){
                        grade = 10;
                    }
                    else if (col[j].equals("11")){
                        grade = 11;
                    }
                    else if (col[j].equals("12")){
                        grade = 12;
                    }

                    students.get(j-1).grade = grade;
                }
            }
            // full or half day preference
            else if (i == 4){
                for (int j = 1; j < col.length; j++){
                    String s = col[j];
                    boolean preference = false;

                    if (s.equals("half")){
                        preference = true;
                    }
                    students.get(j-1).halfPreferred = preference;
                }
            }
            // full classes
            else if (i < (fullClasses+5)){
                String name = col[0];
                Class c = new Class(15, name, false);
                for (int j = 1; j < col.length; j++){
                    if (col[j] != ""){
                        int ranking = Integer.parseInt(col[j]);
                        students.get(j-1).full[ranking-1] = c;
                    }
                }
                classesF.add(c);
                allClasses.add(c);
            }
            // half classes
            else {
                String name = col[0];
                // half classes have double the capacity, since they are in the morning and afternoon
                Class c = new Class(30, name, true);
                for (int j = 1; j < col.length; j++){
                    if (col[j] != ""){
                        int ranking = Integer.parseInt(col[j]);
                        students.get(j-1).half[ranking-1] = c;
                    }
                }
                classesH.add(c);
                allClasses.add(c);
            }
        }
        
        // testing
        // for (Student s : students){
        //   System.out.print(s.name + ": ");
        //   for (Class c : s.half){
        //     if (c != null){
        //       System.out.print(c.ID + ", ");
        //     }
        //   }
        //   System.out.println();
        //   System.out.println();
        // }
    }

    // the function readCol takes the data from a column in a csv file and returns it in the form of a string array
    public String[] readCol(int col, String delimiter){
        String data[];
        String curLine;
        ArrayList<String> colData = new ArrayList<String>();

        try {
            FileReader fr = new FileReader(csv);
            BufferedReader br = new BufferedReader(fr);

            while ((curLine = br.readLine()) != null){
                data = curLine.split(delimiter,-1);
                colData.add(data[col]);
            }

            br.close();
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }

        return colData.toArray(new String[0]);
    }

  // the function countCols returns the number of columns in a csv file
    public int countCols(String delimiter){
        String data[];
        String curLine;
        int res = 0;
        try {
            FileReader fr = new FileReader(csv);
            BufferedReader br = new BufferedReader(fr);

            if ((curLine = br.readLine()) != null){
                data = curLine.split(delimiter);
                res = data.length;
            }

            br.close();

        }
        catch (Exception e){
            System.out.println(e);
            return 0;
        }

        return res;
    }
}

