<%@page contentType="text/plain;charset=UTF-8"%>
<%@page import="java.net.InetAddress"%>
${project.artifactId}.version=${project.version}
${project.artifactId}.localhost.hostaddress=<%=InetAddress.getLocalHost().getHostAddress() %>
${project.artifactId}.localhost.canonicalhostname=<%=InetAddress.getLocalHost().getCanonicalHostName() %>
${project.artifactId}.localhost.hostname=<%=InetAddress.getLocalHost().getHostName() %>
<% 
HttpSession theSession = request.getSession( false );

// print out the session id
try {
	if( theSession != null ) {
	  //pw.println( "<BR>Session Id: " + theSession.getId() );
	  synchronized( theSession ) {
	    // invalidating a session destroys it
	    theSession.invalidate();
	    //pw.println( "<BR>Session destroyed" );
	  }
	}
} catch (Exception e) {
//Bhen on s'en fou
}
%>
	
