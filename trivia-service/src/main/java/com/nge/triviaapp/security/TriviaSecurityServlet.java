package com.nge.triviaapp.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.security.DeclareRoles;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

//@FormAuthenticationMechanismDefinition(
//	loginToContinue = @LoginToContinue(
//		loginPage = "login.html",
//		errorPage = "login-error.html")
//)
//@BasicAuthenticationMechanismDefinition(realmName="TriviaApp")
@WebServlet(value="/login", loadOnStartup=1)
//@DeclareRoles({"host", "admin", "contestant"})
//@ServletSecurity(@HttpConstraint(transportGuarantee=TransportGuarantee.NONE, rolesAllowed={SecurityRoles.HOST_ROLE, SecurityRoles.ADMIN_ROLE, SecurityRoles.CONTESTANT_ROLE}))
public class TriviaSecurityServlet extends HttpServlet {
	
	@Inject
	private SecurityContext context;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter pw = resp.getWriter();
		pw.write("Hello! " + req.getUserPrincipal().getName());
		pw.flush();
	}
}