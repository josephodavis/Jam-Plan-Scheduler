public class PQueue {
    // inner array
    private Student[] _arr;

    // num allocated
    private int _allocated;

    // num filled
    private int _filled;

    private String classID;

    // constructor
    public PQueue(int num, String newClassID){
        _filled = -1;
        _allocated = num;
        _arr = new Student[_allocated];
        classID = newClassID;
    }

    public boolean isEmpty(){
        return _filled == -1;
    }
    
    public boolean isFull(){
        return _filled == _allocated-1;
    }

    public void insert(Student s){
        if (this.isFull()){
            System.out.println("full pqueue");
            return;
        }

        // check if empty, matters for priority queue since all values are null
        if (this.isEmpty()){
            _arr[0] = s;
        }
        else {
            for (int i = 0; i <= _filled; i++){
                // check grade
                if (s.grade == 12 && _arr[i].grade < 12){
                    for (int j = _filled+1; j > i; j--){
                        _arr[j] = _arr[j-1];
                    }
                    _arr[i] = s;
                    break;
                }
                else {
                    // check preferences --> lower value preferences are first
                    if (s.getPreference(classID) < _arr[i].getPreference(classID)){
                        for (int j = _filled+1; j > i; j--){
                            _arr[j] = _arr[j-1];
                        }
                        _arr[i] = s;
                        break;
                    }

                }
                
                if (i == _filled){
                    _arr[_filled+1] = s;
                }
    
            }
        }

        _filled++;
    }

    public Student remove(){
        Student res = _arr[0];
        // move everything over
        for (int i = 0; i < _filled; i++){
            _arr[i] = _arr[i+1];
        }
        _filled--;

        return res;
    }

    public Student peek(){
        return _arr[0];
    }
}