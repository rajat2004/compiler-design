#define print(arg) {System.out.println(arg);} 
#define INPUT() (10+0)
class Test3{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod(INPUT()));
    }
}
class TestClass{
    public int TestMethod(int num){
	print(num);
	return 999 ;
    }
}
