#define print(arg) {System.out.println(arg);} 
#define COND() ( false || false)
class Test23{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    public int TestMethod(){
        if (COND())
            System.out.println(5 + 10);
        else
            System.out.println(5 + 11);
        return 999 ;
    }
}
