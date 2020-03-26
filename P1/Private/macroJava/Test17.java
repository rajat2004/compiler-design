#define INCRPRINT(a) { a = a + 1; System.out.println(a); }
class Test17{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    int a;
    public int ConstRet()
    {
        return 10;
    }
    public int TestMethod(){
        a = this.ConstRet();
        INCRPRINT(a);
        return 10;
    }
}
