#define print(arg) {System.out.println(arg);} 
#define COND(a,b) (a <= b)
class Test24{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    public int TestMethod(){
        if (COND(1, 2))
            System.out.println(5 + 10);
        else
            System.out.println(5 + 11);
        return 999 ;
    }
}
