import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.ToString;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.xtext.example.mydsl.videoGen.AlternativeVideoSeq;
import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.OptionalVideoSeq;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;
import org.xtext.example.mydsl.videoGen.VideoSeq;

public class VideoGenTestJava1 {

	@Test
	public void testInJava1() {

		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));
		assertNotNull(videoGen);

		System.out.println(videoGen.getInformation().getAuthorName());

	}

	public static void TP3() {

		List<List<VideoDescription>> listvs = new ArrayList<>();
		List<String> listId = new LinkedList<>();
		listId.add("id");
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));

		for (Media vs : videoGen.getMedias()) {

			if (vs instanceof MandatoryVideoSeq) {
				listId.add(((MandatoryVideoSeq) vs).getDescription().getVideoid());
				((MandatoryVideoSeq) vs).getDescription().getLocation();
				if (listvs.size() == 0) {
					List<VideoDescription> l = new ArrayList<>();
					l.add(((MandatoryVideoSeq) vs).getDescription());
					listvs.add(l);
				} else {
					for (List<VideoDescription> lv : listvs) {
						lv.add(((MandatoryVideoSeq) vs).getDescription());
					}
				}
			}

			if (vs instanceof OptionalVideoSeq) {
				listId.add(((OptionalVideoSeq) vs).getDescription().getVideoid());
				if (listvs.size() == 0) {
					List<VideoDescription> l = new ArrayList<>();
					l.add((VideoDescription) vs);
					listvs.add(l);
				} else {
					List<List<VideoDescription>> listTemp = clone(listvs);
					for (List<VideoDescription> lv : listTemp) {
						lv.add(((OptionalVideoSeq) vs).getDescription());
						listvs.add(lv);
					}

				}

			}
			if (vs instanceof AlternativeVideoSeq) {
				List<VideoDescription> list = ((AlternativeVideoSeq) vs).getVideodescs();

				if (listvs.size() == 0) {
					for (VideoDescription v : list) {
						listId.add(v.getVideoid());
						List<VideoDescription> l = new ArrayList<>();
						l.add(v);
						listvs.add(l);
					}
				} else {
					List<List<VideoDescription>> listTemp = clone(listvs);
					listvs.clear();
					for (VideoDescription v : list) {
						listId.add(v.getVideoid());
						List<List<VideoDescription>> listTemp2 = clone(listTemp);
						for (List<VideoDescription> lt2 : listTemp2) {

							lt2.add(v);

							listvs.add(lt2);
						}

					}
				}

			}
			System.out.println(listvs);
		}
		listId.add("size");
		try {
			createCSVFromList(listId, listvs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void TP4() {
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));

		for (Media vs : videoGen.getMedias()) {

		}

	}

	public static void TP2() throws IOException, InterruptedException {
		String location = "";
		String loc = "";

		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));

		for (Media vs : videoGen.getMedias()) {

			if (vs instanceof MandatoryVideoSeq) {
				location = location + "file " + ((MandatoryVideoSeq) vs).getDescription().getLocation() + "\n";
			}

			if (vs instanceof OptionalVideoSeq) {
				double i = Math.random();
				if (i > 0.5) {
					location = location + "file " + ((OptionalVideoSeq) vs).getDescription().getLocation() + "\n";
					loc += ((OptionalVideoSeq) vs).getDescription().getLocation() + " ";
				}
			}
			if (vs instanceof AlternativeVideoSeq) {
				List<VideoDescription> list = ((AlternativeVideoSeq) vs).getVideodescs();

				int choix = (int) (Math.random() * (list.size() - 1));
				VideoDescription video = list.get(choix);
				location = location + "file \'" + video.getLocation() + "\'\n";

			}

		}
		FileWriter f = null;
		try {
			f = new FileWriter("videos.txt");
			f.write(location);
					} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (f != null) {
				f.close();
			}
		}
		

		openVideoFromFile("videos.txt", "video1.mp4");

	}

	public static void openVideoFromFile(String textpath, String outputpath) throws IOException, InterruptedException {

		Runtime runtimeFF = Runtime.getRuntime();
		String[] tabff = { "/usr/bin/ffmpeg", "-y" ,"-f" ,"concat" ,"-safe","0","-i",textpath,"-c", "copy",  outputpath};
		Process p = runtimeFF.exec(tabff);
		
		p.waitFor();

		String[] tab = { "vlc", outputpath };
		Process p1 = runtimeFF.exec(tab);
		p1.waitFor();

	}

	public static List<List<VideoDescription>> clone(List<List<VideoDescription>> list) {
		List<List<VideoDescription>> listv = new ArrayList<>();
		for (List<VideoDescription> l : list) {
			List<VideoDescription> l1 = (List<VideoDescription>) ((ArrayList) l).clone();
			listv.add(l1);
		}
		return listv;

	}

	public static void createCSVFromList(List<String> listId, List<List<VideoDescription>> list) throws IOException {
		String premiere = listId.toString();
		FileWriter fw = null;
		int cpt = 1;
		int nbId = listId.size();

		try {
			fw = new FileWriter("tailles.csv");
			fw.write(premiere.substring(1, premiere.length() - 1) + "\n");
			for (List<VideoDescription> l : list) {
				int size = 0;
				String bool = "";
				System.out.println("=======================");
				List<String> listIdR = new LinkedList<>();
				for (VideoDescription v : l) {
					File f = new File(v.getLocation());
					size += f.length();
					System.out.println(v.getVideoid());
					listIdR.add(v.getVideoid());

				}
				for (int i = 1; i < nbId - 1; i++) {
					boolean b = listIdR.contains(listId.get(i));
					bool += Boolean.toString(b).toUpperCase() + ",";
				}
				System.out.println(bool);
				System.out.println(cpt + " size " + size);
				fw.write(cpt + "," + bool + size + "\n");
				cpt++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				fw.close();
			}

		}

	}
	
	
	public static void createImage(String inputPath,String outputPath,int duration) {
		
		Runtime runtimeFF = Runtime.getRuntime();
		String[] tabff = { "/usr/bin/ffmpeg", "-y", "-i", inputPath, "-r", "1" ,"-t" ,"00:00:01","-ss", "00:00:"+ duration, "-f" , outputPath};
		try {
			Process p = runtimeFF.exec(tabff);
			p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		try {
			createImage("video/jori.mp4","testJ.png",10);
			TP2();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//TP3();
	}

}

