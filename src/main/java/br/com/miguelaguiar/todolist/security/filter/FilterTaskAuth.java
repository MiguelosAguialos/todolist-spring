package br.com.miguelaguiar.todolist.security.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.miguelaguiar.todolist.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if(servletPath.contains("/tasks/")){

        var authorization = request.getHeader("Authorization");
        byte[] authDecode = Base64.getDecoder().decode(authorization.substring("Basic".length()).trim());
        var authString = new String(authDecode);
        String[] credentials = authString.split(":");
        String username = credentials[0];
        String password = credentials[1];

        var user = this.userRepository.findByUsername(username);
        if(user == null){
            response.sendError(401, "Usuário não autorizado");
        } else {
            var verified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;
            if(verified){
                request.setAttribute("idUser", user.getId());
                filterChain.doFilter(request, response);
            } else {
                response.sendError(401, "Usuário não autorizado");
            }
        }
    } else {
            filterChain.doFilter(request, response);
        }
    }
}
