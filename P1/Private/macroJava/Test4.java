#define print(arg) {System.out.println(arg);} 
#define INPUT() (10+0)
#define MACROINP() (10+0)
class Test4{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod(INPUT()));
    }
}
class TestClass{
    public int TestMethod(int num){
	print(MACROINP());
	return 999 ;
    }
}
