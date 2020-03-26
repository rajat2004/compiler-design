#define PRINTIF(a,b,c) {if (a <= b) System.out.println(c); } 
class Test32{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    int a;
    public int TestMethod(){
        a = 1;
        PRINTIF(a,1,100);
        return 999 ;
    }
}
