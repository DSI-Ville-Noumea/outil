package nc.mairie.outils.distiller;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.Logger;

import nc.mairie.metier.DistillerEtat;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.ConvertImageToPDF;
import nc.mairie.technique.Transaction;
/**
 * Insérez la description du type ici.
 * Date de création : (08/04/2004 10:08:25)
 * @author: Administrator
 */
public class ImageDistiller extends Thread {
	private static  String nomFichier;
	private static Logger logger = Logger.getLogger(ImageDistiller.class.getName());
	
	private int delay;
	private static ImageDistiller instance;
	private boolean vivant = true;

	private Vector<String> filesConvertError;
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:17:15)
 * 
 * @param aDelay temps en minutes d'inactivité
 */
public ImageDistiller(int aDelay) {
	super();
	delay=aDelay*1000;
	if (instance == null) {
		instance=this;
	}
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @param org répertoire d'origine
 * @param dest répertoire de destination
 * @throws Exception Exception
 */
public void ajouteDossier(String org, String dest) throws Exception {
	Hashtable<String, DossierDistiller> list = lireDossier();

	DossierDistiller d = new DossierDistiller(org, dest);
	list.put(d.getDossierOrg().getAbsolutePath(), d);

	ecrireDossier(list);
	
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:16:43)
 * @throws UnknownHostException 
 */
public void arreter() throws UnknownHostException {
	
	String ip = InetAddress.getLocalHost().getHostAddress();
		
	Transaction t = getUneTransaction();
	
	DistillerEtat de;
	try {
		de = DistillerEtat.chercherDistillerEtat(t, ip);
		
		de.setQuand(Long.valueOf(System.currentTimeMillis()).toString());
		de.setEtat("OFF");
		
		//si pas trouvé
		if (t.isErreur()) {
			t.traiterErreur();
			de.setServeurip(ip);
			de.creerDistillerEtat(t);
		} else {
			de.modifierDistillerEtat(t);
		}
		
		t.commitTransaction();
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		fermerTransaction(t);
	}
	
	
	
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:16:43)
 * @throws UnknownHostException 
 */
public void demarrer() throws UnknownHostException {
	String ip = InetAddress.getLocalHost().getHostAddress();
	
	Transaction t = getUneTransaction();
	
	DistillerEtat de;
	try {
		de = DistillerEtat.chercherDistillerEtat(t, ip);
		
		de.setQuand(Long.valueOf(System.currentTimeMillis()).toString());
		de.setEtat("ON");
		
		//si pas trouvé
		if (t.isErreur()) {
			t.traiterErreur();
			de.setServeurip(ip);
			de.creerDistillerEtat(t);
		} else {
			de.modifierDistillerEtat(t);
		}
		
		t.commitTransaction();
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		fermerTransaction(t);
	}
}
/**
 * Destroys this thread, without any cleanup. Any monitors it has 
 * locked remain locked. (This method is not implemented.)
 */
public void destroy() {
	vivant = false;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @param list liste des dossiers
 * @throws Exception
 */
private void ecrireDossier(Hashtable<String, DossierDistiller> list) throws Exception{
	
	FileOutputStream fout = new FileOutputStream(getNomFichier());
	ObjectOutputStream oos = new ObjectOutputStream(fout);
	oos.writeObject(list);
	oos.close();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * 
 * @param dossier dossier à supprimmer
 * @throws Exception Exception
 */
public void enleveDossier(String dossier) throws Exception {
	Hashtable<String, DossierDistiller> list = lireDossier();

	list.remove(dossier);

	ecrireDossier(list);
}

public Transaction getUneTransaction () {

	String servername = ImageDistillerServlet.getParametres().get("HOST_SGBD");
	
	if (servername == null) {
		logger.fatal("La variable HOST_SGBD ne se trouve pas dans le contexte.");
		return null;
	}
		
	try {
		Connection con = BasicBroker.getUneConnexion(null, null, servername);
		con.setAutoCommit(true);
		Transaction t= new Transaction(con);
		return t;
	} catch (Exception e) {
		logger.fatal("Impossible d'avoir une connexion sur "+servername);
		return null;
	}
}

public void fermerTransaction (Transaction t) {
	if (t==null) return;
	try {
		t.getConnection().close();
	} catch (Exception e) {
		e.printStackTrace();
	}
}


/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:16:43)
 * @return boolean true si le distiller est arrêté
 */
public boolean estArrete() {
	
	Transaction t = getUneTransaction();
	
	boolean estArrete = true;
	
	try {
		
		DistillerEtat de = DistillerEtat.chercherLastDistillerEtat(t);
		
		// Si pas trouvé d'enreg, c'est que table vide
		if (t.isErreur()) {
			t.traiterErreur();
			estArrete = true;
		} else {
			estArrete = "OFF".equals(de.getEtat());
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		fermerTransaction(t);
	}
	
	return estArrete;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (23/08/2004 11:19:09)
 * @return Vector
 */
private Vector<String> getFilesConvertError() {
	if (filesConvertError == null) {
		filesConvertError = new Vector<String>();
	}
	return filesConvertError;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:09:31)
 * @return nc.mairie.outils.ImageDistiller
 */
public static synchronized ImageDistiller getInstance() {
	if (instance == null) {
		//instance de 60 sec par défaut
		instance = new ImageDistiller(60);
	}
	return instance;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @return Hashtable Hashtable
 * @throws Exception Exception
 */
public Hashtable<String, DossierDistiller> lireDossier() throws Exception {
	FileInputStream fin = null;
	try {
		fin= new FileInputStream(getNomFichier());
	} catch (Exception e) {
		return new Hashtable<String, DossierDistiller>();
	}
	
	ObjectInputStream ois = new ObjectInputStream(fin);
	@SuppressWarnings("unchecked")
	Hashtable<String, DossierDistiller> result = (Hashtable<String, DossierDistiller>) ois.readObject();
	ois.close();

	if (result == null)
		result = new Hashtable<String, DossierDistiller>();
	
	return result;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:16:43)
 */
public void run() {
	while (vivant) {
		synchronized (this) {
			try {
				logger.debug("debug");
				sleep(delay);
			} catch (Exception sleep) {
				sleep.printStackTrace();
			}
			
			Transaction t = null;
			try {
				//Recup du distillerEtat en cours
				t = getUneTransaction();
				String monIP = InetAddress.getLocalHost().getHostAddress();
				DistillerEtat lastDE = DistillerEtat.chercherLastDistillerEtat(t);
				
				//si pas trouvé on créée l'enreg à ON
				if (t.isErreur()) {
					t.traiterErreur();
					lastDE.setServeurip(monIP);
					lastDE.setQuand(Long.valueOf(System.currentTimeMillis()).toString());
					lastDE.setEtat("ON");
					lastDE.creerDistillerEtat(t);
				}
				
				//si dernière action a moins de 3 fois le delay autorisén
				long delaiDerniereExecution = System.currentTimeMillis() - Long.valueOf(lastDE.getQuand());  
				logger.debug("temps : "+delaiDerniereExecution );
				
				if (delaiDerniereExecution  < delay * 3) {
					
					//Si c'est mon IP qui a bossé la dernière fois
					if (monIP.equals(lastDE.getServeurip().trim())) {
						
						lastDE.setQuand(Long.valueOf(System.currentTimeMillis()).toString());
						lastDE.modifierDistillerEtat(t);
						
						//Si l'ancien état était à OFF
						if ("ON".equals(lastDE.getEtat().trim())) {
							
							logger.debug("Scanne dossiers normalement");
							scanneDossiers();
							
						} else {
							logger.debug("Je suis sur OFF");
						}
					
					//c'était pas mon IP, je patiente
					} else {
						//bhen rien
						logger.debug("c'est pas mon ip, alors je patiente");
					}
					
					
					
				//Plus de 3mn alors il faut forcer le lancement
				} else {
					//Recup du distiller si pa mon IP
					DistillerEtat de = lastDE.getServeurip().trim().equals(monIP) ? lastDE : DistillerEtat.chercherDistillerEtat(t, monIP);
				
					de.setQuand(Long.valueOf(System.currentTimeMillis()).toString());
					de.setEtat("ON");
					
					//si pas trouvé
					if (t.isErreur()) {
						t.traiterErreur();
						de.setServeurip(monIP);
						de.creerDistillerEtat(t);
					} else {
						de.modifierDistillerEtat(t);
					}
					
					t.commitTransaction();
					
					//Si last etat = ON alors scanne dossier
					logger.info("Scanne dossiers car ça fait plus de "+(delay/1000)+" secondes  : "+(delaiDerniereExecution/1000)+" secondes.");
					scanneDossiers();
				
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				fermerTransaction(t);
			}
		}
	}
	logger.info("Sortie du Distiller");
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:23:08)
 */
private synchronized void scanneDossiers() throws Exception{

	//Rajout gestion fichiers en erreur de conversion
	Vector<String> newFileConvertError = new Vector<String>();

	//recup de la liste des dossiers
	Hashtable<String, DossierDistiller> list = lireDossier();
	Enumeration<DossierDistiller> enume = list.elements();
	while (enume.hasMoreElements()) {
		DossierDistiller d = (DossierDistiller)enume.nextElement();

		//Si dossier innaccessible
		if (! d.getDossierOrg().exists()) {
			logger.info("Le dossier d'origine "+d.getDossierOrg()+" est inexistant ou innaccessible");
			continue;
		}

		//Si dossier innaccessible
		if (! d.getDossierDest().exists()) {
			logger.info("Le dossier de destination "+d.getDossierDest()+" est inexistant ou innaccessible");
			continue;
		}
		
		File [] lesFile = d.getDossierOrg().listFiles();

		//init dyu compteur de temps
		long deb = System.currentTimeMillis();
		int nbFiles = 0;

		//parcours de chaque file
		for (int i = 0; i < lesFile.length; i++){

			//recup du fichier org
			File fichierOrg = lesFile[i];

			//Si c'est un dossier, on ignore
			if (fichierOrg.isDirectory()) {
				continue;
			}

			//Recup du nom de fichier
			String nom = fichierOrg.getName();
			int posPoint = nom.lastIndexOf(".") == -1 ? nom.length() : nom.lastIndexOf(".");

			//on vire l'extension
			nom= nom.substring(0,posPoint);
			
			//On crée le fichier dest
			File fichierDest = new File(d.getDossierDest().getPath()+"/"+nom+".pdf");

			//on convertit
			try {
				ConvertImageToPDF.convertir(fichierOrg, fichierDest);		
			} catch (Exception e) {
				newFileConvertError.add(fichierOrg.getAbsolutePath());
				//Si pas déjà en erreur alors on envoie le message dans la log
				if (! getFilesConvertError().contains(fichierOrg.getAbsolutePath())) {
					logger.info("Impossible de convertir le fichier "+fichierOrg);
				}
				continue;
			}

			//on supprime la source
			try {
				fichierOrg.delete();
			} catch (Exception e) {
				logger.info("Impossible de supprimer le fichier "+fichierOrg.getName());
				continue;
			}

			//incrémentation du nombre de fichiers traités
			nbFiles++;
		}

		//Si des fichiers ont été convertis, on affiche le total et le temps
		if (nbFiles !=0) {
			int diff = (int)(System.currentTimeMillis()-deb)/1000;
			int h = diff / 360;
			int m = (diff - h * 360) / 60;
			int s = diff - h * 360 - m * 60;
			logger.info("Conversion de "+nbFiles+" fichiers du dossier "+d.getDossierOrg().getAbsolutePath()+" dans le dossier "+d.getDossierDest().getAbsolutePath()+" en "+
				(h>0 ? h+" heures " : "") +
				(h>0 || m > 0 ? m+" minutes ": "")+
				s+" secondes");
		}
	}

	//MAJ des fichiers en erreur de conversion
	setFilesConvertError(newFileConvertError);
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (23/08/2004 11:19:09)
 * @param newFilesConvertError Vector
 */
private void setFilesConvertError(Vector<String> newFilesConvertError) {
	filesConvertError = newFilesConvertError;
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 10:41:14)
 * @throws Exception Exception
 */
public void videDossier() throws Exception {
	Hashtable<String, DossierDistiller> list = new Hashtable<String, DossierDistiller>();

	ecrireDossier(list);
	
}
/**
 * 
 * @return nom du DATA_FILE passé en paramètre
 */
private static String getNomFichier() {
	if (nomFichier == null) {
		nomFichier = (String)ImageDistillerServlet.getParametres().get("DATA_FILE");
	}
	return nomFichier;
}

}
