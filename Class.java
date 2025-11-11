public class Class {
    public String ID;
    public int capacity;

    // number of students in the class currently
    public int numStudents;

    public boolean isHalfDay;
    public Student[] students;

    public Class(int newcapacity, String newID, boolean half){
        capacity = newcapacity;
        numStudents = 0;
        ID = newID;
        isHalfDay = half;
        students = new Student[capacity];
    }

    // the function studentInClass takes a student and checks to see if the student is in the current class, returning a boolean
    public boolean studentInClass(Student s){
        for (Student check : students){
            if (s == check){
                return true;
            }
        }

        return false;
    }
}
