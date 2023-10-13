package com.alexrosa.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alexrosa.todolist.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository iuserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("--------> chegou no filtro");


        var serveltPath = request.getServletPath();
        System.out.println(serveltPath.trim());

        if (serveltPath.startsWith("/tasks/")) {
            // Pegar usuario e senha e // validar user e senha

            // Get auth info...
            var auth = request.getHeader("Authorization");
            System.out.println("--> auth" + auth);

            //remove basic do inicio do auth
            var authEncoded = auth.substring("Basic".length(), auth.length()).trim();
            //decode from base64 to byte
            byte[] authDecoded = Base64.getUrlDecoder().decode(authEncoded);
            System.out.println(authDecoded);

            //convert byte[] to string
            var authString = new String(authDecoded);
            System.out.println(authString);

            //split string to get credentials
            var credentials = authString.split(":");
            System.out.println(credentials[0]);
            System.out.println(credentials[1]);

            String username = credentials[0];
            String password = credentials[1];
            
            //se user existir 
            var user = this.iuserRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401, "test1");
            } else {
                //valida senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    //recupera Id do usuario autenticado e passa para o controler
                    request.setAttribute("IdUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401, "test2");
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }

        
    }

}
