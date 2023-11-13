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

        /* O 'equals' do 'servletPath' diz que o caminho informado precisa ser igual a '/tasks/' para proseguir com
        * a requisição, porém, por se tratar de uma atualização, é necessário informar o id do recurso a ser atualizado
        * e por esta razão, o 'equals' não é apropriado para este tipo de operação. Para resolver este problema,
        * o uso de 'startsWith' será o ideal, pois ele além de reconhecer paths com somente o '/tasks/' também irá
        * reconhecer caminhos adicionais como por exemplo, um 'id' - http://localhost:8080/tasks/id*/

        if (servletPath.startsWith("/tasks/")) {

            // etapa 1: Pegar a autenticação (usuário e senha)
            var authorization = request.getHeader("Authorization");

            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecode = Base64.getDecoder().decode(authEncoded);

            var authString = new String(authDecode);
            // ["orion", "124"]
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
                    // continuação da aplicação
                    request.setAttribute("idUser", user.getId());
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
