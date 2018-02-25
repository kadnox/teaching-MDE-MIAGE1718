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
import org.xtext.example.mydsl.videoGen.Filter;
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

	/**
	 * Dans cette méthode il est possible de récupérer une liste avec toutes les
	 * variantes possible des vidéos rpovenant d'un fichier .videogen
	 */
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

	//TP3 - export gif d'une video
	public static void generateGif(String inputpath, String outputpath) throws IOException, InterruptedException {
		Runtime runtime = Runtime.getRuntime();
		
		File f = new File(outputpath);
				
		if(f.exists()){
			String [] cmdRm = {"rm", outputpath};
			Process p = runtime.exec(cmdRm);
			p.waitFor();
		}
		
		String[] cmd = {"/usr/bin/ffmpeg", "-i", inputpath, outputpath };
		
		Process p = runtime.exec(cmd);
		
		p.waitFor();
		
		BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		
		String line;
        while ((line = err.readLine()) != null) {
        	System.out.println(line);
        }
	}
	
	//TP4 - gestion de filtres
	public static void applyFilter(String inputpath, String outputpath) throws IOException, InterruptedException {
		Runtime runtime = Runtime.getRuntime(); 

		File f = new File(outputpath);

		if(f.exists()){
			String [] cmdRm = {"rm", outputpath};
			Process p = runtime.exec(cmdRm);
			p.waitFor();
		}

		String[] cmd = {"/usr/bin/ffmpeg","-i", inputpath, "-vf", "vflip", "-c:a", "copy", outputpath};
		Process p = runtime.exec(cmd);
		p.waitFor();

		BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line;
		while ((line = err.readLine()) != null) {
			System.out.println(line);
		}

	}
	
	public void TP4() {
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));

		for (Media vs : videoGen.getMedias()) {

		}

	}

	public static void TP2() throws IOException, InterruptedException {
		String location = "";
		

		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));

		for (Media vs : videoGen.getMedias()) {

			if (vs instanceof MandatoryVideoSeq) {
				location = location + "file " + ((MandatoryVideoSeq) vs).getDescription().getLocation() + "\n";
			}

			if (vs instanceof OptionalVideoSeq) {
				double i = Math.random();
				if (i > 0.5) {
					location = location + "file " + ((OptionalVideoSeq) vs).getDescription().getLocation() + "\n";
					
					
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
	
	/**
	 * Méthode permettant de créer une variante aléatoire à partir d'un fichier videogen
	 * @param textpath path vers ou se situe le fichier avec la liste des videos variante en txt
	 * @param outputpath le path de sortie de video
	 * @throws IOException si problème
	 * @throws InterruptedException si problème
	 */
	public static void openVideoFromFile(String textpath, String outputpath) throws IOException, InterruptedException {

		Runtime runtimeFF = Runtime.getRuntime();
		String[] tabff = { "/usr/bin/ffmpeg", "-y" ,"-f" ,"concat" ,"-safe","0","-i",textpath,"-c", "copy",  outputpath};
		Process p = runtimeFF.exec(tabff);
		
		p.waitFor();

		String[] tab = { "vlc", outputpath };
		Process p1 = runtimeFF.exec(tab);
		p1.waitFor();

	}
	
	/**
	 * Méthode permettant de cloner des listes imbriquées
	 * @param list liste à cloner contenant des listes
	 * @return la liste avec des listes imbriquées clonnée
	 */
	public static List<List<VideoDescription>> clone(List<List<VideoDescription>> list) {
		List<List<VideoDescription>> listv = new ArrayList<>();
		for (List<VideoDescription> l : list) {
			List<VideoDescription> l1 = (List<VideoDescription>) ((ArrayList) l).clone();
			listv.add(l1);
		}
		return listv;

	}
	
	/**
	 * Méthode permettant de créer un fichier csv avec la taille des variantes possible provenant d'un fichier .videogen
	 * @param listId l'ensembre des ids des videos (trouvé par l'algorithme)
	 * @param list la liste imbriqué des variantes de toutes les vidéos possibles
	 * @throws IOException si problème
	 */
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
	
	/**
	 * 
	 * @param inputPath path de la video ou il faut créer un image
	 * @param outputPath nom de l'image de sortie
	 * @param duration duréee pour trouver le snapshot
	 * @throws IOException si problème
	 * @throws InterruptedException si problème
	 */
	public static void createImage(String inputPath,String outputPath,int duration,String directory) throws IOException, InterruptedException {
		
		Runtime runtimeFF = Runtime.getRuntime();
		String[] tabff = { "/usr/bin/ffmpeg", "-y","-i", inputPath, "-r", "1" ,"-t" ,"00:00:01","-ss", "00:00:"+ duration, "-f","image2" , directory+"/"+outputPath+".png"};
		Process p2 = null;
		System.out.println("l\'image "+outputPath+" est créée");
		
		p2 = runtimeFF.exec(tabff);
		p2.waitFor();
	}
	
	
	/**
	 * Créer l'ensemble des images des vidéos, (une image par video) pour la visualisation sur le web le front
	 */
	public static void creerFichierImages() {
		VideoGeneratorModel videoGen = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));
		String dirname = "image";
		File dir = new File ("./"+dirname);
		dir.mkdir();
		for (Media vs : videoGen.getMedias()) {

			if (vs instanceof MandatoryVideoSeq) {
				try {
					createImage(((MandatoryVideoSeq) vs).getDescription().getLocation() , ((MandatoryVideoSeq) vs).getDescription().getVideoid(), 10,dirname);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}

			if (vs instanceof OptionalVideoSeq) {
				try {
					createImage(((OptionalVideoSeq) vs).getDescription().getLocation(), ((OptionalVideoSeq)vs).getDescription().getVideoid(), 10, dirname);
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			if (vs instanceof AlternativeVideoSeq) {
				List<VideoDescription> list = ((AlternativeVideoSeq) vs).getVideodescs();

				for(VideoDescription l : list) {
					try {
						createImage(l.getLocation(), l.getVideoid(), 10,dirname);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}
	}
	
	public static void main(String[] args) {
		try {
			TP2();
			generateGif("video/jori.mp4", "test.gif");
			applyFilter("video/blabla.mp4", "testFilter.mp4");
			creerFichierImages();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		TP3();
	}

}

