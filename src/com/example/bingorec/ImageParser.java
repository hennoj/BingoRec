package com.example.bingorec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.googlecode.tesseract.android.TessBaseAPI;



public class ImageParser 
{
	
	private String directory;
	
	public ImageParser(String dir)
	{
		directory = dir;
		initialize();
		
	}
	
	public int CV_RGB(int r , int g, int b)
	{
		return (b + (g << 8) + (r << 16));
		
		  
	}
	
	public double[] sum(Point[] pts)
	{
		double[] sum = new double[pts.length];
		for(int i=0;i<pts.length;i++)
		{
			sum[i] = pts[i].x+pts[i].y;
		}
		return sum;
	}
	
	public double[] diff(Point[] pts)
	{
		double diff[] = new double[pts.length];
		for(int i=0;i<pts.length;i++)
		{
			diff[i] = pts[i].y-pts[i].x;
		}
		return diff;
	}
	
	public int max(double [] arr)
	{
		int max = 0;
		for(int i=0;i<arr.length;i++)
		{
			if(arr[i] > arr[max])
			{
				max = i;
			}
		}
		return max;
	}
	
	public int min(double[] arr)
	{
		int min = 0;
		for(int i=0;i<arr.length;i++)
		{
			if(arr[i] < arr[min])
			{
				min = i;
			}
		}
		return min;
		
	}
	
	public MatOfPoint2f sort4(MatOfPoint2f m)
	{
		Point[] pts = m.toArray();
		double[] sum = sum(pts);
		double[] diff = diff(pts);
		
		Point[] sorted = new Point[4];
		
		sorted[0] = pts[min(sum)];
		sorted[2] = pts[max(sum)];
		
		sorted[1] = pts[min(diff)];
		sorted[3] = pts[max(diff)];
		
		return new MatOfPoint2f(sorted);
	}
	
	public MatOfPoint2f getNewPoints()
	{
		Point[] pts = new Point[4];
		pts[0] = new Point(0,0);
		pts[1] = new Point(449,0);
		pts[2] = new Point(449,449);
		pts[3] = new Point(0,449);
		return new MatOfPoint2f(pts);
		
	}
	
	
	public Mat getBlob(Point blob,Mat src)
	{
		Mat mask = Mat.zeros(src.rows()+2,src.cols()+2, CvType.CV_8UC1);
        
        
        Imgproc.floodFill(src, mask, blob,new Scalar(0), new Rect(),new Scalar(0), new Scalar(0), (255 << 8)+ Imgproc.FLOODFILL_MASK_ONLY);
        
        
        
        Mat smallMask = new Mat(mask, new Rect(1,1,src.rows(),src.cols()));
        
        
        Mat m = Mat.zeros(src.rows(), src.cols(),  CvType.CV_8UC1);
        src.copyTo(m,smallMask);

		Mat finalpic = Mat.zeros(m.rows(),m.cols(), CvType.CV_8UC1);
		
	    Core.bitwise_not(m, finalpic);
	    return finalpic;
	}
	
	public Mat [] segment(Mat src)
	{
		
		Mat dilate = src; /* Mat.zeros(src.size(), CvType.CV_8UC1);
		Mat ukernel = new Mat(3, 3, CvType.CV_8U);
        ukernel.put(0, 0, (byte) 0);
        ukernel.put(0, 1, (byte) 1);
        ukernel.put(0, 2, (byte) 0);
        ukernel.put(1, 0, (byte) 1);
        ukernel.put(1, 1, (byte) 1);
        ukernel.put(1, 2, (byte) 1);
        ukernel.put(2, 0, (byte) 0);
        ukernel.put(2, 1, (byte) 1);
        ukernel.put(2, 2, (byte) 0);
        
        Imgproc.dilate(src, dilate, ukernel);
		*/
		Mat mask = Mat.zeros(dilate.rows()+2,dilate.cols()+2, CvType.CV_8UC1);
		
		int max1=-1;
		int max2=-1;
        Point number1 = new Point(0,0);
        Point number2 = new Point(0,0);

        // find two biggest connected components (blobs)
        for(int y=0;y<dilate.size().height;y++)
        {
           
            for(int x=0;x<dilate.size().width;x++)
            {
            	
            	double[] el = dilate.get(y, x);
            	
                if(el[0]>=128)
                {

                	int area = Imgproc.floodFill(dilate, mask, new Point(x,y),new Scalar(64), new Rect(),new Scalar(0), new Scalar(0), (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY);
                    
                     if(area>max1)
                     {
                    	 max2 = max1;
                    	 max1 = area;
                    	 number2 = number1;
                         number1 = new Point(x,y);
                     }
                     else if(area > max2)
                     {
                    	 max2 = area;
                    	 number2 = new Point(x,y);
                     }
                }
                
            }

        }
        
       
        Mat [] segments = new Mat[2];
        if(number1.x < number2.x)
        {
        	segments[0] = getBlob(number1,dilate);
        	segments[1] = getBlob(number2,dilate);
        }
        else
        {
        	segments[0] = getBlob(number2,dilate);
        	segments[1] = getBlob(number1,dilate);
        }
        
        
	    
	    
	    
	    return segments;
	}
	
	public void saveText(String data,String file)
	{
		try
		{
			File textFile = new File(directory,file);
			BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
		
			writer.write(data);
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void makeText()
	{
		try
		{
			File textFile = new File(directory, "textnumbers.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
			
			for(int i=1;i<=25;i++)
			{
				
				String str1 = readAsBinaryString(directory+"/bingo_cell_"+i+"_first.png");
				
				String str2 = readAsBinaryString(directory+"/bingo_cell_"+i+"_second.png");
				
					
				writer.write(str1 + "\n\n");
				writer.write(str2 + "\n\n");
					
				
			}
			writer.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public Bitmap readAsBitmap(String dir, int id, boolean first)
	{
		File imgFile = null;
		if(first)
		{
			imgFile = new  File(dir,"bingo_cell_"+id+"_first.png");
		}
		else
		{
			imgFile = new  File(dir,"bingo_cell_"+id+"_second.png");
		}
		

		if(imgFile.exists()){

		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

		    return myBitmap;

		}
		throw new RuntimeException("Image does not exist!");
	}
	
	public String readAsBinaryString(String dir,int id,boolean first)
	{
		String imgFile = null;
		if(first)
		{
			imgFile = dir+"/bingo_cell_"+id+"_first.png";
		}
		else
		{
			imgFile = dir+"/bingo_cell_"+id+"_second.png";
		}
		
		
		return readAsBinaryString(imgFile);
		
	}
	
	public String readAsBinaryString(String imageName)
	{
		String str = "";
		
		Mat number = Highgui.imread(imageName,Highgui.IMREAD_GRAYSCALE);
		Mat thresh = new Mat(number.size(),CvType.CV_8UC1);
		Imgproc.adaptiveThreshold(number, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV, 11, 2);
		
		
		Mat resized = new Mat();
		
		Imgproc.resize(thresh, resized, resized.size(), 0.20, 0.20, Imgproc.INTER_AREA);
		
		
		for(int y=0;y<resized.height();y++)
		{
			for(int x=0;x<resized.width();x++)
			{
				double [] pixel = resized.get(y, x);
				if(pixel[0] > 128)
				{
					str += "1";
				}
				else
				{
					str += "0";
				}
			}
			
			 // str += "\n";
		}
		
		return str;
		
		
	}
	
	public void initialize()
	{
		if (!OpenCVLoader.initDebug()) 
		{
			throw new RuntimeException("Could not load opencv library!");
	    }
	}
	
	
	// http://stackoverflow.com/questions/11336315/converting-nv21-to-rgb-using-opencv-in-android
	public Bitmap preview(byte [] data, int width, int height)
	{
		
		Mat mYuv = new Mat( height + height/2, width, CvType.CV_8UC1 );
	    mYuv.put( 0, 0, data );
		Mat mRgba = new Mat();
	    Imgproc.cvtColor( mYuv, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4 );
	    

		Mat grayscale = new Mat();
		Imgproc.cvtColor(mRgba, grayscale, Imgproc.COLOR_BGR2GRAY);
		
		Mat thresh = new Mat(grayscale.size(),CvType.CV_8UC1);
		Imgproc.adaptiveThreshold(grayscale, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV, 11, 2);
		
		Mat hierarchy = new Mat(thresh.size(),CvType.CV_8UC1);
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		
		Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		double max_area = 0;
		Rect bbr = null;
		for(MatOfPoint i : contours)
		{
			Rect r = Imgproc.boundingRect(i);
			double area = r.area();
			if(area > max_area)
			{
				bbr = r;
				max_area = area;
			}
		}
		
		Core.rectangle(thresh, new Point(bbr.x,bbr.y), new Point(bbr.x+bbr.width,bbr.y+bbr.height), new Scalar(255,0,0,0));
		
		
		Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
		   
		Utils.matToBitmap(thresh, bitmap);
		
		
		
		return bitmap;
		
	}
	
	public void parse(String imageName) throws Exception
	{
		
		
		
		Mat sudoku = Highgui.imread(imageName,Highgui.IMREAD_GRAYSCALE);
		if(sudoku.empty())
		{
			throw new Exception("Could not read image ");
		}
		
		//Mat outerbox = new Mat(sudoku.size(),CvType.CV_8UC1);
		
		
		//Imgproc.GaussianBlur(sudoku, outerbox, new Size(5,5), 0);
		//Highgui.imwrite(directory+"/sudoku_greyblur.png", outerbox);
		
		Mat thresh = new Mat(sudoku.size(),CvType.CV_8UC1);
		Imgproc.adaptiveThreshold(sudoku, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV, 11, 2);
		
		Highgui.imwrite(directory+"/bingo_firstthresh.png", thresh);
		
		Mat hierarchy = new Mat(thresh.size(),CvType.CV_8UC1);
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		
		Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		
		
		MatOfPoint2f biggest = null;
		double max_area = 0;
		for(MatOfPoint i : contours)
		{
			Rect r = Imgproc.boundingRect(i);
			double area = r.area();
				
			MatOfPoint2f approx = new MatOfPoint2f(i.toArray());
			// double peri = Imgproc.arcLength(ti, true);
			// MatOfPoint2f approx = new MatOfPoint2f();
			// Imgproc.approxPolyDP(ti,  approx, 0.02*peri, true);
			
			if(area > max_area)
			{
				biggest = approx;
				max_area = area;
			}
				
			
			
		}
		
		MatOfPoint2f sortedbiggest = sort4(biggest);
		MatOfPoint2f newPic = getNewPoints();
		
		Mat ret = Imgproc.getPerspectiveTransform(sortedbiggest, newPic);
		
		Mat cut = new Mat(sudoku.size(),CvType.CV_8UC1);
		
		Imgproc.warpPerspective(sudoku, cut, ret, new Size(450,450));
		Highgui.imwrite(directory+"/bingo_warp.png", cut);
		
		// extract numbers
		
		Mat blur = new Mat(cut.size(),CvType.CV_8UC1);
		
		Imgproc.GaussianBlur(cut, blur, new Size(5,5), 0);
		
		
		
		
		Mat thresh2 = Mat.zeros(blur.size(), CvType.CV_8UC1);
		
		Imgproc.adaptiveThreshold(blur, thresh2, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV, 11, 2);
		
		Highgui.imwrite(directory+"/bingo_thresh.png", thresh2);
		
		Mat dilate = Mat.zeros(thresh2.size(), CvType.CV_8UC1);
		Mat ukernel = new Mat(3, 3, CvType.CV_8U);
        ukernel.put(0, 0, (byte) 0);
        ukernel.put(0, 1, (byte) 1);
        ukernel.put(0, 2, (byte) 0);
        ukernel.put(1, 0, (byte) 1);
        ukernel.put(1, 1, (byte) 1);
        ukernel.put(1, 2, (byte) 1);
        ukernel.put(2, 0, (byte) 0);
        ukernel.put(2, 1, (byte) 1);
        ukernel.put(2, 2, (byte) 0);
        
        Imgproc.dilate(thresh2, dilate, ukernel);
       
		
        Highgui.imwrite(directory+"/bingo_extract_dilate.png", dilate);
        Mat mask = Mat.zeros(dilate.rows()+2,dilate.cols()+2, CvType.CV_8UC1);
       
        
        int max=-1;
        Point maxPt = new Point(0,0);

        for(int y=0;y<dilate.size().height;y++)
        {
           
            for(int x=0;x<dilate.size().width;x++)
            {
            	
            	double[] el = dilate.get(y, x);
            	
                if(el[0]>=128)
                {

                	int area = Imgproc.floodFill(dilate, mask, new Point(x,y),new Scalar(64), new Rect(),new Scalar(0), new Scalar(0), (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY);
                    
                     if(area>max)
                     {
                         maxPt = new Point(x,y);
                         max = area;
                     }
                }
            }

        }
        
        Highgui.imwrite(directory+"/bingo_fill_mask.png",mask);
        
        mask = Mat.zeros(dilate.rows()+2,dilate.cols()+2, CvType.CV_8UC1);
        
        Imgproc.floodFill(dilate, mask, maxPt,new Scalar(0), new Rect(),new Scalar(0), new Scalar(0), (255 << 8) );
        
       
        
        
		Highgui.imwrite(directory+"/bingo_final_mask.png",mask);
        
		Highgui.imwrite(directory+"/bingo_final_floodfill.png",dilate);
		
		Mat digits = new Mat(thresh2.rows(),thresh2.cols(),CvType.CV_8UC1);
		thresh2.copyTo(digits, dilate);
		
		Highgui.imwrite(directory+"/bingo_final_digits.png",digits);
        
		
        
        
		
		int cell = 1;
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<5;j++)
			{
				Mat part = new Mat(digits,new Rect(j*90,i*90,90,90));
				
				Mat [] finalpics = segment(part);	
				
				
				if(!Highgui.imwrite(directory+"/bingo_cell_"+cell+"_first.png", finalpics[0]))
		        {
		        	System.out.println("Writing file error");
		        }
				
				if(!Highgui.imwrite(directory+"/bingo_cell_"+cell+"_second.png", finalpics[1]))
		        {
		        	System.out.println("Writing file error");
		        }
				
				
				cell++;
			}
			
		}
		
		
	}
	
	public static String convertFromUTF8(String s) throws Exception
	{
        String out = null;
        out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        return out;
    }
	
	public String multinetRecognize()
	{
		String [] numbers = new String[25];
		
		MultiNetwork mnet = new MultiNetwork(directory);
		mnet.loadNetworks();
		
		for(int i=1;i<=25;i++)
		{
			String num1 = readAsBinaryString(directory+"/bingo_cell_"+i+"_first.png");
			String num2 = readAsBinaryString(directory+"/bingo_cell_"+i+"_second.png");
			
			Instance inst1 = Instance.createSingleInstance(-1,num1);
			Instance inst2 = Instance.createSingleInstance(-1,num2);
			
			
			
			int inum1 = mnet.feedMultiNetwork(inst1);
			int inum2 = mnet.feedMultiNetwork(inst2);
			
			String num = "";
			if(inum1 >= 0 && inum1 <= 9)
			{
				num += inum1;
			}
			
			if(inum2 >= 0 && inum2 <= 9)
			{
				num += inum2;
			}
			
			numbers[i-1] = num;
			
			
		}
		
		return implode(",",numbers);
	}
	
	public String ANNrecognize()
	{
		String [] numbers = new String[25];
		
		Network net = Network.loadNetwork(directory);
		
		for(int i=1;i<=25;i++)
		{
			String num1 = readAsBinaryString(directory+"/bingo_cell_"+i+"_first.png");
			String num2 = readAsBinaryString(directory+"/bingo_cell_"+i+"_second.png");
			
			Instance inst1 = Instance.createSingleInstance(-1,num1);
			Instance inst2 = Instance.createSingleInstance(-1,num2);
			
			double [] res1 = net.feedInstance(inst1);
			int inum1 = Network.readANNOutput(res1);
			
			double [] res2 = net.feedInstance(inst2);
			int inum2 = Network.readANNOutput(res2);
			
			String num = "";
			if(inum1 >= 0 && inum1 <= 9)
			{
				num += inum1;
			}
			
			if(inum2 >= 0 && inum2 <= 9)
			{
				num += inum2;
			}
			
			numbers[i-1] = num;
			
			
		}
		
		return implode(",",numbers);
	}
	
	
	
	public String TesseractRecognize() throws Exception
	{
		TessBaseAPI b = new TessBaseAPI();
		

		if(!b.init(directory+File.separator,"eng"))
		{
			System.err.println(directory+File.separator);
			throw new Exception("Could not initialize Tesseract library");
		}
		
		b.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR);
		b.setVariable("tessedit_char_whitelist", "123456789");
		
		
		
		String [] numbers = new String[25];
        for(int i=1;i<=25;i++)   	
        {
        	String num = "";
        	File imageFile = new File(directory,"bingo_cell_"+i+"_first.png");
	        try
	        {
	        	b.setImage(imageFile);
	        }
	        catch(Exception e)
	        {
	        	Exception ex = new Exception(imageFile.getName());
	        	throw ex;
	        }
	        String result = convertFromUTF8(b.getUTF8Text());
	        
	        num = result;
	        
	        imageFile = new File(directory,"bingo_cell_"+i+"_second.png");
	        try
	        {
	        	b.setImage(imageFile);
	        }
	        catch(Exception e)
	        {
	        	Exception ex = new Exception(imageFile.getName());
	        	throw ex;
	        }
	        result = convertFromUTF8(b.getUTF8Text());
	        
	        num += result;
	        numbers[i-1] = num;
	        /*
	        result = result.replace("\n", "").replace("\r", "");
	        if(result.equals(""))
            {
            	System.out.print("0");
            }
            else
            {
            	System.out.print(result);
            }
	        System.out.print(" ");
	        */
        }
        
        b.end();
        
        
        return implode(",",numbers);
      
        
	}
	
	// http://stackoverflow.com/questions/11248119/java-equivalent-of-phps-implode-array-filter-array
	public static String implode(String glue, String[] strArray)
	{
	    String ret = "";
	    for(int i=0;i<strArray.length;i++)
	    {
	        ret += (i == strArray.length - 1) ? strArray[i] : strArray[i] + glue;
	    }
	    return ret;
	}
	
	

}
