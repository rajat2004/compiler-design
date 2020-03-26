#define INCRPRINT(a) { a = a + 1; System.out.println(a); }
class Test36{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    int [] array;
    public int TestMethod(){
        array = new int[10] ;
        array[5] = 10;
        if ((array[5]) <= 10)
        {
            INCRPRINT(array[5]);
        }
        return 10 ;
    }
}
