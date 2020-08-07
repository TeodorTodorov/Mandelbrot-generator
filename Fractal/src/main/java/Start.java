import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.special.Gamma;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;





public class Start {

public static void mandelMultithreaded(int width, int height,double minReal,double maxReal,double minImaginary,double maxImaginary,int threadsCount, int chunkMultiplier, String outputFileName, Boolean print) throws IOException, InterruptedException {
    BufferedImage picture = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
	int chunkSize = (int) Math.ceil( height/ (threadsCount* chunkMultiplier));
	MandelWorker[] workers = new MandelWorker[threadsCount];
	for(int i = 0; i < threadsCount; i++) {
		workers[i] =  new MandelWorker(width, height, minReal, maxReal, minImaginary, maxImaginary, picture,print);
	}
	int nextChunkHeight;
	int prevChunkIndex = 0;
	 
	 int j =0;
	while(prevChunkIndex< height) {
		nextChunkHeight = prevChunkIndex + chunkSize;
		if(nextChunkHeight > height) {
			nextChunkHeight = height;
		}
		
		workers[j].insert(0,width, prevChunkIndex, nextChunkHeight);
		prevChunkIndex += chunkSize;
		j++;
		if(j == threadsCount) {
			j = 0;
		}
	}
	Thread[] threads = new Thread[threadsCount];
	for(int i = 0; i< threadsCount; i++) {
		Thread ct = new Thread(workers[i]);
		ct.setName("Slave" + i);
		threads[i] = ct;
	}
	long startTime = System.currentTimeMillis();
	for(int i = 0; i < threadsCount; i++) {
		threads[i].start();
	}
	for(int i = 0; i< threadsCount; i++) {
		threads[i].join();
	}
	long stopTime = System.currentTimeMillis();
	if(print) {
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("results.txt"),true));
		writer.println( threadsCount + " " + chunkMultiplier + " " +( stopTime - startTime));
		writer.close();

    
	}
	 File outputfile = new File(outputFileName);
     ImageIO.write(picture, "png", outputfile);
}

	public static void main(String[] args) throws ParseException, IOException, InterruptedException {
		
		int width = 640;
		int height = 480;
		double minReal = -2.0;
		double maxReal = 2.0;
		double minImaginary = -1.0;
		double maxImaginary = 1.0;
		int threadsCount = 1;
		String outputFileName = "zad19.png";
		int chunkMulttiplier = 1;
		Boolean print = true;
		Options options = new Options();
		options.addOption("s", true, "size x y");
		options.addOption("t", true, "number of threads");
		options.addOption("r", true, "Area of complax plane minReal maxReal minImaginary maxImaginary");
		options.addOption("q", false, "quiet mode");
		options.addOption("c", true, "chunk multiplier");
		
     HelpFormatter formatter = new HelpFormatter();
     formatter.printHelp("CLITester", options);
     CommandLineParser gnuParser = new GnuParser();
     CommandLine cmd = gnuParser.parse(options, args);


     if( cmd.hasOption("s") ) {
    	 String value = cmd.getOptionValue("s");   
        width = Integer.parseInt( value.substring(0, value.indexOf("x") ));
        height = Integer.parseInt(value.substring(value.indexOf("x") +1));
        
       
     }
     if( cmd.hasOption("r") ) {
    	 String value = cmd.getOptionValue("r");
        int delimiter1 = value.indexOf(":");
        int delimiter2 = delimiter1 +1+ value.substring(delimiter1 +1).indexOf(":");
        int delimiter3 = delimiter2+1+ value.substring(delimiter2 +1).indexOf(":");
        minReal = Double.parseDouble( value.substring(0, value.indexOf(":") ));
        
        maxReal = Double.parseDouble(value.substring(delimiter1 +1, delimiter2));
        minImaginary =  Double.parseDouble(value.substring(delimiter2 +1, delimiter3));
        maxImaginary =  Double.parseDouble(value.substring(delimiter3 +1));
     }
     if( cmd.hasOption("t") ) {
    	 String value = cmd.getOptionValue("t");
    	 threadsCount = Integer.parseInt(value);
    	 System.out.println("Threads:" + threadsCount);
	}
     if( cmd.hasOption("q") ) {
    	print = false;
     }
     if( cmd.hasOption("c") ) {
    	 String value = cmd.getOptionValue("c");
    	 chunkMulttiplier = Integer.parseInt(value);
    	 System.out.println("Chunk multiplier" + " "+ chunkMulttiplier);
	}
    
     
     //mandel(width, height, minReal,maxReal, minImaginary, maxImaginary, threadsCount, outputFileName);
     
     for(int i = 1; i < 32 ; i++) {
    	 long startTime = System.currentTimeMillis();
    	 mandelMultithreaded(width,height,minReal, maxReal, minImaginary, maxImaginary,i, chunkMulttiplier, outputFileName,print);
         long stopTime = System.currentTimeMillis();
     
         System.out.println( "Total work time (from parsing command params to writing to file):" +( stopTime - startTime));
     }
    
 	

}
}
