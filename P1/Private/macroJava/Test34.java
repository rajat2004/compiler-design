#define PRINTTHREETIMES(a) { System.out.println(a);System.out.println(a);System.out.println(a); } 
class Test34{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    int a;
    public int TestMethod(){
        a = 1;
        PRINTTHREETIMES(1);
        return 999 ;
    }
}
