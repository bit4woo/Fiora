package test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class Invoice {
    public Integer invoice; // invoice
    public String date; // date
    public Person billTo;// bill-to
    public Person shipTo;// ship-to
    public List<Product> product;
    public Float tax;
    public Float total;
    public String comments;
    
    public static void main(String[] args) throws Exception {
    	Yaml yaml = new Yaml(new Constructor(Invoice.class));
    	//yaml.load(new FileReader("/Users/liwenjun/Documents/github/Fiora/src/test/Invoice.yaml"));
    	yaml.loadAs(new FileReader("/Users/liwenjun/Documents/github/Fiora/src/test/Invoice.yaml"),Invoice.class);
    }

}