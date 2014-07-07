/*
 * Created on 20 juil. 2006
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nc.mairie.exportfile.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.mairie.technique.Transaction;

/**
 * @author boulu72
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportFileServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1504718999650362299L;
	
	private static Logger logger = Logger.getLogger(ExportFileServlet.class.getName());
	
	/**
	 * 
	 */
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		//super.doGet(arg0, arg1);
		performTask(arg0, arg1);
	}
	/**
	 * 
	 */
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		//super.doPost(arg0, arg1);
		performTask(arg0, arg1);
	}
	
	/**
	 * 
	 * @param request request
	 * @param response response
	 * @throws ServletException ServletException
	 * @throws IOException IOException 
	 */
	protected void performTask(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		String theFile = request.getParameter("file");
		String theTTO = request.getParameter("nomTTO");
		
		//Si file null alors message d'erreur
		if (theFile == null && theTTO == null) {
			afficheMessage(response,"Le paramètre file ou nomTTO dans l'URL est manquant. Veuillez contacter l'administrateur.", false);
		//Si nom TTO et File sont la alors PB	
		} else if (theFile != null && theTTO != null) {
			afficheMessage(response,"Les paramètres file et nomTTO ne peuvent être renseignés en même temps. Veuillez contacter l'administrateur.", false);
		//Le paramètre passe n'est pas renseigné
		} else if (request.getParameter("passe") == null) {
			affichePageTelechargement(request, response, theTTO != null ? "TTO "+theTTO : "fichier "+theFile);			
		} else {
			if (theTTO != null) {
				recupereFichierTTO(request, response, theTTO);
				logger.info("Transfert TTO de "+theTTO+" effectué par "+request.getRemoteHost());
			} else {
				recupereFichier(request, response, theFile);
				logger.info("Transfert du fichier "+theFile+" effectué par "+request.getRemoteHost());
			}
		}
		
}
	/**
	 * Insérez la description de la méthode à cet endroit.
	 *  Date de création : (22/02/2002 10:51:46)
	 * @param request
	 * @param response
	 * @param theFile fichier
	 * @throws IOException
	 */
	private void recupereFichier(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, String theFile) throws IOException{
		try{
			theFile.replace('/','.');
			
			String admin = (String)getParametres().get("HOST_SGBD_ADMIN");
			String pwd = (String)getParametres().get("HOST_SGBD_PWD");
			String serveur =(String)getParametres().get("HOST_SGBD");
			
			nc.mairie.technique.UserAppli aUser = new nc.mairie.technique.UserAppli(admin, pwd, serveur);
			
			Transaction t = new Transaction(aUser);
			java.sql.Connection conn = t.getConnection();;
			java.sql.Statement st = null;
			java.sql.ResultSet rs = null;
		
			try {
				st = conn.createStatement();
				rs = st.executeQuery("select * from "+theFile);
				
				String ext = request.getParameter("ext") == null ? "" : "."+request.getParameter("ext");
				response.setContentType("application/x-msdownload");
				response.addHeader("Content-Disposition", "attachment; filename="+theFile+ext);
				
				
				String temp = null;
				while (rs.next()) {
					for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
						
						try { temp = rs.getString(i+1);} catch (Exception e) {
							temp = "null";}
						response.getWriter().print(temp);
					}
					response.getWriter().println();
				}
				
				t.commitTransaction();
			} catch (Exception e) {
				t.rollbackTransaction();
				t.fermerConnexion();
				e.printStackTrace();
				throw e;
			}	
			
			rs.close();
			st.close();
			t.fermerConnexion();
			conn.close();
		} catch (Exception e) {
			afficheMessage(response, "Exception interceptée lors de la récupération du fichier\n"+e.getMessage(), false);
			e.printStackTrace();
		}
	}
	/**
	 * Insérez la description de la méthode à cet endroit.
	 *  Date de création : (22/02/2002 10:51:46)
	 * @param request
	 * @param response
	 * @param theTTO nom du TTO
	 * @throws IOException
	 */
	private void recupereFichierTTO(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, String theTTO) throws IOException{
		try{
			
			String admin = (String)getParametres().get("HOST_SGBD_ADMIN");
			String pwd = (String)getParametres().get("HOST_SGBD_PWD");
			String serveur =(String)getParametres().get("HOST_SGBD");
			
			nc.mairie.technique.UserAppli aUser = new nc.mairie.technique.UserAppli(admin, pwd, serveur);
			
			Transaction t = new Transaction(aUser);
			java.sql.Connection conn = t.getConnection();;
			java.sql.Statement st = null;
			java.sql.ResultSet rs = null;
		
			//on récupère la requete SQL
			try {
				st = conn.createStatement();
				rs = st.executeQuery("select * from mairie.exportfile where nomtto = '"+theTTO+"'");
			
				//si pas trouvé d'éléments
				if (!rs.next()) {
					afficheMessage(response, "La requète pour le TTO "+theTTO+" est introuvable.",false);
					t.rollbackTransaction();
					t.fermerConnexion();
					return;
				}
				
				//recup des infos
				String theRequete = rs.getString("requete");
				String theFile = rs.getString("nomfichier") == null ? theTTO : rs.getString("nomfichier");
				String theExt = rs.getString("extfichier") == null ? "" : "."+rs.getString("extfichier");
				String theEntete = rs.getString("entete") == null ? "" : rs.getString("entete");
			
				//on exécute la requete
				rs = st.executeQuery(theRequete);
				
				
				response.setContentType("application/x-msdownload");
				response.addHeader("Content-Disposition", "attachment; filename="+theFile+theExt);

				//Si on veut l'entête
				if ("O".equals(theEntete.toUpperCase())) {
				
					String temp = "";
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						
						//si dernière colonne, alors on trime
						if (i == rs.getMetaData().getColumnCount())
							temp += rs.getMetaData().getColumnName(i);
						else {
							temp+=ajouteChamp(rs.getMetaData().getColumnName(i),theExt);
						}
					}
					response.getWriter().println(temp);
				}
				
				//On met toutes les lignes
				String temp = null;
				while (rs.next()) {
					temp="";
					for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
						
						try {
							//Récup de la zone
							String type = rs.getMetaData().getColumnTypeName(i+1);
							String zone = rs.getString(i+1);
							//si dernière colonne alors on trime
							if (i == rs.getMetaData().getColumnCount()-1) zone = zone.trim();
						
							int nbcar = rs.getMetaData().getPrecision(i+1);
							
							//si décimal alors on rajoute un caractère (le signe) et remplacement de la virgule
							if (!"CHAR".equals(type) && !"VARCHAR".equals(type)) {
								//On remplace le point par la virgule
								zone=zone.replace('.',',');
								
								nbcar++;
								//s'il y a des sécimales, alors encore 1
								if (rs.getMetaData().getScale(i+1) > 0) {
									nbcar++;
								}
							}
							
							//on met les blancs devant si pas la dernière zone
							boolean isChar = "CHAR".equals(type) || "VARCHAR".equals(type);
							while (zone.length() < nbcar) {
								if (isChar) zone = zone + " ";
								else	zone = " "+zone;
							}
							
							//si dernière colonne, alors on colle
							if (i == rs.getMetaData().getColumnCount()-1) {
								temp += zone;
							} else {
								temp += ajouteChamp(zone,theExt);
							}
								
						}catch (Exception e) {temp += "null";}
					}
					response.getWriter().println(temp);
				}
				//on ajoute la fin du fichier
				ajouteFin(response, theExt);
				
				t.commitTransaction();
			} catch (Exception e) {
				t.rollbackTransaction();
				t.fermerConnexion();
				e.printStackTrace();
				throw e;
			}	
			
			rs.close();
			st.close();
			t.fermerConnexion();
			conn.close();
		} catch (Exception e) {
			afficheMessage(response, "Exception interceptée lors de la récupération du fichier\n"+e.getMessage(), false);
			e.printStackTrace();
		}
	}
	/**
	 * Insérez la description de la méthode à cet endroit.
	 *  Date de création : (22/02/2002 10:51:46)
	 * @param champ
	 * @param theExt
	 * @return
	 */
	private String ajouteChamp(String champ, String theExt){
		
		//Si c'est du CSV alors on rajoute les ; entre chaque colonne
		if (".CSV".equals(theExt.toUpperCase())) {
			return champ+";";
		} else if (".XLS".equals(theExt.toUpperCase())) {
			return champ+";";
		} else{
			return champ;
		}
	}
	/**
	 * Insérez la description de la méthode à cet endroit.
	 *  Date de création : (22/02/2002 10:51:46)
	 * @param response
	 * @param theExt
	 * @throws IOException
	 */
	private void ajouteFin(HttpServletResponse response, String theExt) throws IOException{
		
		//Si c'est du CSV alors on ne et rien à la fin
		if (".CSV".equals(theExt.toUpperCase())) {
			return;
		} else {
			response.getWriter().print((char)0x1A);
		}
	}
	/**
	 * Cette méthode a été importée à partir d'un fichier .class.
	 * Aucun code source disponible.
	 */
	public void init() throws javax.servlet.ServletException {
		// insert code to initialize the servlet here
		initialiseParametreInitiaux();
	
	}
	/**
	 * Insérez la description de la méthode à cet endroit.
	 *  Date de création : (22/02/2002 10:51:46)
	 */
	private void initialiseParametreInitiaux() {
	
		boolean doitPrendreInit = getServletContext().getInitParameterNames().hasMoreElements();
	
		logger.info("Chargement des paramètres initiaux dans la servlet : "+getClass().getName());
		if (getParametres().size() == 0) {
			//chargement des paramêtres du contexte
			Enumeration<?> enumContext = doitPrendreInit ? getServletContext().getInitParameterNames() : getServletContext().getAttributeNames();
			while (enumContext.hasMoreElements()) {
				try {
					String cleParametre = (String)enumContext.nextElement();
					if (cleParametre != null && ! cleParametre.startsWith("com.ibm.websphere") ) {
						String valParametre = doitPrendreInit ? (String)getServletContext().getInitParameter(cleParametre) : (String)getServletContext().getAttribute(cleParametre);
						getParametres().put(cleParametre,valParametre);
						logger.info("Chargement de la clé : "+cleParametre+" avec "+valParametre);
					}
				} catch (Exception e) {
					continue;
				}
			}
		
			//chargement des param de la servlet
			Enumeration<?> enumServlet = getInitParameterNames();
			while (enumServlet.hasMoreElements()) {
				String cleParametre = (String)enumServlet.nextElement();
				String valParametre = (String)getInitParameter(cleParametre);
				getParametres().put(cleParametre,valParametre);
				logger.info("Chargement de la clé : "+cleParametre+" avec "+valParametre);
			}
		}
		logger.info("Fin de chargement des paramètres initiaux dans la servlet : "+getClass().getName());
	}
	private static Hashtable <String,String> parametres;
	/**
	 * Returns the servlet info string.
	 * @author Luc Bourdil
	 * @return Hashtable Hashtable
	 */
	public static Hashtable<String, String> getParametres() {
		if (parametres == null) {
			parametres = new Hashtable<String, String>();
		}
		return parametres;
	}
	
	/**
	 * Insérez la description de la méthode à cet endroit.
	 *  Date de création : (22/02/2002 10:51:46)
	 * @param request
	 * @param response
	 * @param theFile
	 * @throws IOException
	 */
	private void affichePageTelechargement(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, String theFile) throws IOException{

		String params = "?passe=1";
		for (Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();){
			String cle = (String)e.nextElement();
			params+="&"+cle+"="+request.getParameter(cle);
		}
		
		response.getWriter().println("<html>");
		response.getWriter().println("<head>");
		response.getWriter().println("<title>Téléchargement du fichier</title>");
		response.getWriter().println("</head>");
		response.getWriter().println("<body>");
		afficheMessage(response,"Chargement du "+theFile+" en cours. Veuillez patienter...", true);
		response.getWriter().println("<script language=\"JavaScript\">");
		response.getWriter().println("function go() {");
		response.getWriter().println("window.moveTo(-10000,-10000);");
		response.getWriter().println("setInterval(\"ferme()\", 15000); ");
		response.getWriter().println("}");
		response.getWriter().println("function ferme() {");
		response.getWriter().println("opener=self;");
		response.getWriter().println("window.opener='X';");
		response.getWriter().println("window.open('','_parent','');");
		response.getWriter().println("window.close();");
		response.getWriter().println("}");
		
		
		response.getWriter().println("window.document.location.href=\"ExportFileServlet" + params +"\";");
		//response.getWriter().println("window.navigate('ExportFileServlet" + params +"');");

		response.getWriter().println("setTimeout (\"go()\", 2000 );"); 
		response.getWriter().println("</script></body>");

	
	}
	/**
	 * Insérez la description de la méthode à cet endroit.
	 *  Date de création : (22/02/2002 10:51:46)
	 * @param response
	 * @param message
	 * @param info
	 * @throws IOException
	 */
	private void afficheMessage(HttpServletResponse response, String message, boolean info) throws IOException{

		response.getWriter().println("<html>");
		response.getWriter().println("<body>");
		response.getWriter().println("<p style=\"font-family: Verdana, Arial, Helvetica, sans-serif;color:"+ (info ? "black":"red")+";	font-weight: bold;	font-size: 16px;	text-decoration: blink;	border: 1px dotted #999999;	padding: 5px;	margin: 5px;	position: relative;\">");
		response.getWriter().println(message);
		response.getWriter().println("</p>");
		response.getWriter().println("</body>");
		response.getWriter().println("</html>");
		
	}
	
}
