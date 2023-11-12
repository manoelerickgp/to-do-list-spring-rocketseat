package br.com.manoelerick.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.manoelerick.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    private final IUserRepository userRepository;

    public FilterTaskAuth(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if (servletPath.equals("/tasks/")) {
            // etapa 1: Pegar a autenticação (usuário e senha)
            var authorization = request.getHeader("Authorization");

            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecode = Base64.getDecoder().decode(authEncoded);

            var authString = new String(authDecode);
            // ["orion", "1234"]
            String[] credentials = authString.split(":");
            String userName = credentials[0];
            String password = credentials[1];

            // etapa 2: depois irá validar o usuário (verificar se o usuário existe)
            var user = this.userRepository.findByUserName(userName);
            if (user == null) {
                response.sendError(401, "Unauthorized user");
            } else {
                System.out.println("Authorized user!");
                // etapa 3: depois irá 'validar' a senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    filterChain.doFilter(request, response);
                } else  {
                    response.sendError(401, "Wrong password!");
                }
                // caso haja algum erro nessas 3 etapas, será retornado um erro 'usuário não tem permissão'

                // caso contrário, a aplicação continua

            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
