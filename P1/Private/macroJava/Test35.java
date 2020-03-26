// Testing if comments are ignored.
#define PRINT(a) { System.out.println(a); } 
class Test35{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    public int TestMethod(){
        PRINT(1);
        return 999 ;
    }
}
