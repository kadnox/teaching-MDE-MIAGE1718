import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public final class FfmpegUtils {
	private static final String FFMPEG = "/usr/bin/ffmpeg";
	
	private FfmpegUtils() {}
	
	public static void executeCommand(String[] cmd) throws IOException, InterruptedException {
		Runtime runtime = Runtime.getRuntime();
		Process p = runtime.exec(cmd);
		p.waitFor();
		showLog(p);
	}
	
	public static void deleteIfExist(String path) throws IOException, InterruptedException {
		File f = new File(path);
				
		if(f.exists()){deleteFile(path);}
	}
	
	public static void deleteFile(String path) throws IOException, InterruptedException {
		String [] cmd = {"rm", path};
		
		executeCommand(cmd);
	}
	
	public static void generateGif(String input, String output) throws IOException, InterruptedException {
		String[] cmd = {FFMPEG, "-i", input, output };
		
		executeCommand(cmd);
	}
	
	private static void showLog(Process p) throws IOException {
		BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line;
        while ((line = err.readLine()) != null) {
        	System.out.println(line);
        }
	}
	
	public static void applyFilter(String input, String output) throws IOException, InterruptedException {
		
		String[] cmd = {FFMPEG,"-i", input, "-vf", "vflip", "-c:a", "copy", output};
		
		executeCommand(cmd);
	}
	
	public static void openVideoVLC(String input, String output) throws IOException, InterruptedException {
		String[] cmd = { FFMPEG, "-y" ,"-f" ,"concat" ,"-safe","0","-i",input,"-c", "copy",  output};
		executeCommand(cmd);
		String[] tab = { "vlc", output};
		executeCommand(tab);

	}
	
	public static void createImage(String input, String output, int duration, String directory) throws IOException, InterruptedException {
		String[] cmd = {FFMPEG, "-y","-i", input, "-r", "1" ,"-t" ,"00:00:01","-ss", "00:00:"+ duration, "-f","image2" , directory+"/"+output+".png"};
		/*
		Runtime runtimeFF = Runtime.getRuntime();
		Process p2 = null;
		System.out.println("l\'image "+output+" est créée");
		
		p2 = runtimeFF.exec(cmd);
		p2.waitFor();
		*/
		executeCommand(cmd);
	}
}
