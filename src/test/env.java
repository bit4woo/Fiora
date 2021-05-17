package test;

public class env {
	public static void main(String[] args) {
		String pathvalue = System.getenv().get("PATH");
		//String pathvalue = System.getenv().get("path");
		System.out.println(pathvalue);
	}
}
