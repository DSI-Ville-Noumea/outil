package nc.mairie.outils.distiller;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.technique.MairieLDAP;
/**
 * Insérez la description du type ici.
 * Date de création : (08/04/2004 12:32:08)
 * @author: Administrator
 */
public class ImageDistillerServlet extends javax.servlet.http.HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7672747281577006619L;
	private static java.util.Hashtable parametres;
	private static java.util.ArrayList listeUserHabilites;
	//private static final long serialVersionUID = 123456789;
/**
 * Méthode qui contrôle l'habilitation d'un utilisateur qui se connecte
 * @author Luc Bourdil
 * @param HttpServletRequest
 * @return boolean
 */
public static boolean controlerHabilitation(javax.servlet.http.HttpServletRequest request) {
	//Si un user appli en session alors OK
	if (getUserAppli(request) != null)
		return true;

	//Sinon fenêtre de connexion
	String auth = request.getHeader("Authorization");
	if (auth == null)
		return false;

	String str = null;
	String passwd = null;
	String user = null;

	// Vérification du schéma d'authentification
	String startString = "basic ";
	if (auth.toLowerCase().startsWith(startString)) {
		// Extraction et décodage du user
		String creditB64 = auth.substring(startString.length());
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		
		// Extraction du nom d'utilisateur et du mot de passe
		try {
			byte[] credit = decoder.decodeBuffer(creditB64);
			str = new String(credit);

			//Découpage du nom user:passwd
			int sep = str.indexOf(':');
			user = str.substring(0,sep);
			passwd = str.substring(sep+1);
		} catch (Exception e) {
			return false;
		}
	}

	//init de la liste de user habilités au cas où mis à jour
	initialiseListeUserHabilites();
	
	//Contrôle de la liste des authorisés
	if (! getListeUserHabilites().contains(user.toUpperCase())) {
		return false;
	}
	
	//Contrôle de l'habilitation LDAP
	if (!MairieLDAP.controlerHabilitation(getParametres(), user,passwd))
		return false;
	//Creation du UserAppli
	UserAppli aUserAppli = new UserAppli(user,passwd, (String)getParametres().get("HOST_SGBD"));
	//Ajout du user en var globale
	VariableGlobale.ajouter(request,VariableGlobale.GLOBAL_USER_APPLI, aUserAppli);

	return true;
}
/**
 * Destroy pour tuer le distiller
 */
public void destroy() {
	System.out.println("Destroy de ImageDistiller Servlet");
	ImageDistiller.getInstance().destroy();
}
/**
 * Process incoming HTTP GET requests 
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	performTask(request, response);

}
/**
 * Process incoming HTTP POST requests 
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	performTask(request, response);

}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (08/04/2004 12:39:04)
 */
public java.util.Hashtable getListeDossiers() throws Exception {
	return ImageDistiller.getInstance().lireDossier();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (26/05/2004 11:42:46)
 * @return java.util.ArrayList
 */
private static java.util.ArrayList getListeUserHabilites() {
	if (listeUserHabilites ==  null) {
		listeUserHabilites = new java.util.ArrayList();
	}
	return listeUserHabilites;
}
/**
 * Returns the servlet info string.
 * @author Luc Bourdil
 */
public static java.util.Hashtable getParametres() {
	if (parametres == null) {
		parametres = new java.util.Hashtable();
	}
	return parametres;
}
/**
 * Returns the servlet info string.
 */
public String getServletInfo() {

	return super.getServletInfo();

}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (28/10/2002 11:17:51)
 * @author Luc Bourdil
 * @return nc.mairie.technique.UserAppli
 * @param request javax.servlet.http.HttpServletRequest
 */
private static UserAppli getUserAppli(javax.servlet.http.HttpServletRequest request) {
	return (UserAppli)VariableGlobale.recuperer(request,VariableGlobale.GLOBAL_USER_APPLI);
}
/**
 * Cette méthode a été importée à partir d'un fichier .class.
 * Aucun code source disponible.
 */
public void init() throws javax.servlet.ServletException {
	// Démarrage du distiller
	if (!ImageDistiller.getInstance().isAlive()) {
		ImageDistiller.getInstance().start();
	}
	
	// insert code to initialize the servlet here
	initialiseParametreInitiaux();

	//init des user habilités
	initialiseListeUserHabilites();
}
/**
 * Insérez la description de la méthode ici.
 *  Date de création : (14/04/2004 09:53:56)
 */
private static void initialiseListeUserHabilites() {

	listeUserHabilites = new java.util.ArrayList();
	listeUserHabilites.add("BOULU72");
	listeUserHabilites.add("ADMINWAS");
	listeUserHabilites.add("DOSFR75");
}




/**
 * Insérez la description de la méthode à cet endroit.
 *  Date de création : (22/02/2002 10:51:46)
 * @return fr.averse.servlets.Contexte
 */
private void initialiseParametreInitiaux() {

	boolean doitPrendreInit = getServletContext().getInitParameterNames().hasMoreElements();

	System.out.println("Chargement des paramètres initiaux dans la servlet : "+getClass().getName());
	if (getParametres().size() == 0) {
		
			//chargement des paramêtres du contexte
		java.util.Enumeration enumContext = doitPrendreInit ? getServletContext().getInitParameterNames() : getServletContext().getAttributeNames();
		while (enumContext.hasMoreElements()) {
			try {
				String cleParametre = (String)enumContext.nextElement();
				if (cleParametre != null && ! cleParametre.startsWith("com.ibm.websphere") ) {
					String valParametre = doitPrendreInit ? (String)getServletContext().getInitParameter(cleParametre) : (String)getServletContext().getAttribute(cleParametre);
					getParametres().put(cleParametre,valParametre);
					System.out.println("Chargement de la clé : "+cleParametre+" avec "+valParametre);
				}
			} catch (Exception e) {
				continue;
			}
		}
	
		//chargement des param de la servlet
		java.util.Enumeration enumServlet = getInitParameterNames();
		while (enumServlet.hasMoreElements()) {
			String cleParametre = (String)enumServlet.nextElement();
			String valParametre = (String)getInitParameter(cleParametre);
			getParametres().put(cleParametre,valParametre);
			System.out.println("Chargement de la clé : "+cleParametre+" avec "+valParametre);
		}
	}
	System.out.println("Fin de chargement des paramètres initiaux dans la servlet : "+getClass().getName());
}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
private void performCreer(javax.servlet.http.HttpServletRequest request) throws Exception {

	ImageDistillerBean bean = (ImageDistillerBean)request.getSession().getAttribute("imageDistillerBean");
	
	String repOrg = request.getParameter("REPORG");
	String repDest = request.getParameter("REPDEST");

	bean.setRepOrg(repOrg);
	bean.setRepDest(repDest);
	
	//Si dossier d'origine pas saisi
	if (repOrg == null || repOrg.length() == 0) {
		bean.setMessageErreur("Saisir le répertoire d'origine");
		return;
	}
	//Si dossier destination pas saisi
	if (repDest == null || repDest.length() == 0) {
		bean.setMessageErreur("Saisir le répertoire de destination");
		return;
	}

	//Si dossier innaccessible
	if (! new java.io.File(repOrg).isDirectory()) {
		bean.setMessageErreur("ImageDistiller : le dossier d'origine "+repOrg+" est inexistant ou innaccessible");
		return;
	}

	//Si dossier innaccessible
	if (! new java.io.File(repDest).isDirectory()) {
		bean.setMessageErreur("ImageDistiller : le dossier de destination "+repDest+" est inexistant ou innaccessible");
		return;
	}

	ImageDistiller.getInstance().ajouteDossier(repOrg, repDest);
	
}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
private void performDistiller(javax.servlet.http.HttpServletRequest request) throws Exception {

	//si arrêté, on le lance, sinon on l'arrête
	if (ImageDistiller.getInstance().estArrete()) {
		ImageDistiller.getInstance().demarrer();
	} else {
		ImageDistiller.getInstance().arreter();
	}
	
}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
private void performSupprimer(javax.servlet.http.HttpServletRequest request) throws Exception {

	ImageDistillerBean bean = (ImageDistillerBean)request.getSession().getAttribute("imageDistillerBean");
	
	String repOrg = request.getParameter("LISTE");
	if (repOrg == null) {
		bean.setMessageErreur("Il faut sélectionner une ligne");
		return;
	}

	ImageDistiller.getInstance().enleveDossier(repOrg);
	
}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void performTask(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {

	try	{

		//Verif si habill ok
		if (!controlerHabilitation(request)) {
			javax.servlet.ServletContext sc= getServletContext();
			javax.servlet.RequestDispatcher rd = sc.getRequestDispatcher("/ImageDistiller.jsp");
			rd.forward(request,response);
			return;
		}

		
		//si rien dans la session
		if (request.getSession().getAttribute("imageDistillerBean") == null) {
			ImageDistillerBean bean = new ImageDistillerBean();
			request.getSession().setAttribute("imageDistillerBean", bean);
		}
		
		ImageDistillerBean bean = (ImageDistillerBean) request.getSession().getAttribute("imageDistillerBean");
		bean.setMessageErreur("");
		
		// Insert user code from here.
		if (request.getParameter("SUPPRIMER") != null) {
			performSupprimer(request);
		} else if (request.getParameter("CREER") != null) {
			performCreer(request);
		} else if (request.getParameter("DISTILLER") != null) {
			performDistiller(request);
		}
		
		//On forwarde la JSP du process en cours
		javax.servlet.ServletContext sc= getServletContext();
		javax.servlet.RequestDispatcher rd = null;
		rd = sc.getRequestDispatcher("/ImageDistiller.jsp");
		rd.forward(request, response);
		
	}
	catch(Throwable theException)
	{
		// uncomment the following line when unexpected exceptions
		// are occuring to aid in debugging the problem.
		//theException.printStackTrace();
	}
}
}
