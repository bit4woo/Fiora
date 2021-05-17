package test;

public class env {
	public static void main(String[] args) throws Exception{
		String pathvalue = System.getenv().get("PATH");
		//String pathvalue = System.getenv().get("path");
		System.out.println(pathvalue);
		String[] cmdArray = new String[] {"open","\"/Users\""};
		Runtime.getRuntime().exec(cmdArray);
	}
}
