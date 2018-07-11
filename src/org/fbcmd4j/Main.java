package org.fbcmd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fbcmd4j.utils.Utils;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;

public class Main {
	static final Logger logger = LogManager.getLogger(Main.class);
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "fbcmd4j.properties";
	public static void main(String[] args) throws IOException {
		logger.info("Iniciando fbcmd4j");
		Facebook fb =  null;
		Properties props = null;

		try {
			props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
		} catch (IOException ex) {
			logger.error(ex);
		}
		
		int option;
		try {
			Scanner scan = new Scanner(System.in);
			while(true) {
				fb = Utils.configFacebook(props);
				System.out.println("Cliente de Facebook en linea de comando\n");
				System.out.println("Opciones: \n");
				System.out.println("(0) Configurar tokens \n");
				System.out.println("(1) Obtener NewsFeed \n");
				System.out.println("(2) Publicar en Wall \n");
				System.out.println("(3) Publicar Estado \n");
				System.out.println("(4) Publicar Link \n");
				System.out.println("(5) Salir \n");
				System.out.println("\nPor favor ingrese una opcion:");
				
				try {
					option = scan.nextInt();
					scan.nextLine();
					switch (option) {
					case 0:
						Utils.configTokens(CONFIG_DIR, CONFIG_FILE, props, scan);
						props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
						break;
					case 1:
						System.out.println("Accediendo a NewsFeed");
						ResponseList<Post> newsFeed = fb.getFeed();
						for (Post p : newsFeed) {
							Utils.printPost(p);
						}
						askToSaveFile("NewsFeed", newsFeed, scan);
						break;
					case 2:
						System.out.println("Accediendo a Wall");
						ResponseList<Post> wall = fb.getPosts();
						for (Post p : wall) {
							Utils.printPost(p);
						}		
						askToSaveFile("Wall", wall, scan);
						break;
					case 3:
						System.out.println("Escribe un estado: ");
						String estado = scan.nextLine();
						Utils.postStatus(estado, fb);
						break;
					case 4:
						System.out.println("Ingresa un link: ");
						String link = scan.nextLine();
						Utils.postLink(link, fb);
						break;
					case 5:
						System.out.println("Fin");
						System.exit(0);
						break;
					default:
						break;
					}
				} catch (InputMismatchException ex) {
					System.out.println("Error, revisar log.");
					logger.error("Opcion no valida. %s. \n", ex.getClass());
				} catch (FacebookException ex) {
					System.out.println("Error, revisar log.");
					logger.error(ex.getErrorMessage());
				} catch (Exception ex) {
					System.out.println("Error, revisar log.");
					logger.error(ex);
				}
				System.out.println();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public static void askToSaveFile(String fileName, ResponseList<Post> posts, Scanner scan) {
		System.out.println("Desea guardar los resultados en un archivo de texto? Si/No");
		String option = scan.nextLine();
		
		if (option.contains("Si") || option.contains("si")) {
			List<Post> ps = new ArrayList<>();
			int n = 0;

			while(n <= 0) {
				try {
					System.out.println("Que numero de publicaciones quieres guardar?");
					n = Integer.parseInt(scan.nextLine());					
			
					if(n <= 0) {
						System.out.println("Ingrese un numero valido");
					} else {
						for(int i = 0; i<n; i++) {
							if(i>posts.size()-1) break;
							ps.add(posts.get(i));
						}
					}
				} catch(NumberFormatException e) {
					logger.error(e);
				}
			}
			Utils.savePostsToFile(fileName, ps);
		}
	}
}
