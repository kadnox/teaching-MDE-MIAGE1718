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
import java.util.logging.Logger;

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
			//Dans tous les cas la vidéo est ajoutée
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
			
			//On garde une copie et on ajoute a cette copie la video et on rassemble la copie et l'origine
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
			// On garde une copie et on ajoute a cette copie une video altenrantive après
			// l'autre sur des copie différentes et on rassemble les copies
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
		listId.add("duree");
		try {
			createCSVFromList(listId, listvs);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//TP3 - export gif d'une video
	public static void generateGif(String inputpath, String outputpath) throws IOException, InterruptedException {
		
		FfmpegUtils.deleteIfExist(outputpath);
		FfmpegUtils.generateGif(inputpath, outputpath);
		
	}
	
	//TP4 - gestion de filtres
	public static void applyFilter(String inputpath, String outputpath) throws IOException, InterruptedException {
		
		FfmpegUtils.deleteIfExist(outputpath);
		FfmpegUtils.applyFilter(inputpath, outputpath);
	}
	
	/**
	 * Méthode permettant de créer une variante de vidéo al&éatoirement et de la lancer avec VLC
	 * @throws IOException
	 * @throws InterruptedException
	 */
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

		
		FfmpegUtils.createVideoFromVideos(textpath, outputpath);
		FfmpegUtils.openVideoVLC(outputpath);
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
				//- 2 car on ajoute size et duree (oui c'est sale ...)
				for (int i = 1; i < nbId - 2; i++) {
					boolean b = listIdR.contains(listId.get(i));
					bool += Boolean.toString(b).toUpperCase() + ",";
				}
				double duree = getDurationVariante(l);
				System.out.println(bool);
				System.out.println(cpt + " size " + size+ " duree: "+duree);
				fw.write(cpt + "," + bool + size + ","+duree+"\n");
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
	 * Créer un image à partir d'une vidéo
	 * @param inputPath path de la video ou il faut créer un image
	 * @param outputPath nom de l'image de sortie
	 * @param duration duréee pour trouver le snapshot
	 * @throws IOException si problème
	 * @throws InterruptedException si problème
	 */
	public static void createImage(String inputPath,String outputPath,int duration,String directory) throws IOException, InterruptedException {

		FfmpegUtils.createImage(inputPath, outputPath, duration, directory);
				
	}
	/**
	 * Donne le temps en seconde d'une video
	 * @param inputPath video à calculer
	 * @return la durée en seconde
	 * @throws IOException si problème
	 */
	public static double getDuration(String inputPath) throws IOException {
		
		Process p2 = FfmpegUtils.getInfo(inputPath);
		String s = null;
		String[] parts= null;
		String[] parts2= null;

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p2.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(p2.getErrorStream()));
		
		while ((s = stdInput.readLine()) != null) {
			if (s.contains("Duraration:")) {
				parts = s.split(": ");
			}
		}
	
		while ((s = stdError.readLine()) != null) {
			if (s.contains("Duration:")) {
				parts = s.split(": ");
			}
		}
		parts2 = parts[1].substring(0, 11).split(":");
		double heure = Double.parseDouble(parts2[0]);
		double minute = Double.parseDouble(parts2[1]);
		double seconde = Double.parseDouble(parts2[2]);
		
		return ((heure*60)+minute)*60 + seconde;
		
	}
	
	/**
	 * Methode permettant d'avoir la durée d'une variante
	 * 
	 * N.B cette méthode sera tuilisé dans TP3 pour pouvoir l'appliqué à toutes les variantes
	 * 
	 * @param variante une variante possible de videoget
	 * @return
	 */
	public static double getDurationVariante(List<VideoDescription> variante) {
		double res = 0;
		for(VideoDescription vs : variante) {
			try {
				res += getDuration(vs.getLocation());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		return res;
		
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
					createImage(((MandatoryVideoSeq) vs).getDescription().getLocation() , ((MandatoryVideoSeq) vs).getDescription().getVideoid()+"man", 10,dirname);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				} 
			}

			if (vs instanceof OptionalVideoSeq) {
				try {
					createImage(((OptionalVideoSeq) vs).getDescription().getLocation(), ((OptionalVideoSeq)vs).getDescription().getVideoid()+"opt", 10, dirname);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				
				
			}
			if (vs instanceof AlternativeVideoSeq) {
				List<VideoDescription> list = ((AlternativeVideoSeq) vs).getVideodescs();

				for(VideoDescription l : list) {
					try {
						createImage(l.getLocation(), l.getVideoid()+"alt", 10,dirname);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		try {
			TP2();
			TP3();
			generateGif("video/jori.mp4", "test.gif");
			applyFilter("video/jori.mp4", "testFilter.mp4");
			//les images se trouvents dans le repertoire image
			creerFichierImages();
			System.out.println(getDuration("video1.mp4"));

		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
		
	}

}

