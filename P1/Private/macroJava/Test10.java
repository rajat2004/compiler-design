#define print(arg) {System.out.println(arg);} 
#define COND() ((10+10) <= 25)
class Test10{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    public int TestMethod(){
        if (COND())
            print(5 + 10);
        return 999 ;
    }
}
