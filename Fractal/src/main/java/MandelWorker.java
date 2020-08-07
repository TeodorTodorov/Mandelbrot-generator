import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.math3.complex.Complex;

public class MandelWorker implements Runnable {

	 public static final int MAX = 255; 
	 int width;
	 int height;
	 double minReal;
	 double maxReal;
	 double minImaginary;
	 double maxImaginary; 
	 Queue<Chunk> chunksQueue;
	 Boolean print;
	 double rangeReal;
	 double rangeImaginary;
	 
	 BufferedImage picture;
	MandelWorker( int width, int height, double minReal, double maxReal, double minImaginary, double maxImaginary, BufferedImage picture, Boolean print){
		this.width= width;
		this.height= height;
		this.minReal = minReal;
		this.maxReal = maxReal;
		this.minImaginary = minImaginary;
		this.maxImaginary = maxImaginary;
		this.picture = picture;
		this.chunksQueue = new ArrayDeque<Chunk>(200);
		this.print = print;
		this.rangeReal = maxReal - minReal;
		this.rangeImaginary = maxImaginary - minImaginary;
	
	}

	public double mapToReal(double x, double width, double minReal, double maxReal) {
		return minReal+ (x/width) * rangeReal;
	}
	public double mapToImaginary(double x, double height, double minImaginary, double maxImaginary) {

		return minImaginary +( x/height) * rangeImaginary;
	}
	public void insert( int startWidth, int endWidth, int startHeight, int endHeight) {
		Chunk c = new Chunk(startWidth, endWidth, startHeight, endHeight);
		chunksQueue.add(c);
	}
	public static int mand(Complex c, int max) {
		// z0 is 
        Complex z = c;
        int t = 0;
        for (; t < max; t++) {
            if (z.abs() > 2.0) {return t;}
           // mandelbrot 
          //  z= z.multiply(z);
           // z= z.add(c);
            z = z.multiply(z).add(c).exp();
            
            // z is e^ z^2 + c now
          
             
            
        }
        return t;
    }
	public void run() {
		long startTime = System.currentTimeMillis();
		while(!chunksQueue.isEmpty()) {
			Chunk c = chunksQueue.poll();
		for(int x = c.startWidth; x < c.endWidth; x++) {
			for(int y = c.startHeight; y < c.endHeight; y++) {
				double x0 =  mapToReal(x, width, minReal, maxReal);
                double y0 = mapToImaginary(y, height, minImaginary, maxImaginary);
                Complex z0 = new Complex(x0, y0);
                int gray = MAX - mand(z0, MAX);
                Color color ;
                color = new Color(gray, gray, gray);
                picture.setRGB(x, y, color.getRGB());
			}
		}
		}
		long stopTime = System.currentTimeMillis();
		if(print) {
		    System.out.println( "Thread " + Thread.currentThread().getName()+ " run for :" +( stopTime - startTime));
			}
	}
}
