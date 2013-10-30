/*
 * Created on 20 juil. 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nc.mairie.exportfile.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.mairie.technique.Transaction;

/**
 * @author boulu72
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportFileServlet extends HttpServlet {

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(arg0, arg1);
		performTask(arg0, arg1);
	}
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doPost(arg0, arg1);
		performTask(arg0, arg1);
	}
	
	protected void performTask(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		String theFile = request.getParameter("file");
		String theTTO = request.getParameter("nomTTO");
		
		//Si file null alors message d'erreur
		if (theFile == null && theTTO == null) {
			afficheMessage(response,"Le param�tre file ou nomTTO dans l'URL est manquant. Veuillez contacter l'administrateur.", false);
		//Si nom TTO et File sont la alors PB	
		} else if (theFile != null && theTTO != null) {
			afficheMessage(response,"Les param�tres file et nomTTO ne peuvent �tre renseign�s en m�me temps. Veuillez contacter l'administrateur.", false);
		//Le param�tre passe n'est pas renseign�
		} else if (request.getParameter("passe") == null) {
			affichePageTelechargement(request, response, theTTO != null ? "TTO "+theTTO : "fichier "+theFile);			
		} else {
			if (theTTO != null) {
				recupereFichierTTO(request, response, theTTO);
				System.out.println("ExportFileServlet : Transfert TTO de "+theTTO+" effectu� par "+request.getRemoteHost());
			} else {
				recupereFichier(request, response, theFile);
				System.out.println("ExportFileServlet : Transfert du fichier "+theFile+" effectu� par "+request.getRemoteHost());
			}
		}
		
}
	/**
	 * Ins�rez la description de la m�thode � cet endroit.
	 *  Date de cr�ation : (22/02/2002 10:51:46)
	 * @return fr.averse.servlets.Contexte
	 */
	private void recupereFichier(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, String theFile) throws IOException{
		try{
			String result="";
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
			afficheMessage(response, "Exception intercept�e lors de la r�cup�ration du fichier\n"+e.getMessage(), false);
			e.printStackTrace();
		}
	}
	/**
	 * Ins�rez la description de la m�thode � cet endroit.
	 *  Date de cr�ation : (22/02/2002 10:51:46)
	 * @return fr.averse.servlets.Contexte
	 */
	private void recupereFichierTTO(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, String theTTO) throws IOException{
		try{
			String result="";
			
			String admin = (String)getParametres().get("HOST_SGBD_ADMIN");
			String pwd = (String)getParametres().get("HOST_SGBD_PWD");
			String serveur =(String)getParametres().get("HOST_SGBD");
			
			nc.mairie.technique.UserAppli aUser = new nc.mairie.technique.UserAppli(admin, pwd, serveur);
			
			Transaction t = new Transaction(aUser);
			java.sql.Connection conn = t.getConnection();;
			java.sql.Statement st = null;
			java.sql.ResultSet rs = null;
		
			//on r�cup�re la requete SQL
			try {
				st = conn.createStatement();
				rs = st.executeQuery("select * from mairie.exportfile where nomtto = '"+theTTO+"'");
			
				//si pas trouv� d'�l�ments
				if (!rs.next()) {
					afficheMessage(response, "La requ�te pour le TTO "+theTTO+" est introuvable.",false);
					t.rollbackTransaction();
					t.fermerConnexion();
					return;
				}
				
				//recup des infos
				String theRequete = rs.getString("requete");
				String theFile = rs.getString("nomfichier") == null ? theTTO : rs.getString("nomfichier");
				String theExt = rs.getString("extfichier") == null ? "" : "."+rs.getString("extfichier");
				String theEntete = rs.getString("entete") == null ? "" : rs.getString("entete");
			
				//on ex�cute la requete
				rs = st.executeQuery(theRequete);
				
				
				response.setContentType("application/x-msdownload");
				response.addHeader("Content-Disposition", "attachment; filename="+theFile+theExt);

				//Si on veut l'ent�te
				if ("O".equals(theEntete.toUpperCase())) {
				
					String temp = "";
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						
						//si derni�re colonne, alors on trime
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
							//R�cup de la zone
							String type = rs.getMetaData().getColumnTypeName(i+1);
							String zone = rs.getString(i+1);
							//si derni�re colonne alors on trime
							if (i == rs.getMetaData().getColumnCount()-1) zone = zone.trim();
						
							int nbcar = rs.getMetaData().getPrecision(i+1);
							
							//si d�cimal alors on rajoute un caract�re (le signe) et remplacement de la virgule
							if (!"CHAR".equals(type) && !"VARCHAR".equals(type)) {
								//On remplace le point par la virgule
								zone=zone.replace('.',',');
								
								nbcar++;
								//s'il y a des s�cimales, alors encore 1
								if (rs.getMetaData().getScale(i+1) > 0) {
									nbcar++;
								}
							}
							
							//on met les blancs devant si pas la derni�re zone
							boolean isChar = "CHAR".equals(type) || "VARCHAR".equals(type);
							while (zone.length() < nbcar) {
								if (isChar) zone = zone + " ";
								else	zone = " "+zone;
							}
							
							//si derni�re colonne, alors on colle
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
			afficheMessage(response, "Exception intercept�e lors de la r�cup�ration du fichier\n"+e.getMessage(), false);
			e.printStackTrace();
		}
	}
	/**
	 * Ins�rez la description de la m�thode � cet endroit.
	 *  Date de cr�ation : (22/02/2002 10:51:46)
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
	 * Ins�rez la description de la m�thode � cet endroit.
	 *  Date de cr�ation : (22/02/2002 10:51:46)
	 */
	private void ajouteFin(HttpServletResponse response, String theExt) throws IOException{
		
		//Si c'est du CSV alors on ne et rien � la fin
		if (".CSV".equals(theExt.toUpperCase())) {
			return;
		} else {
			response.getWriter().print((char)0x1A);
		}
	}
	/**
	 * Cette m�thode a �t� import�e � partir d'un fichier .class.
	 * Aucun code source disponible.
	 */
	public void init() throws javax.servlet.ServletException {
		// insert code to initialize the servlet here
		initialiseParametreInitiaux();
	
	}
	/**
	 * Ins�rez la description de la m�thode � cet endroit.
	 *  Date de cr�ation : (22/02/2002 10:51:46)
	 * @return fr.averse.servlets.Contexte
	 */
	private void initialiseParametreInitiaux() {
	
		boolean doitPrendreInit = getServletContext().getInitParameterNames().hasMoreElements();
	
		System.out.println("Chargement des param�tres initiaux dans la servlet : "+getClass().getName());
		if (getParametres().size() == 0) {
			//chargement des param�tres du contexte
			java.util.Enumeration enumContext = doitPrendreInit ? getServletContext().getInitParameterNames() : getServletContext().getAttributeNames();
			while (enumContext.hasMoreElements()) {
				try {
					String cleParametre = (String)enumContext.nextElement();
					if (cleParametre != null && ! cleParametre.startsWith("com.ibm.websphere") ) {
						String valParametre = doitPrendreInit ? (String)getServletContext().getInitParameter(cleParametre) : (String)getServletContext().getAttribute(cleParametre);
						getParametres().put(cleParametre,valParametre);
						System.out.println("Chargement de la cl� : "+cleParametre+" avec "+valParametre);
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
				System.out.println("Chargement de la cl� : "+cleParametre+" avec "+valParametre);
			}
		}
		System.out.println("Fin de chargement des param�tres initiaux dans la servlet : "+getClass().getName());
	}
	private static java.util.Hashtable parametres;
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
	 * Ins�rez la description de la m�thode � cet endroit.
	 *  Date de cr�ation : (22/02/2002 10:51:46)
	 * @return fr.averse.servlets.Contexte
	 */
	private void affichePageTelechargement(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, String theFile) throws IOException{

		String params = "?passe=1";
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();){
			String cle = (String)e.nextElement();
			params+="&"+cle+"="+request.getParameter(cle);
		}
		
		response.getWriter().println("<html>");
		response.getWriter().println("<head>");
		response.getWriter().println("<title>T�l�chargement du fichier</title>");
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
		//response.getWriter().println("window.document.location.href=\"ExportFileServlet" + params +"\";");
		response.getWriter().println("window.navigate('ExportFileServlet" + params +"');");
//		response.getWriter().println("opener=self;");
		response.getWriter().println("setTimeout (\"go()\", 2000 );"); 
		response.getWriter().println("</script></body>");

	
	}
	/**
	 * Ins�rez la description de la m�thode � cet endroit.
	 *  Date de cr�ation : (22/02/2002 10:51:46)
	 * @return fr.averse.servlets.Contexte
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
