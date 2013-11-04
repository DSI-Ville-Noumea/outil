<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<link rel="stylesheet" href="theme/sigp2.css" type="text/css">
<TITLE>Gestion du distiller</TITLE>

<!--SCRIPT language="javascript" src="file:///X:/TestLuc/js/GestionBoutonDroit.js"></SCRIPT--> 

<%@page contentType="text/html;charset=UTF-8"%>
</HEAD>
<%
	if (!nc.mairie.outils.distiller.ImageDistillerServlet.controlerHabilitation(request)) {
		response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED); 
		response.setHeader("WWW-Authenticate","BASIC realm=\"Habilitation HTTP pour le Distiller\"");

		response.setContentType("text/html");
		out.println("Vous n'êtes pas habilité...");

		return;
	}
%>

<BODY bgcolor="#ebebeb" lang="FR" link="blue" vlink="purple" class="sigp2-BODY">
<jsp:useBean class="nc.mairie.outils.distiller.ImageDistillerBean" id="imageDistillerBean" scope="session"></jsp:useBean>
<P><%=imageDistillerBean.getMessageErreur()%></P>
<TABLE border="0" width="580" style="text-align : center;">
  <TBODY align="center">
    <TR>
      <TD style="text-align : center;">
      <FORM name="formu" action="ImageDistillerServlet" method="POST" class="sigp2-titre">Gestion des Dossiers de Distiller<BR>
      <BR>
      <TABLE width="500" border="0" cellpadding="0" cellspacing="0">
        <TBODY>
          <TR>
            <TD>
            <FIELDSET style="text-align : center;"><BR>
            <TABLE border="0" cellpadding="0" cellspacing="0">
              <TBODY>
                <TR>
                  <TD style="text-align : center;" width="100" height="155" align="center">
                  <TABLE border="0" class="sigp2" cellpadding="0" cellspacing="0" width="100%">
                    <TBODY>
                      <TR>
                        <TD class="sigp2-titre" nowrap style="text-align : center;">Liste des Dossiers</TD>
                      </TR>
                      <TR>
                        <TD style="text-align : center;"><%--METADATA type="DynamicData" startspan
<SELECT size="6" style="width : 400px;font-family : monospace;" class="sigp2-liste" name="LISTE" loopproperty="imageDistillerBean.LB_DOSSIER[]" itemproperty="imageDistillerBean.LB_DOSSIER[]" dynamicelement varprefix="popo" valueproperty="imageDistillerBean.LB_DOSSIER_CLE[]">
                          <OPTION selected>c:\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;d:\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</OPTION>
                        </SELECT>
--%><%
try {
  Object[] popo_a0 = imageDistillerBean.getLB_DOSSIER();
  Object popo_p0 = popo_a0[0]; // throws an exception if empty.
  Object[] popo_a0_0 = imageDistillerBean.getLB_DOSSIER_CLE();
  Object popo_p0_0 = popo_a0_0[0];
  %><SELECT class="sigp2-liste" name="LISTE" size="6" style="width : 400px;font-family : monospace;"><%
    for (int popo_i0 = 0; ; ) { %>
      <OPTION value="<%= popo_p0_0 %>"><%= popo_p0 %></OPTION><%
      popo_i0++;
      try {
        popo_p0 = popo_a0[popo_i0];
        popo_p0_0 = popo_a0_0[popo_i0];
      }
      catch (java.lang.ArrayIndexOutOfBoundsException popo_e0) {
        break;
      }
    } %>
  </SELECT><%
}
catch (java.lang.ArrayIndexOutOfBoundsException popo_e0) {
} %><%--METADATA type="DynamicData" endspan--%><BR>
                        <BR>
                        <INPUT type="submit" class="sigp2-Bouton-100" value="Supprimer" name="SUPPRIMER"><BR>
                        <BR>
                        </TD>
                      </TR>
                      <TR>
                        <TD>
                        <FIELDSET class="sigp2Fieldset" style="text-align : center;"><BR>
                        <TABLE border="0" cellpadding="0" cellspacing="0">
                          <TBODY>
                            <TR>
                              <TD class="sigp2" width="124" nowrap>Dossier source</TD>
                              <TD width="150"><%--METADATA type="DynamicData" startspan
<INPUT name="REPORG" type="text" class="sigp2-saisie" size="20" maxlength="100" valueproperty="imageDistillerBean.repOrg" dynamicelement>
--%><INPUT class="sigp2-saisie" maxlength="100" name="REPORG" size="20" type="text" value="<%= imageDistillerBean.getRepOrg() %>"><%--METADATA type="DynamicData" endspan--%></TD>
                            </TR>
                            <TR>
                              <TD class="sigp2" width="124" nowrap>Dossier destination</TD>
                              <TD width="150"><%--METADATA type="DynamicData" startspan
<INPUT size="20" type="text" maxlength="100" class="sigp2-saisie" name="REPDEST" valueproperty="imageDistillerBean.repDest" dynamicelement>
--%><INPUT class="sigp2-saisie" maxlength="100" name="REPDEST" size="20" type="text" value="<%= imageDistillerBean.getRepDest() %>"><%--METADATA type="DynamicData" endspan--%></TD>
                            </TR>
                            <TR>
                              <TD colspan="2" align="center"><BR>
                              <TABLE border="0" cellpadding="0" cellspacing="0">
                                <TBODY>
                                  <TR>
                                    <TD></TD>
                                    <TD width="15">&nbsp;</TD>
                                    <TD><INPUT type="submit" class="sigp2-Bouton-100" value="Créer" name="CREER"></TD>
                                    <TD width="15">&nbsp;</TD>
                                    <TD></TD>
                                  </TR>
                                </TBODY>
                              </TABLE>
                              <BR>
                              </TD>
                            </TR>
                          </TBODY>
                        </TABLE>
                        </FIELDSET>
                        <BR>
                        </TD>
                      </TR>
                      <TR>
                        <TD>
                        <FIELDSET style="text-align : center;">
                        <TABLE border="0" cellpadding="0" cellspacing="0">
                          <TBODY>
                            <TR>
                              <TD class="sigp2">
                        Etat du distiller : <B><%=imageDistillerBean.getEtat()%></B> </TD>
                              <TD width="120" align="right"><INPUT type="submit" name="DISTILLER" value='<%=imageDistillerBean.getEtat().equals("démarré") ? "Arreter" : "Demarrer"%>' class="sigp2-Bouton-100"></TD>
                            </TR>
                          </TBODY>
                        </TABLE>
                        </FIELDSET>
                        </TD>
                      </TR>
                    </TBODY>
            </TABLE>
                  <BR>
                  </TD>
                </TR>
              </TBODY>
            </TABLE>
            </FIELDSET>
            </TD>
          </TR>
        </TBODY>
      </TABLE>
     </FORM>
      </TD>
    </TR>
  </TBODY>
</TABLE>
<FORM>
</FORM>
</BODY>
</HTML>