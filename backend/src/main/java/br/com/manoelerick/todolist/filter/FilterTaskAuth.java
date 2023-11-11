package br.com.manoelerick.todolist.filter;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FilterTaskAuth implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // doFilter executa alguma ação
        System.out.println("Chegou no doFilter!");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
