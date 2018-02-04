package FileInputOutput;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectFileConnect {
	ObjectOutputStream output = null;
	ObjectInputStream input = null;


	public Object read(String filepath) {
		try {
			input=new ObjectInputStream(new FileInputStream(filepath));
			return input.readObject();
		} catch (IOException | ClassNotFoundException e) {
		}
		return null;
	}
	public void write(Object obj,String filepath) {
		try {
			output = new ObjectOutputStream(new FileOutputStream(filepath));
			output.writeObject(obj);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
