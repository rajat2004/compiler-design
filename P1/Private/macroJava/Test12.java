#define CONDWHILE(a) (a <= 10)
#define INCR(a) { a = a + 1; }
class Test12{
    public static void main(String[] a){
	System.out.println(new TestClass().TestMethod());
    }
}
class TestClass{
    int i;
    int j;
    public int TestMethod(){
        i = 0; 
        j = 0;
        while (CONDWHILE(i))
        {
            INCR(i);
            j = j + 2;
        }
        return j;
    }
}
