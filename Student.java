public class Student {
    public String name;
    public int grade;
    public String ID;

    public Class[] half;
    public Class[] full;
    // if the student prefers half day classes
    public boolean halfPreferred;
    // number of half day classes currently
    public int halfCount;

    // can implement at a later date, Dean Kuhl mentioned making sure students are not in a class they have already taken
    public String[] alreadyTaken;

    public Student(){
        full = new Class[10];
        half = new Class[10];
        halfCount = 0;
    }

    // returns index of classID in the student's preference array --> lower index means more preference
    public int getPreference(String classID){
        int res = -1;
        for (int i = 0; i < half.length; i++){
            if (half[i].ID == classID){
                res = i;
                break;
            }
        }

        // if not a half day class
        if (res == -1){
            for (int i = 0; i < full.length; i++){
                if (full[i].ID == classID){
                    res = i;
                    break;
                }
            }
        }

        // if not ranked
        if (res == -1){
            res = Integer.MAX_VALUE;
        }
        return res;
    }
}
