#define ASSIGN(a, b) { a = b;}
#define INCRPRINT(a) { a = a + 1; System.out.println(a); }
class Test18{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    int [] array;
    public int TestMethod(){
        array = new int[10] ;
        ASSIGN(array[5], 10);
        INCRPRINT((array[5]));
        return 10;
    }
}
