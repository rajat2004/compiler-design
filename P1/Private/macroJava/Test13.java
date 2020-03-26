#define CONDWHILE(a) (a <= 10)
#define CONDIF(a) (a <= 5)
#define INCR(a) { a = a + 1; }
class Test13{
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
            if (CONDIF(i))
                j = j + 2;
        }
        return j;
    }
}
