class Test{
    Test f;

    public static void arrayCheck(){
        int [] b = new int [10];
        // int [] a;
        Test v1 = new Test();
        Test v2 = new Test();
        // int i  = 0;
        v2.f = v1;
        v2.f.toString();
        // a = b;
        // a[0] = 1;
        // a[1] = 2;
        // while( i < 10){
        //     a[i] = a[i+1];
        //     i++;
        // }
        int [] c = new int []{1,2,3};
        int [] a;
        int i = 0;
        if(b[0] == 1){
            a = b;
        } else {
            a = c;
        }

        while( i < 2) {
            a[i] = a[i+1];
            i++;
        }
    }
    public static void main(String [] args){
        Test.arrayCheck();
    }
}
